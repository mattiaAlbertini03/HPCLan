package ast;

import java.util.ArrayList;
import java.util.HashMap;

import evaluator.HPCLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;
import semanticanalysis.STentry;

public class MapredStmNode implements Node {
	private final String idx;
	private String len;
	private final String arr;
	private final Node arr_idx;
	private final Node exp;
	private STentry type ;
	private STentry typeArr ;
	private Integer n;
	private int nesting;


	public MapredStmNode(String _idx, String valStr, Integer valInt, String _arr, Node exp_0, Node exp_1){
		idx = _idx;
		arr = _arr;
		len = valStr;
		n = valInt;
		arr_idx = exp_0;
		exp = exp_1;

	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		this.nesting = _nesting;

		if(len != ""){
			Integer constVal = CT.lookup(len);	
			if (constVal != null)
				n = constVal;
		} else {
			errors.add(new SemanticError("Mapred error: size '" + len + "' is not a const."));
		}
		STentry val = ST.lookup(arr);
		if (val == null) {
			errors.add(new SemanticError("Mapred error: array '" + arr + "' is not declared."));
		}
		else typeArr = val;
		if (n <= 0) {
			errors.add(new SemanticError("Mapred error: the number of proc. have to be at least 1"));
		}

		STentry val2 = ST.lookup(arr);
		if (val2 == null) {
			errors.add(new SemanticError("Mapred error: idx '" + idx + "' is not declared."));
		}
		type = ST.lookup(idx);

		errors.addAll(arr_idx.checkSemantics(ST, CT, _nesting));
		errors.addAll(exp.checkSemantics(ST, CT, _nesting));

		return errors;
	}


	public Type typeCheck() {
		if (!(arr_idx.typeCheck() instanceof IntType)) {
			System.out.println("Type Error: Mapred target array index expression must be an integer.");
			return new ErrorType();
		}
		Type t = type.gettype();
		if (exp.typeCheck().getClass().equals(t.getClass() ))
			return null;
		else {           
			System.out.println("Type Error: incompatible type of expression for "+arr) ;
			return new ErrorType();
		}

	}


	public String codeGeneration() {
		String lend = HPCLanlib.freshLabel();
		String lstart = HPCLanlib.freshLabel();
		
		String getAR_i = "";
		for (int i=0; i < nesting - type.getnesting(); i++)
			getAR_i += "store T1 0(T1) \n";
		
		String i_eq_n = "storei A0 "+n+"\n"
			+ "move AL T1\n"		// risalgo la catena statica
			+ getAR_i
			+ "subi T1 " + type.getoffset() +"\n" //metto offset sullo stack
			+ "load A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto
	
		String exp_i = 
			"move AL T1 \n"
			+ getAR_i  //risalgo la catena statica
			+ "subi T1 " + type.getoffset() +"\n" //metto offset sullo stack
			+ "store A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto

			;

		String decr_i = exp_i
			+ "subi A0 1 \n" 
			+ "move AL T1\n"		// risalgo la catena statica
			+ getAR_i
			+ "subi T1 " + type.getoffset() +"\n" //metto offset sullo stack
			+ "load A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto

		String getAR_arr="";
		for (int i=0; i < nesting - typeArr.getnesting(); i++)
			getAR_arr += "store T1 0(T1) \n";

		String arrStm = exp.codeGeneration()
			+ "pushr A0 \n"
			+ "move AL T1 \n"
			+ getAR_arr  //risalgo la catena statica
			+ "subi T1 " + typeArr.getoffset() +"\n" //metto offset sullo stack
			+ "subi T1 1 \n" 
			+ "pushr T1 \n" 
			+ arr_idx.codeGeneration()
			+ "popr T1 \n"
			+ "sub T1 A0 \n"
			+ "popr T1 \n"
			+ "popr A0 \n"
			+ "load A0 0(T1) \n" ;

		return  i_eq_n
			+ lstart + ":\n"
			+ decr_i 
			+ exp_i 	
			+ "pushr A0 \n" 
			+ "storei A0 0\n"
			+ "popr T1 \n" 
			+ "blt T1 A0 " + lend + "\n"
		        + arrStm
			+ "b "+ lstart + " \n"	
			+ lend + ":\n" ;

	}

	public String toPrint(String s) {
		return s + "Mapred(" + idx + " upto: " + n + " array: " + arr + " idx "+ arr_idx.toPrint(s + " ") + "\n\t"
			+ "e = "+ exp.toPrint(s + " ")+")\n";
	}
}



