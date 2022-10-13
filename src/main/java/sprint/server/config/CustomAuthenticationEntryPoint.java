package sprint.server.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        ApiException exception = (ApiException) request.getAttribute("exception");

        /**
         * 토큰 없는 경우
         */
        if(exception == null ) {
            setResponse(response, new ApiException(ExceptionEnum.TOKEN_NONE_PERMISSION));
        }
        /**
         * 그 외 경우
         */
        else {
            setResponse(response, exception);
        }
    }

    private void setResponse(HttpServletResponse response, ApiException errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(
                "{ \"errorCode\" : \"" +  errorCode.getErrorCode()
                +"\", \"errorMessage\" : \"" + errorCode.getMessage() +"\"" +
                "}");
    }
}
