package dev.example.test7.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
@XmlRootElement
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ERROR_MSG_EMPTY_VALUE = "{error_msg.empty_value}";
    public static final String ERROR_MSG_NOT_VALID = "{error_msg.not_valid}";

    @ApiModelProperty(notes = "Имя пользователя")
    private String username;

    @ApiModelProperty(notes = "Почта (обязательно)", required = true)
    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    private String email;

    @ApiModelProperty(notes = "Пароль пользователя", required = true)
    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
    private String password;

    @ApiModelProperty(notes = "Повторный ввод пароля", required = true)
    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
    transient private String confirmPassword;

    public boolean isAdmin() {
        return username.equals("admin");
    }

    public UserDTO(
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String username,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
                    String password
    ) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String username,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String email,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID) String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserDTO(
            String username,

            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
                    String email,

            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
                    String password,

            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
                    String confirmPassword
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
