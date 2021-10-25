package dev.example.test7.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ERROR_MSG_EMPTY_VALUE = "{error_msg.empty_value}";
    public static final String ERROR_MSG_NOT_VALID = "{error_msg.not_valid}";

    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    private String name;

    @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
    @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID)
    private String password;

    private Boolean isRememberMe = false;

    public UserDTO(
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE) String name,
            @NotBlank(message = ERROR_MSG_EMPTY_VALUE)
            @Size(min = 2, max = 8, message = ERROR_MSG_NOT_VALID) String password
    ) {
        this.name = name;
        this.password = password;
    }

    public boolean isAdmin() {
        return name.equals("admin");
    }
}
