package cn.izualzhy.springmore.enumeration;

public enum SexEnum {
    MALE(1, "男"),
    FEMALE(2, "女");

    private int id;
    private String name;
    SexEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SexEnum getEnumById(int id) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.id == id) {
                return sexEnum;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
