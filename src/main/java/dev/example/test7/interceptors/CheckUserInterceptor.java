package dev.example.test7.interceptors;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.constants.Route;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckUserInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView mav
    ) throws Exception {

        if (request.getRequestURI().contains(Route.ROUTE_CHECK_USER)) {

            final UserDTO user = (UserDTO) mav.getModel().get("user");

            if (user != null &&
                    user.getName().length() > 0 &&
                    !user.isAdmin()
            ) {
                response.sendRedirect(request.getContextPath() + Route.ROUTE_FAILED);
            }
        }
    }
}
