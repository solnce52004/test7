package dev.example.test7.controller.mvc.auth;


import dev.example.test7.dto.UserDTO;
import dev.example.test7.service.auth.RegisterUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/auth")
@Log4j2

public class RegistrationController {
    private final RegisterUserService registerUserService;

    @Autowired
    public RegistrationController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView register(
            @Valid @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String route = "/auth/register";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.user",
                    bindingResult);

        } else if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            bindingResult.addError(new ObjectError("confirmPassword", "Пароли не совпадают"));
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.user",
                    bindingResult);

        } else {
            registerUserService.registerAnonymous(userDTO);
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

    @GetMapping("/resent-token/{email}")
    public ModelAndView resent(
            @PathVariable(name = "email") String email,
            RedirectAttributes redirectAttributes
    ) {
        final UserDTO userDTO = registerUserService.resentVerify(email);

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView("/auth/register");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

    @GetMapping("/verify/{token}")
    public ModelAndView register(
            @PathVariable(name = "token") String token,
            RedirectAttributes redirectAttributes
    ) {
        String route;
        UserDTO userDTO = registerUserService.verify(token);

        if (userDTO != null) {
            route = "/auth/login";
        } else {
            userDTO = new UserDTO();
            route = "/auth/register";
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

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
        return "redirect:/auth/register";
    }
}
