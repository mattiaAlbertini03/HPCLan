package ast;

import java.util.ArrayList;

import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class BoolNode implements Node {

	private final boolean val;

	public BoolNode (boolean _val) {
		val = _val ;
	}
	public Integer getValue(){
		return val ? 1 : 0;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		return new ArrayList<SemanticError>();
	}

	public Type typeCheck() {
		return new BoolType();
	}   

	public String codeGeneration() {
		return "storei A0 "+(val?1:0)+"\n";
	}

	public String toPrint(String s) {
		return s + val;
	}

} 
