package cn.izualzhy;

import cn.izualzhy.antlr4.HiveParser;
import cn.izualzhy.antlr4.HiveParserBaseListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Optional;

public class HiveSQLRewriter extends HiveParserBaseListener {
    TokenStreamRewriter tokenStreamRewriter;

    public HiveSQLRewriter(CommonTokenStream commonTokenStream) {
        tokenStreamRewriter = new TokenStreamRewriter(commonTokenStream);
    }

    @Override
    public void enterFunction(HiveParser.FunctionContext ctx) {
        // ARRAY(...) to ARRAY[...]
        Optional.of(ctx)
                .map(HiveParser.FunctionContext::functionName)
                .map(HiveParser.FunctionNameContext::sql11ReservedKeywordsUsedAsFunctionName)
                .map(HiveParser.Sql11ReservedKeywordsUsedAsFunctionNameContext::KW_ARRAY)
                .ifPresent(__ -> {
                    tokenStreamRewriter.replace(ctx.LPAREN().getSymbol(), "[");
                    tokenStreamRewriter.replace(ctx.RPAREN().getSymbol(), "]");
                });
        super.enterFunction(ctx);
    }

    @Override
    public void enterIdentifier(HiveParser.IdentifierContext ctx) {
        // size to CARDINALITY
        if (ctx.Identifier().getText().equalsIgnoreCase("size")) {
            tokenStreamRewriter.replace(ctx.Identifier().getSymbol(), "CARDINALITY");
        }
        super.enterIdentifier(ctx);
    }

    public String toSQL() {
        return tokenStreamRewriter.getText();
    }

}
