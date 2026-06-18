package ast;

import java.util.ArrayList;

import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;
import semanticanalysis.ConstTable;

public class ParNode implements Node {

  private final String id;
  private final Type type;
  private int nesting ;
  
  public ParNode (String _id, Type _type) {
   id = _id ;
   type = _type ;
  }
  
  public String getId(){
	  return id;
  }
  
  public Type getType(){
	  return type;
  }
  
  @Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, ConstTable CT, int _nesting) {
	  nesting = _nesting ;
	  return new ArrayList<SemanticError>();
	}
  
   //non utilizzato
  public Type typeCheck () {
     return null;
  }
  
  //non utilizzato
  public String codeGeneration() {
		return "";
  }
  
  public String toPrint(String s) {
	  return s+"Par " + id + ":" + type.toPrint(s) ;
  }
  

    
}  
