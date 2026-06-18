package ast;
import java.util.ArrayList;
import java.util.HashMap;

import evaluator.HPCLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class FunNode implements Node {
	private final String id;
	private final Type returntype ;
	private final ArrayList<ParNode> parlist ;
	private final ArrayList<Node> declist ;
	private final ArrayList<Node> stmlist ;
	private final Node body ;
	private ArrowType type ;
	private int nesting ;
	private String flabel ;
  
	public FunNode (String _id, Type _type, ArrayList<ParNode> _parlist, ArrayList<Node> _declist, ArrayList<Node> _stmlist, Node _body) {
		id = _id ;
		returntype = _type;
		parlist = _parlist ;
		declist = _declist ;
		stmlist = _stmlist ;
		body = _body ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {

		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		
		if (ST.top_lookup(id))
			errors.add(new SemanticError("Fun " + id + " already declared"));
		else {
			ArrayList<Type> partypes = new ArrayList<Type>() ;

			for (ParNode arg : parlist)
				partypes.add(arg.getType());

			type = new ArrowType(partypes, returntype) ;
			flabel = HPCLanlib.freshFunLabel() ;
			ST.insert(id, type, nesting, flabel) ;

			HashMap<String,STentry> HM = new HashMap<String, STentry>();
			ST.add(HM);
			HashMap<String,Integer> cons = new HashMap<String, Integer>();
			CT.add(cons);

			for (ParNode arg : parlist){
				if (HM.containsKey(arg.getId()))
					errors.add(new SemanticError("Parameter id " + arg.getId() + " already declared")) ;
				else {
					ST.insert(arg.getId(), arg.getType(), nesting+1, "") ;
				}
			}

			ST.increaseoffset() ; // aumentiamo di 1 l'offset per far posto al return value

			for (Node dec : declist)
				errors.addAll(dec.checkSemantics(ST, CT, nesting+1));

			for (Node stm : stmlist)
				errors.addAll(stm.checkSemantics(ST, CT, nesting+1));

			errors.addAll(body.checkSemantics(ST, CT, nesting+1));
			ST.remove();
			CT.remove();

		}
		return errors ; // problemi con la generazione di codice!
	}

	public Type typeCheck () {
		if (declist!=null) 
			for (Node dec:declist)
				dec.typeCheck();
		for (Node stm:stmlist)
			stm.typeCheck();
		if ( (body.typeCheck()).getClass().equals(returntype.getClass())) 
			return null ;
		else {
			System.out.println("Wrong return type for function "+id);
			return new ErrorType() ;
		}  
	}

	public String codeGeneration() {

		String declCode = "" ;
		if (declist.size() != 0) {
			for (Node dec:declist){
				declCode += dec.codeGeneration();
			}
		}
		String stmCode = "" ;
		if (stmlist.size() != 0) {
			for (Node stm:stmlist){
				stmCode += stm.codeGeneration();
			}
		}

		HPCLanlib.putCode(
				flabel + ":\n"
				+ "pushr RA \n"
				+ declCode
				+ stmCode
				+ body.codeGeneration()
				+ "addi SP " + 	declist.size() + "\n"
				+ "popr RA \n"
				+ "addi SP " + 	parlist.size() + "\n" // pop di tutti i parametri
				+ "pop \n"
				+ "store CL 0(CL) \n"
				+ "move CL AL \n"
				+ "subi AL 1 \n"
				+ "pop \n"
				+ "rsub RA \n\n" 
				);

		return "push "+ flabel +"\n"; // e` lo stesso che scrivere "push 0 \n" : non ci accede mai
	}

	public String toPrint(String s) {
		String parlstr="";
		for (Node par:parlist){
			parlstr += par.toPrint(s);
		}
		String decstr= "";
		if (declist!=null) 
			for (Node dec:declist)
				decstr+=dec.toPrint(s);
		String stmstr= "";
		if (stmlist!=null) 
			for (Node stm:stmlist)
				stmstr+=stm.toPrint(s);
	
		return s+"Fun " + id + returntype.toPrint(" ") + "\n"
			+parlstr  
			+decstr + "\n"
			+stmstr + "\n"
			+body.toPrint(s+" ") + "Fun-end\n";
	}

}  
