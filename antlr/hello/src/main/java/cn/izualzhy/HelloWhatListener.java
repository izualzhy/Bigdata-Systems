package cn.izualzhy;

import cn.izualzhy.antlr4.HelloBaseListener;
import cn.izualzhy.antlr4.HelloParser;

import java.util.List;

public class HelloWhatListener extends HelloBaseListener {
    @Override
    public void enterHello(HelloParser.HelloContext ctx) {
        System.out.println("HelloWhatListener enterHello");
        List<HelloParser.NameContext> nameContextsList = ctx.name();
        if (nameContextsList.size() == 1) {
            System.out.println("someone say hello to " + nameContextsList.get(0).ID());
        } else if (nameContextsList.size() == 2) {
            System.out.println(nameContextsList.get(1).ID() +  " say hello to " + nameContextsList.get(0).ID());
        }
        super.enterHello(ctx);
    }

    @Override
    public void enterName(HelloParser.NameContext ctx) {
        System.out.println(ctx.getStart());
        System.out.println(ctx.getStop());
        super.enterName(ctx);
    }
}
