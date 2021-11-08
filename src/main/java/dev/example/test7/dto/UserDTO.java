package dev.example.test7.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@NoArgsConstructor
@ToString
@Setter
@Getter
@XmlRootElement
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ERROR_MSG_EMPTY_VALUE = "{error_msg.empty_value}";
    public static final String ERROR_MSG_NOT_VALID = "{error_msg.not_valid}";

    @ApiModelProperty(notes = "Имя пользователя", required = true)
    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    private String name;

    @ApiModelProperty(notes = "Пароль пользователя", required = true)
    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
    private String password;

    @ApiModelProperty(notes = "Флаг 'Запомнить', указанный при авторизации")
    private Boolean isRememberMe = false;

    @ApiModelProperty(notes = "Почта (опционально)")
    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    private String email;

    public UserDTO(
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String name,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
                    String password
    ) {
        this.name = name;
        this.password = password;
    }

    public UserDTO(
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String name,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID) String password,
            Boolean isRememberMe
    ) {
        this.name = name;
        this.password = password;
        this.isRememberMe = isRememberMe;
    }

    public boolean isAdmin() {
        return name.equals("admin");
    }
}
