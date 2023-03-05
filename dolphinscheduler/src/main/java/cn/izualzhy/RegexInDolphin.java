package cn.izualzhy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexInDolphin {
    static void check(String sql) {
        System.out.println("-------------\nsql:" + sql);
        String rgex = "['\"]*\\$\\{DS:(.*?)\\}['\"]*";
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(sql);

        while (m.find()) {
            String paramName = m.group(1);
            System.out.println("find " + paramName);
        }

        String rgexo = "['\"]*\\!\\{(.*?)\\}['\"]*";
        Pattern pattern2 = Pattern.compile(rgexo);
        while (true) {
            Matcher m2 = pattern2.matcher(sql);
            if (!m2.find()) {
                break;
            }

            System.out.println("before replace sql:" + sql);
            sql = m2.replaceFirst("HELLO");
            System.out.println("after replace sql:" + sql);
        }

        System.out.println("before replaceAll sql:" + sql);
        sql = sql.replaceAll(rgex, "?");
        System.out.println("after replaceAll sql:" + sql);
    }

    static void check_datasource() {
        Pattern params_patter = Pattern.compile("^[a-zA-Z0-9\\-\\_\\/\\@\\.]+$");
    }

    public static void main(String[] args) {
//        check_datasource();
//        check("select * from test_table");
        check("select ${dt} from test_table");
        check("select ${DS:dt} from test_table");
        check("select '${dt}' from test_table");
        check("select '!{dt}' from test_table");
        check("INSERT OVERWRITE TABLE `test_database`.`test_table` PARTITION(dt = '${hiveconf:date}')");
    }
}
