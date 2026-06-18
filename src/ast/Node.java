package ast;

import java.util.ArrayList;

import semanticanalysis.SemanticError ;
import semanticanalysis.SymbolTable ;
import semanticanalysis.ConstTable ;

public interface Node {

	ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting);
	Type typeCheck();
	String codeGeneration();

	String toPrint(String s);

} 
