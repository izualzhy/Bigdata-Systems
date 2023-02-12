package cn.izualzhy.spring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Entity
@Table(indexes = {@Index(name = "uk_email", columnList = "email", unique = true)})
@ApiModel("UserInfo")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("user ID")
    private int id;

    @Column(nullable = false, columnDefinition = "varchar(20) comment '姓名'")
    @ApiModelProperty("user name")
    @NotBlank(message = "name must not empty")
    private String name;

//    @Transient
    @ApiModelProperty("user age")
    @Min(value = 1, message = "age must GE 1")
    private int age;

    @Column(nullable = false, length = 50)
    @ApiModelProperty("user email")
    @Email(message = "E-mail formatted error.")
    private String email;

    @ApiModelProperty("user birthday")
    @Past(message = "birthday before")
    private LocalDate birthday;

    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
        user.setName("");
        user.setEmail("izualzhy#163.com");

        System.out.println(user);
    }
}
