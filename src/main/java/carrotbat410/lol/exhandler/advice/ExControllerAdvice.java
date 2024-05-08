package carrotbat410.lol.exhandler.advice;

import carrotbat410.lol.dto.ErrorResult;
import carrotbat410.lol.exhandler.exception.AlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
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
    public ErrorResult beanValidationHandle(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] ex", e);

        FieldError error = ((FieldError) e.getBindingResult().getAllErrors().get(0));
        String fieldName = error.getField();
        String message = error.getDefaultMessage();
        String code = error.getCode();
//        log.info("fieldName: " + fieldName);
//        log.info("message: " + message);
//        log.info("code: " + code);

        return new ErrorResult(code, message, fieldName);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResult conflictDataHandle(AlreadyExistException e) {
        return new ErrorResult(e.getCode(), e.getMessage());
    }


    //전체 예외 에러 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

}
