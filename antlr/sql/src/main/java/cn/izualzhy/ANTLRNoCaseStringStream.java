package cn.izualzhy;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;

public class ANTLRNoCaseStringStream extends ANTLRInputStream {
    public ANTLRNoCaseStringStream(String input) {
        super(input);
    }

    @Override
    public int LA(int i) {

        int returnChar = super.LA(i);
        if (returnChar == CharStream.EOF) {
            return returnChar;
        } else if (returnChar == 0) {
            return returnChar;
        }

        return Character.toUpperCase((char) returnChar);
    }
}
