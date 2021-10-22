package dev.example.test7.controllers;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.routes.Routes;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Log4j2
//@SessionAttributes("user")

public class HomeController {

//    private final TranslatorService translatorService;
//
//    @Autowired
//    public HomeController(TranslatorService translatorService) {
//        this.translatorService = translatorService;
//    }

    @GetMapping(Routes.ROUTE_HOME)
    public String home(HttpSession session) {
        return Routes.VIEW_HOME;
    }

    @GetMapping(Routes.ROUTE_FAILED)
    public String failed() {
        return Routes.VIEW_FAILED;
    }

//    @ModelAttribute
//    private UserDTO create(){
//        return new UserDTO();
//    }

    @GetMapping(Routes.ROUTE_LOGIN)
    public String index(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return Routes.VIEW_LOGIN;
    }

    @PostMapping(Routes.ROUTE_CHECK_USER)
    public ModelAndView checkUser(
            @Valid @ModelAttribute("user") UserDTO user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        final ModelAndView mav = new ModelAndView();

        if (bindingResult.hasErrors()) {
            final RedirectView redirectView = new RedirectView(Routes.VIEW_LOGIN);
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            mav.setView(redirectView);

            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
        } else {
            final RedirectView redirectView = new RedirectView(Routes.VIEW_HOME);
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            mav.setView(redirectView);
        }
        return mav;
    }


    @GetMapping(value = "/get-user/{id}", produces = "application/json")
    @ResponseBody
    public UserDTO getUserById(@Valid @PathVariable String id) {
        return new UserDTO(id, "1231232132");
    }

    @GetMapping(value = "/get-user-by-name", produces = "application/json")
    @ResponseBody
    public UserDTO getUserByName(@Valid @RequestParam("name") String name) {
        return new UserDTO(name, "1231232132");
    }

    @PostMapping(value = "/set-user", consumes = "application/json")
    public ResponseEntity<UserDTO> setUser(@Valid @RequestBody UserDTO user) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
