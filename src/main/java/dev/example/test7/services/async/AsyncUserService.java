package dev.example.test7.services.async;

import dev.example.test7.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class AsyncUserService {

    @Async("threadPoolTaskExecutor1")
    public CompletableFuture<UserDTO> getUserById(final Long id)
            throws InterruptedException
//            , Throwable
    {
        log.info("(_*_) Filling user details for id {}", id);
        final UserDTO user = new UserDTO("Async", "pass");
        Thread.sleep(10_000);

//        throw new Throwable();
        return CompletableFuture.completedFuture(user);
    }

    @Async("threadPoolTaskExecutor2")
    public void updateUser(UserDTO user) throws InterruptedException {
        log.warn(
                "(_*_) Running method with thread {} :",
                Thread.currentThread().getName()
        );
        // do nothing
    }
}
