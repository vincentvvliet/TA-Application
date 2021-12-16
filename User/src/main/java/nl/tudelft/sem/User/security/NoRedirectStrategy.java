package nl.tudelft.sem.User.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.RedirectStrategy;

public class NoRedirectStrategy implements RedirectStrategy {

    /**
     * In case of authentication failure, the server should not redirect to any error page.
     * The server will simply return an HTTP 401 (Unauthorized).
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param url      url
     * @throws IOException In case of IO problem, throw exception
     */
    @Override
    public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url) throws IOException {
        // No redirect is required
    }
}
