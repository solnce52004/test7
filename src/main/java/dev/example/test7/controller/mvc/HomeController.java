package dev.example.test7.controller.mvc;

import dev.example.test7.constant.Route;
import dev.example.test7.constant.View;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Log4j2
//@SessionAttributes("user")
public class HomeController {

    @GetMapping(Route.ROUTE_HOME)
    public String home(HttpSession session) {
        return View.VIEW_HOME;
    }

//    @ModelAttribute
//    private UserDTO create(){
//        return new UserDTO();
//    }
}
