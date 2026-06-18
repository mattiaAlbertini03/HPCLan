package ast;

import java.util.ArrayList;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class DecNode implements Node {
	private final String id;
	private final Node type;
	private final Node exp;
	private final boolean isConst;
	private int nesting;

	public DecNode(String _id, Node _type, Node _exp, boolean _isConst) {
		id = _id ;
		type = _type ;
		exp = _exp ;
		isConst = _isConst;
	}
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		errors.addAll(exp.checkSemantics(ST, CT, nesting));

		if (ST.top_lookup(id))
			errors.add(new SemanticError("Var id " + id + " already declared"));
		else {
			ST.insert(id, (Type) type, nesting,"") ;
			if (isConst) {
				Integer value = -1;
				if (exp instanceof IntNode) 
					value = ((IntNode) exp).getValue();
				else 
					value = ((BoolNode) exp).getValue();
				CT.insert(id, value) ;
			}
		}
		return errors ;
	}

	public Type typeCheck () {
		if (exp.typeCheck().getClass().equals(type.getClass() ))
			return null ; 
		else {
			System.out.println("Type Error: incompatible type "+ id) ;
			return new ErrorType() ;
		}    
	}

	public String codeGeneration() {
		return exp.codeGeneration() +
			"pushr A0 \n" ;
	} 

	public String toPrint(String s) {
		return s + (isConst ? "Const " : "Var ") + id + type.toPrint(" ") +" =" + exp.toPrint(s + " ") + "\n";	
	}

} 
