package dev.example.test7.controllers.mvc;

import dev.example.test7.constants.View;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.constants.Route;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(Route.ROUTE_HOME)
    public String home(HttpSession session) {
        return View.VIEW_HOME;
    }

    @GetMapping(Route.ROUTE_FAILED)
    public String failed() {
        return View.VIEW_FAILED;
    }

//    @ModelAttribute
//    private UserDTO create(){
//        return new UserDTO();
//    }

    @GetMapping(Route.ROUTE_LOGIN)
    public String index(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO("", ""));
        }
        return View.VIEW_LOGIN;
    }

    @PostMapping(Route.ROUTE_CHECK_USER)
    public ModelAndView checkUser(
            @Valid @ModelAttribute("user") UserDTO user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String view = View.VIEW_HOME;

        if (bindingResult.hasErrors()) {
            view = View.VIEW_LOGIN;
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
        }

        redirectAttributes.addFlashAttribute("user", user);

        final RedirectView redirectView = new RedirectView(view);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
}
