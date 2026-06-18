package ast;

import java.util.ArrayList;
import parser.HPCLanBaseVisitor ;

import parser.HPCLanParser.ProgContext;
import parser.HPCLanParser.DecContext;
import parser.HPCLanParser.SimpledecContext;
import parser.HPCLanParser.IdDecContext;
import parser.HPCLanParser.ArrayDecContext;
import parser.HPCLanParser.SimpleDecContext;
import parser.HPCLanParser.FunDecContext;
import parser.HPCLanParser.ParamContext;
import parser.HPCLanParser.TypeContext;
import parser.HPCLanParser.StmContext;
import parser.HPCLanParser.ExpContext;
import parser.HPCLanParser.SignedValContext;
import parser.HPCLanParser.BaseExpContext;
import parser.HPCLanParser.IfExpContext;
import parser.HPCLanParser.FunExpContext;
import parser.HPCLanParser.ArrayExpContext;
import parser.HPCLanParser.VarExpContext;
import parser.HPCLanParser.IntValContext;
import parser.HPCLanParser.BoolValContext;
import parser.HPCLanParser.IdStmContext;
import parser.HPCLanParser.ArrayStmContext;
import parser.HPCLanParser.IfStmContext;
import parser.HPCLanParser.WhlStmContext;
import parser.HPCLanParser.MprdStmContext;

public class HPCLanVisitorImpl extends HPCLanBaseVisitor<Node> {
	
	public Node visitProg(ProgContext ctx) {
		ArrayList<Node> decs = new ArrayList<Node>();
		if(ctx.dec() != null){
		for (DecContext dc : ctx.dec())
			decs.add( visit(dc) );
		}
		ArrayList<Node> stms = new ArrayList<Node>();
		if(ctx.stm() != null){
		for (StmContext st : ctx.stm())
			stms.add( visit(st) );
		}

		Node exp = visit( ctx.exp() );

		return new ProgNode(decs, stms, exp) ;
	}

	public Node visitIdDec(IdDecContext ctx) {
		Node typeNode = visit(ctx.type()); //visit the type
		Node expNode = visit(ctx.exp()); //visit the exp
		
		boolean isConst = ctx.getText().contains("const");

		return new DecNode(ctx.ID().getText(), typeNode, expNode, isConst);
	}
	public Node visitArrayDec(ArrayDecContext ctx) { 
		Node typeNode = visit(ctx.type()); //visit the type
		String len = "";
		Integer n= -1;
		if(ctx.ID(1) != null)
			len = ctx.ID(1).getText();
		else
			n = Integer.parseInt(ctx.INT().getText());
		return new ArrayDecNode(ctx.ID(0).getText(), typeNode, len, n);
	}
	public Node visitSimpleDec(SimpleDecContext ctx) { 
		return visit (ctx.simpledec());
	}
	public Node visitFunDec(FunDecContext ctx) {
		ArrayList<ParNode> _param = new ArrayList<ParNode>() ;
		for (ParamContext vc : ctx.param()) // build the list of parameters with the types
			_param.add( new ParNode(vc.ID().getText(), (Type) visit( vc.type() )) );

		ArrayList<Node> innerDec = new ArrayList<Node>(); // this is for the declarations in the body
		if(ctx.simpledec() != null){
			for(SimpledecContext dc : ctx.simpledec())
				innerDec.add(visit(dc));
		}
		ArrayList<Node> innerStm = new ArrayList<Node>(); 
		if(ctx.stm() != null){
			for(StmContext st : ctx.stm())
				innerStm.add(visit(st));
		}
		Node exp = visit(ctx.exp()); // visit the body

		return new FunNode(ctx.ID().getText(), (Type) visit(ctx.type()), _param, innerDec, innerStm, exp);
	}
	public Node visitType(TypeContext ctx) {
		if(ctx.getText().equals("int"))
			return new IntType();
		else return new BoolType();
	}
	public Node visitIdStm(IdStmContext ctx) { 
		Node expNode = visit(ctx.exp()); //visit the exp
		return new IdStmNode(ctx.ID().getText(), expNode);
	
	}
	public Node visitArrayStm(ArrayStmContext ctx) { 
		Node exp1 = visit(ctx.exp(0)); //visit the exp
		Node exp2 = visit(ctx.exp(1)); //visit the exp
		return new ArrayStmNode(ctx.ID().getText(), exp1, exp2);
	}
	public Node visitIfStm(IfStmContext ctx) { 
		Node condExp = visit(ctx.exp());
		boolean flag = true;
		ArrayList<Node> thenStms = new ArrayList<Node>();
		ArrayList<Node> elseStms = new ArrayList<Node>();
		if(ctx.thenBranch != null || ctx.elseBranch != null){
			for (StmContext st : ctx.stm()){
				if(st == ctx.elseBranch)
					flag = false;
				if(flag)
					thenStms.add( visit(st) );
				else
					elseStms.add( visit(st) );
			}
		}
		return new IfStmNode(condExp, thenStms, elseStms);
	}
	public Node visitWhlStm(WhlStmContext ctx) { 
		Node condExp = visit(ctx.exp());
		ArrayList<Node> stms = new ArrayList<Node>();
		for (StmContext st : ctx.stm())
			stms.add( visit(st) );
		return new WhlStmNode(condExp, stms);
	}
	public Node visitMprdStm(MprdStmContext ctx) { 
		String idx_0 = ctx.ID(0).getText();
		String idx_1 = "";
		String valStr = "";
		Node exp_0 = visit(ctx.exp(0));
		Node exp_1 = visit(ctx.exp(1));
		Integer valInt = -1;
		if ( ctx.INT() != null){
			valInt = Integer.parseInt(ctx.INT().getText());
			idx_1 = ctx.ID(1).getText();
		}else{
			valStr = ctx.ID(1).getText();
			idx_1 = ctx.ID(2).getText();

		}	
		return new MapredStmNode(idx_0, valStr, valInt, idx_1, exp_0, exp_1);
	}

