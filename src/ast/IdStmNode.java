package ast;
import java.util.ArrayList;

import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class IdStmNode implements Node {
	private final String id ;
	private STentry type ;
	private final Node exp ;
	private int nesting ;

	public IdStmNode(String _id, Node _exp) {
		id = _id;
		exp = _exp ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		STentry st_type = ST.lookup(id) ;
		if (st_type == null)
			errors.add(new SemanticError("(stm)Id " + id + " not declared"));
		else {
			type = st_type ;
			errors.addAll(exp.checkSemantics(ST, CT, nesting));
		}
		return errors;
	}

	public Type typeCheck() {
		Type exp_type = exp.typeCheck();	
		Type id_type = type.gettype();
		if (id_type.getClass().equals(exp_type.getClass()))
			return null;
		else {
			System.out.println("Type Error: Different types in equality") ;
			return new ErrorType() ;
		}
	}

	public String codeGeneration() {
		String getAR="";
		for (int i=0; i < nesting - type.getnesting(); i++)
			getAR += "store T1 0(T1) \n";

		return  exp.codeGeneration()
			+ "move AL T1\n"		// risalgo la catena statica
			+ getAR
			+ "subi T1 " + type.getoffset() +"\n" //metto offset sullo stack
			+ "load A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto
	}

	public String toPrint(String s) {
		return s + id + "=" + exp.toPrint(s) + "\n" ;
	}
} 
