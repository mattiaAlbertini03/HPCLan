package ast;

import java.util.ArrayList;

import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class PlusNode implements Node {
	private final Node left;
	private final Node right;

	public PlusNode (Node _left, Node _right) {
		left = _left ;
		right = _right;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

		errors.addAll(left.checkSemantics(ST, CT, _nesting));
		errors.addAll(right.checkSemantics(ST, CT, _nesting));

		return errors;
	}

	public Type typeCheck() {
		if ((left.typeCheck() instanceof IntType) && (right.typeCheck() instanceof IntType) )
			return new IntType() ;
		else {
			System.out.println("Type Error: Non integers in addition") ;
			return new ErrorType() ;
		}
	}

	public String codeGeneration() {
		return left.codeGeneration()+
			"pushr A0 \n" +
			right.codeGeneration()+
			"popr T1 \n" +
			"add T1 A0 \n" +
			"popr A0 \n";
	}

	public String toPrint(String s) {
		return s+ left.toPrint(s)+"+" + right.toPrint(s) ;
	}

}
