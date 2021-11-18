package dev.example.test7.controller.mvc.auth;


import dev.example.test7.constant.Route;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.service.auth.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping(Route.AUTH)
@Log4j2
@AllArgsConstructor

public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping(Route.REGISTRATION)
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }
        return "registration";
    }

    @PostMapping(Route.REGISTRATION)
    public ModelAndView register(
            @Valid @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String route = Route.AUTH_REGISTRATION;

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
            registrationService.registerAnonymous(userDTO);
            route = Route.AUTH_SEND_VERIFY;
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
}
