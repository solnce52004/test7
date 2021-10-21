package dev.example.test7.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "not blank")
    private String name;

    @NotBlank(message = "not blank")
    @Size(min = 2, max = 8, message = "match size")
    private String password;

    private Boolean isAdmin = false;

    public UserDTO(@NotBlank(message = "not blank") String name, @NotBlank(message = "not blank") @Size(min = 2, max = 8, message = "match size") String password) {
        this.name = name;
        this.password = password;
    }
}
