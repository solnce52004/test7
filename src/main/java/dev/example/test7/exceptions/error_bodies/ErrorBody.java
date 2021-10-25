package dev.example.test7.exceptions.error_bodies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
//список будет включен в ответ только в том случае, если мы его зададим
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ErrorBody {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String statusName;
    private String customMessage;
    private String debugMessage;
    private Map<String, Set<String>> errors;
}
