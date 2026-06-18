package ast;

import java.util.ArrayList;

import evaluator.HPCLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class WhlStmNode implements Node {
	private final Node guard ;
	private final ArrayList<Node> stm ;

	public WhlStmNode (Node _guard, ArrayList<Node> _stm) {
		guard = _guard ;
		stm = _stm;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

		errors.addAll(guard.checkSemantics(ST, CT, nesting));
		for (Node s : stm)
			errors.addAll(s.checkSemantics(ST, CT, nesting)) ;
		return errors;
	}

	public Type typeCheck() {
		if (guard.typeCheck() instanceof BoolType) {
			if (stm!=null)
				for (Node s: stm)
					s.typeCheck();
			return null;
		} else {
			System.out.println("Type Error: non boolean condition in while");
			return new ErrorType() ;
		}  
	}

	public String codeGeneration() {
		String lend = HPCLanlib.freshLabel();
		String lstart = HPCLanlib.freshLabel();
		String stmCode="";
		for (Node s: stm)
			stmCode += s.codeGeneration();
		return
			lstart + ":\n" +
			guard.codeGeneration() +
			"storei T1 0 \n" +
			"beq A0 T1 "+ lend + "\n" +
		        stmCode +
			"b "+ lstart + " \n" + 	
			lend + ":\n" ;
	}

	public String toPrint(String s) {
		String stmString="";
		if (stm.size() != 0) 
			for (Node st: stm)
				stmString += st.toPrint(s+" ");
		return
			s+"While (" + guard.toPrint(s+" ") + "){\n"
			+ stmString + "\n}\n";
	}

} 
