package carrotbat410.lol.dto;

import lombok.Getter;

@Getter
public class ErrorResult {
    private final String code;
    private final String message;
    private final String fieldName;

    public ErrorResult(String code, String message) {
        this(code, message, null);
    }
    public ErrorResult(String code, String message, String fieldName) {
        this.code = code;
        this.message = message;
        this.fieldName = fieldName;
    }
}