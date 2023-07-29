package cn.izualzhy.springmore.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
//@DependsOn("eLClass")
public class AnotherELClass {
    @Value("#{sampleELClass.pi == 3.14f}")
    private boolean piFlag;

    @Value("#{sampleELClass.str eq 'Spring Boot'}")
    private boolean strFlag;

    @Value("#{sampleELClass.str + ' 连接字符串'}")
    private String strApp = null;

    @Value("#{sampleELClass.d > 1000 ? '大于1000' : '小于1000'}")
    private String resultDesc = null;

    @Override
    public String toString() {
        return String.format("SampleSampleELClass\nstrFlag:%b strApp:%s\n" +
                        "resultDesc:%s\npiFlag:%b\n\n",
                strFlag, strApp, resultDesc, piFlag);
    }
}
