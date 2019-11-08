package space.myhomework.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class AuthenticationHandler extends AbstractHandler {
    private String _token;

    public AuthenticationHandler(String token) {
        _token = token;
    }

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String authToken = baseRequest.getHeader("X-MHS-Auth");

        boolean auth = false;
        if (authToken != null && !authToken.isEmpty()) {
            if (authToken.equals(_token)) {
                auth = true;
            }
        }

        if (!auth) {
            response.setContentType("text/html; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().print("Authentication failed!");
			response.getWriter().close();

            baseRequest.setHandled(true);
            return;
        }
    }
}