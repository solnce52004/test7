package dev.example.test7.controller.mvc;


import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import dev.example.test7.service.auth.RegisterService;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.factory.UserDTOFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@Log4j2

public class AuthorizationController {
    private final RegisterService registerService;
    private final UserService userService;
    private final UserDTOFactory userDTOFactory;

    public AuthorizationController(
            RegisterService registerService,
            UserService userService,
            UserDTOFactory userDTOFactory
    ) {
        this.registerService = registerService;
        this.userService = userService;
        this.userDTOFactory = userDTOFactory;
    }

    @GetMapping("/register")
    public String loginPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO("", ""));
        }
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView checkUser(
            @Valid @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String route;

        if (bindingResult.hasErrors()) {
            route = "/auth/register";
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
        } else {
            final User user = userDTOFactory.userDTOtoUser(userDTO);
            final User userExist = userService
                    .findByEmail(user.getEmail())
                    .orElse(null);
            if (userExist != null) {
                throw new SecurityException("user exists: " + user.getEmail());
            }

            registerService.createUserWithRolePermission(user);
            // автоматически не логиним!
            // отправляем письмо для верификации
            route = "/auth/login";
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }


}
