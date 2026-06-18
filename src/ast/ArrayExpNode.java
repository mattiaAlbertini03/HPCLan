package ast;

import java.util.ArrayList;

import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class ArrayExpNode implements Node {
	private final String id ;
	private STentry type ;
	private int nesting ;
	private Node exp;

	public ArrayExpNode (String _id, Node _exp) {
		id = _id ;
		exp = _exp;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;

		STentry st_type = ST.lookup(id) ;
		if (st_type == null)
			errors.add(new SemanticError("array " + id + " not declared"));
		else type = st_type ;
		errors.addAll(exp.checkSemantics(ST, CT, _nesting));
		return errors;
	}

	public Type typeCheck () {
		if (type.gettype() instanceof ArrowType) { 
			System.out.println("Wrong usage of function identifier");
			return new ErrorType() ;
		}
		if (exp.typeCheck() instanceof IntType) {
			return type.gettype();
		}
		else{
			System.out.println("array err: idx is not a int");
			return new ErrorType() ;
		}
	}

	public String codeGeneration() {
		String getAR="";
		for (int i=0; i < nesting - type.getnesting(); i++)
			getAR += "store T1 0(T1) \n";
		return
			"move AL T1 \n"+
			getAR  +
			"subi T1 " + type.getoffset() +"\n" +
			"subi T1 1 \n" +
			"pushr T1 \n" +
			exp.codeGeneration() +
			"popr T1 \n" +
			"sub T1 A0 \n" +
			"popr T1 \n" +
			"store A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto
	}

	public String toPrint(String s) {
		return s+" array " + id + " at nestlev " + type.getnesting() + " at idx " + exp.toPrint(s+" ") ;
	}

} 
