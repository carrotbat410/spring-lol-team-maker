package carrotbat410.lol.exhandler.advice;

import carrotbat410.lol.dto.ErrorResult;
import carrotbat410.lol.exhandler.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "carrotbat410.lol.controller")
public class ExControllerAdvice {

    //방법1
//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> userExHandle(UserException e) {
//        log.error("[exceptionHandle] ex", e);
//        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
//    }

    //방법2
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult userExHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("USER-EX", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult userExHandle2(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] ex", e);
        String defaultMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return new ErrorResult("JOIN", defaultMessage);
    }


    //전체 예외 에러 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

}
