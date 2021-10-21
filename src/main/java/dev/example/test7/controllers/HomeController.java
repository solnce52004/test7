package dev.example.test7.controllers;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.routes.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Slf4j
public class HomeController {

    @GetMapping(Routes.ROUTE_HOME)
    public String home() {
        return Routes.VIEW_HOME;
    }

    @GetMapping(Routes.ROUTE_FAILED)
    public String failed() {
        return Routes.VIEW_FAILED;
    }

    @GetMapping(Routes.ROUTE_LOGIN)
    public ModelAndView index() {
        return new ModelAndView(Routes.VIEW_LOGIN, "user", new UserDTO());
    }

//    @GetMapping(Routes.ROUTE_CHECK_USER)
//    public String checkUser(Model model) {
//        return Routes.VIEW_LOGIN;
//    }

    @PostMapping(Routes.ROUTE_CHECK_USER)
    public ModelAndView checkUser(
            @Valid @ModelAttribute("user") UserDTO user,
            BindingResult bindingResult,
            ModelAndView mav,
            final RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            //TODO: как передать модель и ошибки и сделать редирект (нужна идемпотентность!!!)
//            redirectAttributes.addFlashAttribute("user", user);
//            mav.setViewName("redirect:/login");
//            return mav;

            return new ModelAndView(Routes.VIEW_LOGIN, "user", user);
        }

//        redirectAttributes.addFlashAttribute("user", user);
//        mav.setViewName("redirect:/home");
//        return mav;
        return new ModelAndView(Routes.VIEW_HOME, "user", user);
    }


    @GetMapping(value = "/get-user/{id}", produces = "application/json")
    @ResponseBody
    public UserDTO getUserById(@PathVariable String id){
        //get user from db
        //return json
        return new UserDTO(id, "1231232132");
    }

    @GetMapping(value = "/get-user-by-name", produces = "application/json")
    @ResponseBody
    public UserDTO getUserByName(@RequestParam("name") String name){
        //get user from db
        //return json
        return new UserDTO(name, "1231232132");
    }

    @PostMapping(value = "/set-user", consumes = "application/json")
    public ResponseEntity<String> setUser(@RequestBody UserDTO user){
        //get json

        //save userDTO to db
        log.info(user.getName());
        log.info(user.getPassword());

        //return status code
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
