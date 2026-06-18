package ast;

import evaluator.HPCLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

import java.util.ArrayList;

public class GtNode implements Node {
	private final Node left ;
	private final Node right ;

	public GtNode (Node _left, Node _right) {
		left = _left ;
		right = _right ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		errors.addAll(left.checkSemantics(ST, CT, _nesting));
		errors.addAll(right.checkSemantics(ST, CT, _nesting));

		return errors;
	}

	public Type typeCheck() {
		if ((left.typeCheck() instanceof IntType) && (right.typeCheck() instanceof IntType) )
			return new BoolType() ;
		else {
			System.out.println("Type Error: Non integers in addition") ;
			return new ErrorType() ;
		}
	}

	public String codeGeneration() {
		String ltrue = HPCLanlib.freshLabel();
		String lend = HPCLanlib.freshLabel();
		return  left.codeGeneration()+
			"pushr A0 \n" +
			right.codeGeneration()+
			"popr T1 \n" +
			"blt A0 T1 "+ ltrue +"\n"+
			"storei A0 0\n"+
			"b " + lend + "\n" +
			ltrue + ":\n"+
			"storei A0 1\n" +
			lend + ":\n";
	}

	public String toPrint(String s) {
		return s+"(" + left.toPrint(s+" ")+">" + right.toPrint(s+" ")+")" ;
	}

}
