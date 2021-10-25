package dev.example.test7.controllers.api;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.exceptions.custom_exceptions.ThereIsNoSuchUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HomeApiController {
//    @GetMapping(value = "/get-user/{id}", produces = "application/json")
    @GetMapping(value = "/get-user/{id}")
    @ResponseBody
    public UserDTO getUserById(@Valid @PathVariable Long id) {

//        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));

        throw new ThereIsNoSuchUserException(id);
//        return new UserDTO(id, "1231232132");
    }

    @GetMapping(value = "/get-user-by-name")
    @ResponseBody
    public UserDTO getUserByName(@Valid @RequestParam("name") String name) {
        return new UserDTO(name, "1231232132");
    }

    @PostMapping(
            value = "/set-user",
            produces = "application/json",
            consumes = "application/json",
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE //406
    )
    public ResponseEntity<UserDTO> setUser(
            @Valid @RequestBody UserDTO user
//            ,
//            @RequestHeader(value = "Accept", required = false) List<MediaType> headers
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
