package dev.example.test7.controller.mvc.auth;

import dev.example.config.security.service.SecurityService;
import dev.example.test7.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;

@Controller
@RequestMapping("/auth")
@Log4j2
@AllArgsConstructor

public class AuthenticateController {
    private final SecurityService securityService;

    //!!!!
    @GetMapping("/login")
    public String loginPage(
            Model model
    ) {
        if (securityService.isAuthenticated()) {
            return "redirect:/auth/success";
        }

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO("", ""));
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }

        return "login";
    }

    // .loginProcessingUrl("/login/process")
    // Сообщает Spring Security обрабатывать отправленные учетные данные
    // при отправке по указанному пути
    // и по умолчанию перенаправлять пользователя обратно на страницу,
    // с которой пользователь пришел.
    // Он не будет передавать запрос в Spring MVC и ваш контроллер.

    @PostMapping("/process")
    public ModelAndView checkUser(
            @Valid @ModelAttribute("user") UserDTO user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String route;

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            route = "/auth/login";
        } else {
            securityService.autoLogin(
                    user.getEmail(),
                    user.getPassword()
            );
            route = "/auth/success";
        }

        redirectAttributes.addFlashAttribute("user", user);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

    @GetMapping("/failed")
    public String failed() {
        return "failed";
    }

    @GetMapping("/success")
    public String success(Principal principal) {
//
//        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        log.info(principal.getName());
//        log.info(userDetails.getAuthorities());

        return "success";
    }

    //чтобы редиректиться на кастомную страницу с ошибкой
//    @GetMapping("/error")
//    public String error() {
//        return "error";
//    }

    ////////////////
    @ExceptionHandler(SecurityException.class)
    public String AccessDeniedExceptionHandler(
            SecurityException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** SecurityException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        //TODO
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:/auth/login";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String UsernameNotFoundExceptionHandler(
            UsernameNotFoundException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** UsernameNotFoundException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        //TODO
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:/auth/login";
    }
}
