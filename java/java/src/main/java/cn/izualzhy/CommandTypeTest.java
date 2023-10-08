package cn.izualzhy;

public class CommandTypeTest {
    public static void main(String[] args) {
        for (CommandType command : CommandType.values()) {
            System.out.println(command + " " + command.ordinal());
        }
    }
}
