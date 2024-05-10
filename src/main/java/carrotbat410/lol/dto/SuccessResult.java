package carrotbat410.lol.dto;

import lombok.Getter;

@Getter
public class SuccessResult <T> {
    private final String message;
    private final T data;

    public SuccessResult() {
        this("ok", null);
    }

    public SuccessResult(String message) {
        this(message, null);
    }

    public SuccessResult(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
