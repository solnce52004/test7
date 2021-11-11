package dev.example.test7.controller.mvc;

import dev.example.test7.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

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

    @GetMapping("/success")
    public String success() {
        return "success";
    }

//    @PostMapping("/login")
//    public ModelAndView checkUser(
//            @Valid @ModelAttribute("user") UserDTO user,
//            BindingResult bindingResult,
//            RedirectAttributes redirectAttributes
//    ) {
//        String route = "/auth/success";
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
