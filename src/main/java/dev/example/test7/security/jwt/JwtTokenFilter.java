package dev.example.test7.security.jwt;

import dev.example.test7.security.exception.JwtAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        //вытащили из запроса по тайному названию хэдера - токен
        final String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            // проверили, что не протух
            if (token != null && jwtTokenProvider.validateToken(token)) {

                final Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthException e) {
            //при перехвате ошибки валидации токета
            //чистим контекст и в респонс пропбрасываем ошибку с пойманным статусом
            SecurityContextHolder.clearContext();

            final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            try (PrintWriter writer = httpServletResponse.getWriter()) {
                writer.write("{ \"error\": \"" + e.getMessage() + "\" }");
            }
            return;

//            httpServletResponse.sendError(e.getHttpStatus().value(), e.getMessage());
        }

        //применяем фильтр
        chain.doFilter(request, response);
    }
}
