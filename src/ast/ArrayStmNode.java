package ast;
import java.util.ArrayList;

import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class ArrayStmNode implements Node {
	private final String id ;
	private STentry type ;
	private final Node idx ;
	private final Node exp ;
	private int nesting ;

	public ArrayStmNode(String _id, Node _idx, Node _exp) {
		id = _id;
		idx = _idx;
		exp = _exp ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		STentry st_type = ST.lookup(id) ;
		if (st_type == null)
			errors.add(new SemanticError("array " + id + " not declared"));
		else {
			type = st_type ;
			errors.addAll(idx.checkSemantics(ST, CT, nesting));
			errors.addAll(exp.checkSemantics(ST, CT, nesting));
		}
		return errors;
	}

	public Type typeCheck() {
		Type exp_type = exp.typeCheck();	
		Type id_type = type.gettype();
		if (id_type.getClass().equals(exp_type.getClass())){
			if(idx.typeCheck() instanceof IntType)
				return null;
			else {
			System.out.println("Array Error: idx is not a int") ;
			return new ErrorType() ;
			}
		}else {
			System.out.println("Array Error: Different types in equality") ;
			return new ErrorType() ;
		}
	}

	public String codeGeneration() {
		String getAR="";
		for (int i=0; i < nesting - type.getnesting(); i++)
			getAR += "store T1 0(T1) \n";

		return  exp.codeGeneration()
			+ "pushr A0 \n"
			+ "move AL T1 \n"
			+ getAR  //risalgo la catena statica
			+ "subi T1 " + type.getoffset() +"\n" //metto offset sullo stack
			+ "subi T1 1 \n" 
			+ "pushr T1 \n" 
			+ idx.codeGeneration()
			+ "popr T1 \n"
			+ "sub T1 A0 \n"
			+ "popr T1 \n"
			+ "popr A0 \n"
			+ "load A0 0(T1) \n" ;
	}

	public String toPrint(String s) {
		return s+"Array " + id + "[" + idx.toPrint(s)  + "] = " + exp.toPrint(s) + "\n" ;
	}
} 
