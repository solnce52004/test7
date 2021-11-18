package dev.example.test7.controller.mvc.auth;

import dev.example.test7.constant.Route;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import dev.example.test7.service.auth.VerifyRegistrationService;
import dev.example.test7.service.factory_dto.UserDTOFactory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@Controller
@RequestMapping(Route.AUTH)
@Log4j2
@AllArgsConstructor

public class VerifyRegistrationController {
    private final VerifyRegistrationService verifyRegistrationService;

    @GetMapping(Route.SEND_VERIFY)
    public String sendEmailVerifyPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<>());
        }
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", "");
        }
        return "resend_verify";
    }

    //на форме переотправки подтеврждения нажали отправить снова
    @PostMapping("/resend-token")
    public ModelAndView resent(
            @ModelAttribute("user") UserDTO userDTO,
            RedirectAttributes redirectAttributes
    ) {
        String route;
        final User verifiedUser = verifyRegistrationService.resentVerifyRegisterToken(userDTO.getEmail());

        if (verifiedUser != null) {
            userDTO = UserDTOFactory.userToUserDTORegister(verifiedUser);
            route = Route.AUTH_SEND_VERIFY;//отправили письмо и снова даем возможность переотправить
            redirectAttributes.addFlashAttribute("message", "Вам на почту было отправлено сообщение со ссылкой для подтверждения регистрации. Ссылка действительна в течении 15 минут.");

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

    //сюда попадаем из письма
    @GetMapping("/verify/{token}")
    public ModelAndView register(
            @PathVariable(name = "token") String token,
            RedirectAttributes redirectAttributes
    ) {
        String route;
        UserDTO userDTO;
        final User verifiedUser = verifyRegistrationService.verifyRegisterToken(token);

        if (verifiedUser != null) {
            userDTO = UserDTOFactory.userToUserDTORegister(verifiedUser);
            route = Route.AUTH_LOGIN;//успешно потвердил - логинься
        } else {
            userDTO = new UserDTO();
            route = Route.AUTH_SEND_VERIFY;//не смог подтвердить - уточни почту и переоправь письмо
        }

        redirectAttributes.addFlashAttribute("user", userDTO);
        final RedirectView redirectView = new RedirectView(route);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
}
