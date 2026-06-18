package ast;
import java.util.ArrayList;
import java.util.HashMap;

import evaluator.HPCLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class ProgNode implements Node {

	private final ArrayList<Node> dec ;
	private final ArrayList<Node> stm ;
	private final Node exp ;
	private int nesting ;

	public ProgNode (ArrayList<Node> _dec, ArrayList<Node> _stm, Node _exp) {
		dec = _dec ;
		stm = _stm ;
		exp = _exp ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		nesting = _nesting + 1 ;
		HashMap<String,STentry> H = new HashMap<String, STentry>();
		ST.add(H);
		HashMap<String,Integer> cons = new HashMap<String, Integer>();
		CT.add(cons);

		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		
		if (dec!=null)
			for (Node d : dec)
				errors.addAll(d.checkSemantics(ST, CT, nesting)) ;

		if (stm!=null)
			for (Node s : stm)
				errors.addAll(s.checkSemantics(ST, CT, nesting)) ;
		

		errors.addAll(exp.checkSemantics(ST, CT, nesting)) ;

		ST.remove();
		CT.remove();

		return errors;
	}

	public Type typeCheck () {
		if (dec!=null)
			for (Node d: dec)
				d.typeCheck();
		if (stm!=null)
			for (Node s: stm)
				s.typeCheck();
		return exp.typeCheck();
	}

	public String codeGeneration() {
		String declCode="\n";
		if (dec.size() != 0)
			for (Node d: dec)
				declCode += d.codeGeneration();
		String stmCode="\n";
		if (stm.size() != 0)
			for (Node s: stm)
				stmCode += s.codeGeneration();
		return  "move SP CL \n"
			+ "pushr CL \n"
			+ "move SP AL \n"
			+ "pushr AL \n"
			+ declCode
			+ stmCode
			+ exp.codeGeneration()
			+ "halt\n\n" +
			HPCLanlib.getCode();
	}

	public String toPrint(String s) {
		String declstr="";
		for (Node d : dec)
			declstr += d.toPrint(s);
		String stmstr="";
		for (Node st : stm)
			stmstr += st.toPrint(s);
		return s+"Prog\nDEC:\n" + declstr + "STM:\n" + stmstr +"Exp:\n" +  exp.toPrint(s+" ")+"\n" ;
	}

} 
