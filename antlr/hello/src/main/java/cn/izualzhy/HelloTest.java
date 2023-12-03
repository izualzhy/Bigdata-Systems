package cn.izualzhy;

import cn.izualzhy.antlr4.HelloLexer;
import cn.izualzhy.antlr4.HelloParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class HelloTest {
    private static void run(String expr) {
        ANTLRInputStream input = new ANTLRInputStream(expr);

        Lexer lexer = new HelloLexer(input);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(commonTokenStream);

        ParseTree parseTree = parser.r();
        System.out.println("LISP:\n" + parseTree.toStringTree(parser));

        HelloWhatVisitor helloWhatVisitor = new HelloWhatVisitor();
        System.out.println(helloWhatVisitor.visit(parseTree));

        ParseTreeWalker walker = new ParseTreeWalker();
        HelloWhatListener helloWhatListener = new HelloWhatListener();
        walker.walk(helloWhatListener, parseTree);
    }

    public static void main(String[] args) {
        run("hello world");
    }
}
