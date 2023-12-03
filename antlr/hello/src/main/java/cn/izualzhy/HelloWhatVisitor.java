package cn.izualzhy;

import cn.izualzhy.antlr4.HelloBaseVisitor;
import cn.izualzhy.antlr4.HelloParser;

public class HelloWhatVisitor extends HelloBaseVisitor<String> {
    @Override
    public String visitR(HelloParser.RContext ctx) {
        return "visit: say hello to " + ctx.ID().getText();
    }
}
