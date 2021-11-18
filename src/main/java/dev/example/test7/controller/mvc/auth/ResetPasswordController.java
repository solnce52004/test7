package dev.example.test7.controller.mvc.auth;

import dev.example.test7.constant.Route;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import dev.example.test7.service.auth.ResetPasswordService;
import dev.example.test7.service.factory_dto.UserDTOFactory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@Controller
@RequestMapping(Route.AUTH)
@Log4j2
@AllArgsConstructor

public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;

    @GetMapping(Route.SEND_RESET_PASSWORD)
    public String sendResetPassPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO(""));
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", "");
        }
        return "send_reset_password";
    }

    //на форме нажали "Отправить запрос на сброс пароля"
    @PostMapping(Route.SEND_RESET_PASSWORD)
    public ModelAndView sendPostResetPass(
            @ModelAttribute("user") UserDTO userDTO,
            RedirectAttributes redirectAttributes
    ) {
        String route;
        final User verifiedUser = resetPasswordService.sendResetPasswordToken(userDTO.getEmail());

        if (verifiedUser != null) {
            userDTO = UserDTOFactory.userToUserDTOSendResetPassword(verifiedUser);
            route = Route.AUTH_SEND_RESET_PASSWORD;//отправили письмо и снова даем возможность переотправить
            redirectAttributes.addFlashAttribute("message", "Вам на почту было отправлено сообщение со ссылкой для сброса пароля. Ссылка действительна в течении 15 минут.");
        } else {
            userDTO = new UserDTO();
            route = Route.AUTH_REGISTRATION;//либо пусть регается
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

    //сюда попадаем из письма //////
    @GetMapping("/reset-password/verify/{token}")
    public ModelAndView fromMail(
            @PathVariable(name = "token") String token,
            RedirectAttributes redirectAttributes
    ) {
        String route;
        UserDTO userDTO;
        final User verifiedUser = resetPasswordService.verifyResetPasswordToken(token);

        if (verifiedUser != null) {
            userDTO = UserDTOFactory.userToUserDTOResetPassword(verifiedUser);
            route = Route.AUTH_RESET_PASSWORD;//валидный токен - переопределяй пароль
        } else {
            userDTO = new UserDTO();
            route = Route.AUTH_SEND_RESET_PASSWORD;//невалидный - уточни почту и переоправь письмо
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }

    // Сбросить пароль ///////
    @GetMapping(Route.RESET_PASSWORD)
    public String resetPassPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }
        return "reset_password";
    }

    //на форме Сбросить пароль нажали Save
    @PostMapping(Route.RESET_PASSWORD)
    public ModelAndView resetPass(
            @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String route = Route.AUTH_RESET_PASSWORD;

        //todo подставлять другую дто и ее валидировать
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute(
//                    "org.springframework.validation.BindingResult.user",
//                    bindingResult);
//
//        } else
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            bindingResult.addError(new ObjectError("confirmPassword", "Пароли не совпадают"));
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.user",
                    bindingResult);

        } else {
            resetPasswordService.updatePassword(userDTO.getEmail(), userDTO.getPassword());
            route = Route.AUTH_LOGIN;
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
}
