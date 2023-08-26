package cn.izualzhy.springmore.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class ValidatorPojo {
    @NotNull(message = "id不能为空")
    private Long id;

    @Future(message = "需要一个将来日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date date;

    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "1000.00")
    private Double doubleValue = null;

    @Min(value = 1, message = "最小值为1")
    @Max(value = 88, message = "最大值为88")
    @NotNull
    private Integer integer;

    @Range(min = 1, max = 888, message = "范围=[1,888]")
    private Long range;

    @Email(message = "邮箱格式错误")
    private String email;

    @Size(min = 20, max = 30, message = "字符串长度要求20到30之间")
    private String size;

//    @Override
//    public String toString() {
//        return "id:" + id + " date:" + date + " doubleValue:" + doubleValue + " integer:" + integer
//                + " range:" + range + " email:" + email + " size:" + size;
//    }
}
