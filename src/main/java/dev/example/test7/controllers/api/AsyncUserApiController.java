package dev.example.test7.controllers.api;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.services.async.AsyncUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/async")
public class AsyncUserApiController {

    private final AsyncUserService asyncUserService;

    @Autowired
    public AsyncUserApiController(AsyncUserService asyncUserService) {
        this.asyncUserService = asyncUserService;
    }

    @GetMapping("/user/{id}")
    public CompletableFuture<UserDTO> getUserById(@PathVariable(name = "id") Long id)
            throws InterruptedException
//            , Throwable
    {
        return asyncUserService.getUserById(id);
    }

    /**
     * TODO: уточнить почему не передаем пользователя
     * и что будет, если передать?
     */
    @PutMapping("/user/update")
    public void updateUser() throws InterruptedException {
        asyncUserService.updateUser(null);
    }
}
