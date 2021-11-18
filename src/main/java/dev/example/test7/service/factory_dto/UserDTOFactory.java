package dev.example.test7.service.factory_dto;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDTOFactory {

    public static User userDTOtoUserRegister(UserDTO userDTO){
        return new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getStatus()
        );
    }

    public static UserDTO userToUserDTOVerified(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getStatus()
        );
    }

    public static UserDTO userToUserDTORegister(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus()
        );
    }
    public static UserDTO userToUserDTOSendResetPassword(User user) {
        return new UserDTO(
                user.getEmail()
        );
    }
    public static UserDTO userToUserDTOResetPassword(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getConfirmPassword(),
                user.getStatus()
        );
    }
}
