import cn.izualzhy.ANTLRNoCaseStringStream;
import cn.izualzhy.HiveParserListenerImpl;
import cn.izualzhy.HiveSQLRewriter;
import cn.izualzhy.antlr4.HiveLexer;
import cn.izualzhy.antlr4.HiveParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.StringBufferInputStream;

public class ParseHiveSQLTest {
    private static CommonTokenStream commonTokenStream = null;
    private static ParseTree parseTree = null;
    public static void main(String[] args) {
        String sql = "insert into app.table_c partition(dt = '20231221') select a.id,table_a.col1, c.col2 from app.table_a a left join bdm.table_b c on a.id=c.id";
//        init(sql);
//        parse();

        sql = "SELECT size(ARRAY (1, 2))";
        init(sql);
        rewrite();
    }

    private static void init(String sql) {
        ANTLRInputStream input = new ANTLRNoCaseStringStream(sql);
        Lexer lexer = new HiveLexer(input);
        commonTokenStream = new CommonTokenStream(lexer);

        HiveParser parser = new HiveParser(commonTokenStream);
        parseTree = parser.statements();

        System.out.println("LISP:\n" + parseTree.toStringTree(parser));
    }

    private static void parse() {
        ParseTreeWalker walker = new ParseTreeWalker();
        HiveParserListenerImpl hiveParserListener = new HiveParserListenerImpl();
        walker.walk(hiveParserListener, parseTree);

        hiveParserListener.show();
    }

    private static void rewrite() {
        ParseTreeWalker walker = new ParseTreeWalker();
        HiveSQLRewriter hiveSQLRewriter = new HiveSQLRewriter(commonTokenStream);
        walker.walk(hiveSQLRewriter, parseTree);

        // SELECT CAST(CARDINALITY(ARRAY[1, 2]) AS INTEGER)
        System.out.println(hiveSQLRewriter.toSQL());
    }
}
