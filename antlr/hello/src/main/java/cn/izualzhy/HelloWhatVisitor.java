package cn.izualzhy;

import cn.izualzhy.antlr4.HelloBaseVisitor;
import cn.izualzhy.antlr4.HelloParser;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class HelloWhatVisitor extends HelloBaseVisitor<String> {
    @Override
    public String visitHello(HelloParser.HelloContext ctx) {
        System.out.println("HelloWhatVisitor visitHello");
        List<HelloParser.NameContext> nameContextsList = ctx.name();
        if (nameContextsList.size() == 1) {
            return ("someone say hello to " + nameContextsList.get(0).ID());
        } else if (nameContextsList.size() == 2) {
            return (nameContextsList.get(1).ID() +  " say hello to " + nameContextsList.get(0).ID());
        }
        return super.visitHello(ctx);
    }
}
