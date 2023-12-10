package cn.izualzhy;

import cn.izualzhy.antlr4.HiveParser;
import cn.izualzhy.antlr4.HiveParserBaseListener;
import cn.izualzhy.antlr4.HiveParserListener;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HiveParserListenerImpl extends HiveParserBaseListener {
    List<String> inputTables = new ArrayList<>();
    List<Pair<String, String>> outputTables = new ArrayList<>();
    @Override
    public void enterSelectStatement(HiveParser.SelectStatementContext ctx) {
        super.enterSelectStatement(ctx);
    }

    @Override
    public void enterTableSource(HiveParser.TableSourceContext ctx) {
        inputTables.add(ctx.tableName().getText());
        super.enterTableSource(ctx);
    }

    @Override
    public void enterTableOrPartition(HiveParser.TableOrPartitionContext ctx) {
        String table = ctx.tableName() != null ? ctx.tableName().getText() : "UNKNOWN";
        String partition = ctx.partitionSpec() != null ? ctx.partitionSpec().getText() : "UNKNOWN";

        outputTables.add(new Pair<>(table, partition));
        super.enterTableOrPartition(ctx);
    }

    public void show() {
        System.out.println(inputTables.stream().collect(Collectors.joining(",", "[", "]")) +
                " -> " + outputTables.stream()
                        .map(pair -> pair.a + "@" + pair.b)
                        .collect(Collectors.joining(",", "[", "]")));
    }
}
