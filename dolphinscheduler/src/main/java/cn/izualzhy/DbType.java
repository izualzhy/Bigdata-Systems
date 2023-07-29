package cn.izualzhy;

public enum DbType {
    A(0, "HelloA"),
    B(1, "HelloB"),
    C(2, "HelloC"),
    D(3, "HelloD"),
    E(4, "HelloE");

    private final int code;
    private final String desc;

    DbType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}