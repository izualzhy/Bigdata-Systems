package cn.izualzhy.springmore.exception;


import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {
    private Long code;
    private String customMessage;

    public NotFoundException(Long code, String customMessage) {
        this.code = code;
        this.customMessage = customMessage;
    }
}
