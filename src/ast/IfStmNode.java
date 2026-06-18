package ast;

import java.util.ArrayList;

import evaluator.HPCLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class IfStmNode implements Node {
	private final Node guard ;
	private final ArrayList<Node> stm1 ;
	private final ArrayList<Node> stm2 ;
	private boolean elseBranch;

	public IfStmNode (Node _guard, ArrayList<Node> _stm1, ArrayList<Node> _stm2) {
		guard = _guard ;
		stm1 = _stm1;
		stm2 = _stm2;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

		errors.addAll(guard.checkSemantics(ST, CT, nesting));
		if (stm1!=null) 
			for (Node s : stm1) 
				errors.addAll(s.checkSemantics(ST, CT, nesting)) ; 
		if (stm2!=null) 
			for (Node s : stm2) 
				errors.addAll(s.checkSemantics(ST, CT, nesting)) ; 
		return errors;
	}

	public Type typeCheck() {
		if (guard.typeCheck() instanceof BoolType) {
			if (stm1!=null) 
				for (Node s: stm1)
					s.typeCheck();
			if (stm2!=null) 
				for (Node s: stm2)
					s.typeCheck();
			return null;
		} else {
			System.out.println("Type Error: non boolean condition in if");
			return new ErrorType() ;
		}   
	}

	public String codeGeneration() {
		String lthen = HPCLanlib.freshLabel(); 
		String lend = HPCLanlib.freshLabel();
		String stm1Code="";
		if (stm1.size() != 0) 
			for (Node s: stm1)
				stm1Code += s.codeGeneration();
		String stm2Code="";
		if (stm2.size() != 0) 
			for (Node s: stm2)
				stm2Code += s.codeGeneration();
		return guard.codeGeneration() +
			"storei T1 1 \n" +
			"beq A0 T1 "+ lthen + "\n" +
		        stm2Code +	
			"b " + lend + "\n" +
			lthen + ":\n" +
		        stm1Code +	
			lend + ":\n" ; 
	}

	public String toPrint(String s) {
		String stm1String="";
		if (stm1.size() != 0) 
			for (Node st: stm1)
				stm1String += st.toPrint(s+" ");
		String stm2String="";
		if (stm2.size() != 0) 
			for (Node st: stm2)
				stm2String += st.toPrint(s+" ");
		return
			s+"If (" + guard.toPrint(s+" ")+ "){\n"
			+ stm1String + "}else{\n"
			+ stm2String+"}\n";
	}

}  
