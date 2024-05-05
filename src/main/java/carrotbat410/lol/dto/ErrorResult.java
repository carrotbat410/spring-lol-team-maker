package carrotbat410.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}