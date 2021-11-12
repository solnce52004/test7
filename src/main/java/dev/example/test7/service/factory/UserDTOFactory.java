package dev.example.test7.service.factory;

import dev.example.config.security.enums.UserStatusEnum;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserDTOFactory {
    public User userDTOtoUser(UserDTO userDTO){
        return new User(
                userDTO.getUsername(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                UserStatusEnum.ACTIVE
        );
    }
}
