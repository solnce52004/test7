package dev.example.test7.controller.mvc;

import dev.example.test7.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
@Log4j2

public class AuthenticateController {
//    private final UserService userService;
//
//    public AuthenticateController(UserService userService) {
//        this.userService = userService;
//    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO("", ""));
        }
        return "login";
    }

    @GetMapping("/failed")
    public String failed() {
        return "failed";
    }

    @GetMapping("/error")
    public String error() {
        return "redirect:/login";
    }

    @GetMapping("/success")
    public String success(Principal principal) {

        final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info(principal.getName());
        log.info(userDetails.getAuthorities());

        return "success";
    }

    // .loginProcessingUrl("/login/process")
    // Сообщает Spring Security обрабатывать отправленные учетные данные
    // при отправке по указанному пути
    // и по умолчанию перенаправлять пользователя обратно на страницу,
    // с которой пользователь пришел.
    // Он не будет передавать запрос в Spring MVC и ваш контроллер.

//    @PostMapping("/sign-in")
//    public ModelAndView checkUser(
//            @Valid @ModelAttribute("user") UserDTO user,
//            BindingResult bindingResult,
//            RedirectAttributes redirectAttributes
//    ) {
//        String route = "/upload-multiple";
//
//        if (bindingResult.hasErrors()) {
//            route = "/auth/login";
//            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
//        }
//
//        redirectAttributes.addFlashAttribute("user", user);
//        final RedirectView redirectView = new RedirectView(route);
//        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        final ModelAndView mav = new ModelAndView();
//        mav.setView(redirectView);
//
//        return mav;
//    }
}
