package ast;

import java.util.ArrayList;

import evaluator.HPCLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class IfNode implements Node {
	private final Node guard ;
	private final ArrayList<Node> stm1 ;
	private final Node thenbranch ;
	private final ArrayList<Node> stm2 ;
	private final Node elsebranch ;

	public IfNode (Node _guard, ArrayList<Node> _stm1, Node _thenbranch, ArrayList<Node> _stm2, Node _elsebranch) {
		guard = _guard ;
		stm1 = _stm1;
		thenbranch = _thenbranch ;
		stm2 = _stm2;
		elsebranch = _elsebranch ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

		errors.addAll(guard.checkSemantics(ST, CT, nesting));
		if (stm1!=null) 
			for (Node s : stm1) 
				errors.addAll(s.checkSemantics(ST, CT, nesting)) ; 
		errors.addAll(thenbranch.checkSemantics(ST, CT, nesting));
		if (stm2!=null) 
			for (Node s : stm2) 
				errors.addAll(s.checkSemantics(ST, CT, nesting)) ; 
		errors.addAll(elsebranch.checkSemantics(ST, CT, nesting));

		return errors;
	}

	public Type typeCheck() {
		if (guard.typeCheck() instanceof BoolType) {
			if (stm1!=null) 
				for (Node s: stm1)
					s.typeCheck();
			Type thenexp = thenbranch.typeCheck() ;
			if (stm2!=null) 
				for (Node s: stm2)
					s.typeCheck();
			Type elseexp = elsebranch.typeCheck() ;
			if (thenexp.getClass().equals(elseexp.getClass()))
				return thenexp;
			else {
				System.out.println("Type Error: incompatible types in then and else branches");
				return new ErrorType() ;	
			}
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
			elsebranch.codeGeneration() +
			"b " + lend + "\n" +
			lthen + ":\n" +
		        stm1Code +	
			thenbranch.codeGeneration() +
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
			s+"If ("+ guard.toPrint(s+" ")+"){\n"
			+ stm1String
			+ thenbranch.toPrint(s+" ")+"\n} else {\n"
			+ stm2String
			+ elsebranch.toPrint(s+" ") +"\n}\n";
	}

}  
