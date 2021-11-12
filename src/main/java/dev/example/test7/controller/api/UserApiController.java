package dev.example.test7.controller.api;

import dev.example.test7.entity.User;
import dev.example.test7.service.by_entities.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Api(value = "UserController")

public class UserApiController {
    private final UserService userService;

    @Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Создание нового пользователя")
    @PostMapping(
//           path = "/",//в постмане тогда обязательно пишем "/" в конце запроса!
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<User> save(@RequestBody User user) {
        final HttpHeaders headers = new HttpHeaders();

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userService.save(user);
        return new ResponseEntity<>(user, headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Поиск пользователя по id")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Secured({"ROLE_USER"})
    public ResponseEntity<User> getById(@PathVariable("id") Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Поиск всех пользователей")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('reader')")
    public ResponseEntity<List<User>> getAll() {
        final List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @ApiOperation(value = "Поиск всех пользователей по имени")
    @GetMapping(
            path = "/names/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<User>> getAllByName(
            @PathVariable("username") String username
    ) {
        final List<User> users = userService.findAllByName(username);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @ApiOperation(value = "Частичное обновление данных пользователя с указанным id")
    @PatchMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> patch(
            @PathVariable("id") Long id,
            @RequestBody User user
    ) {
        final HttpHeaders headers = new HttpHeaders();

        if (id == null || user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final User userExist = userService.findById(id);
        if (userExist == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //todo
        userService.patch(user);
        return new ResponseEntity<>(user, headers, HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "Обновление всех данных пользователя с указанным id")
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        final HttpHeaders headers = new HttpHeaders();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final User updatedUser = userService.update(user);
        return new ResponseEntity<>(updatedUser, headers, HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "Удаление данных пользователя с указанным id")
    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> deleteById(@PathVariable("id") Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