	public Node visitExp(ExpContext ctx) {
		if (ctx.op == null) { return visit(ctx.value());
		} else {
			Node left = visit(ctx.left);
			Node right = visit(ctx.right);
			switch (ctx.op.getText()) {
				case "*": return new MultNode(left, right);
				case "/": return new DivNode(left, right);
				case "+": return new PlusNode(left, right);
				case "-": return new MinusNode(left, right);
				case "==": return new EqualNode(left, right);
				case "!=": return new UnEqualNode(left, right);
				case ">=": return new GeqNode(left, right);
				case "<=": return new LeqNode(left, right);
				case "<": return new LtNode(left, right);
				case ">": return new GtNode(left, right);
				case "&&": return new AndNode(left, right);
				case "||": return new OrNode(left, right);
				default: throw new IllegalStateException("Unknown operator: " + ctx.op.getText());
			}
		}
	}
	public Node visitSignedVal(SignedValContext ctx) {
		String operator = ctx.op.getText();
		if (operator.equals("-")) return new UMinusNode(visit(ctx.value())) ;
		else if (operator.equals("!")) return new NotNode(visit(ctx.value())) ;
		else return visit(ctx.value()) ; // operator.equals("+")
	}
	public Node visitBaseExp(BaseExpContext ctx) {
		return visit (ctx.exp());
	}
	public Node visitIfExp(IfExpContext ctx) {
		Node condExp = visit(ctx.cond);
		boolean flag = true;
		ArrayList<Node> thenStms = new ArrayList<Node>();
		ArrayList<Node> elseStms = new ArrayList<Node>();
		if(ctx.thenBranch != null || ctx.elseBranch != null){
			for (StmContext st : ctx.stm()){
				if(st == ctx.elseBranch)
					flag = false;
				if(flag)
					thenStms.add( visit(st) );
				else
					elseStms.add( visit(st) );
			}
		}
		Node thenExp = visit(ctx.exp(1));
		Node elseExp = visit(ctx.exp(2));
		return new IfNode(condExp, thenStms, thenExp, elseStms, elseExp);
	}
	public Node visitFunExp(FunExpContext ctx) {
		ArrayList<Node> args = new ArrayList<Node>();

		for (ExpContext exp : ctx.exp())
			args.add(visit(exp));

		return new CallNode(ctx.ID().getText(), args);
	}
	public Node visitArrayExp(ArrayExpContext ctx) { 
		Node expNode = visit(ctx.exp()); 
		return new ArrayExpNode(ctx.ID().getText(), expNode); 

	}
	public Node visitVarExp(VarExpContext ctx) {
		return new IdNode(ctx.getText());
	}
	public Node visitIntVal(IntValContext ctx) {
		return new IntNode(Integer.parseInt(ctx.INT().getText()));
	}
	public Node visitBoolVal(BoolValContext ctx) {
		return new BoolNode(Boolean.parseBoolean(ctx.BOOL().getText()));
	}
}
