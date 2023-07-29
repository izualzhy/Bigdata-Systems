package cn.izualzhy.springmore.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SampleELClass {
    @Value("#{'使用 Spring EL 赋值字符串'}")
    public String str = null;

    @Value("#{9.3E3}")
    public double d;

    @Value("#{3.14}")
    public float pi;

    @Value("#{user.getUserName}")
    private String otherBeanProp = null;

    @Value("#{user.getUserName?.toUpperCase()}")
    private String otherBeanPropUpper = null;

    @Value("#{1+2}")
    private int run;

    @Override
    public String toString() {
        return String.format("SampleELClass\nrun:%d\nstr:%s\n" +
                        "d:%f\npi:%f\notherBeanProp:%s otherBeanPropUpper:%s\n",
                run, str, d, pi, otherBeanProp, otherBeanPropUpper);
    }
}
