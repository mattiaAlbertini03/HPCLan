package ast;

import java.util.ArrayList;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class ArrayDecNode implements Node {
	private final String id;
	private final String len;
	private final Node type;
	private int nesting;
	private Integer n;

	public ArrayDecNode(String _id, Node _type, String _len, Integer _n) {
		id = _id ;
		type = _type ;
		len = _len;
		n = _n;
	}


	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		if (ST.top_lookup(id))
			errors.add(new SemanticError("Array " + id + " already declared"));
		if(len != ""){
			Integer constVal = CT.lookup(len);	
			if (constVal != null){
				n = constVal;
			} else {
				errors.add(new SemanticError("Array " + id + " error: size " + len + " is not a const."));
			}
		}

		ST.insert(id, (Type) type, nesting,"") ;

		if (n > 0) {
			for( int i = 0; i < n; i++)
				ST.insert(id + "_" + i, (Type) type, nesting,"") ;
		}

		return errors ;
	}

	public Type typeCheck () {
		return null ;
	}

	public String codeGeneration() {
		String arrdec = "";
		for (int i = 0; i < n ; i++)
			arrdec += "pushr A0 \n";

		return  "storei A0 "+ n + "\n" +
			"pushr A0 \n" +
			"storei A0 0\n" + 	
			arrdec;
	} 


	public String toPrint(String s) {
		return s + "Array " + id + "[" + n + "]" + type.toPrint(s + " ")+"\n";
	}

} 
