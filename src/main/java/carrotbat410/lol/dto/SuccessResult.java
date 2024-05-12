package carrotbat410.lol.dto;

import lombok.Getter;

@Getter
public class SuccessResult <T> {
    private final String message;
    private final T data;
    private final int totalCnt;

    public SuccessResult() {
        this("ok", null);
    }

    public SuccessResult(String message) {
        this(message, null);
    }

    public SuccessResult(String message, T data) {
        this(message, data, 0);
    }

    public SuccessResult(String message, T data, int totalCnt) {
        this.message = message;
        this.data = data;
        this.totalCnt = totalCnt;
    }
    //TODO SuccessResult totalCnt 더 나은 방법없나.
    // 이렇게 하니 다른 api에도 totalCnt 값 찍히는게 문제임.
    // List전용 SuccessListResult이런거 만들어야하나?
}
