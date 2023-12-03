package cn.izualzhy;

import cn.izualzhy.antlr4.HelloBaseListener;
import cn.izualzhy.antlr4.HelloParser;

public class HelloWhatListener extends HelloBaseListener {
    @Override
    public void enterR(HelloParser.RContext ctx) {
        System.out.println("listen: say hello to " + ctx.ID().getText());
    }
}
