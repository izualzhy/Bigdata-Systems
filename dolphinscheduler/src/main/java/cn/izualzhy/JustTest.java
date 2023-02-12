package cn.izualzhy;

import java.io.File;

public class JustTest {
    public static void main(String[] args) {
        DbType dbType = DbType.E;

        switch (dbType) {
            case A:
                System.out.println("1");
            case B:
            case C:
                System.out.println("2");
                break;
            case D:
                System.out.println("3");
            case E:
                System.out.println("4");
            default:
                System.out.println("5");
        }

        System.out.println(dbType == DbType.E);
        System.out.println(dbType.equals(DbType.E));
    }
}
