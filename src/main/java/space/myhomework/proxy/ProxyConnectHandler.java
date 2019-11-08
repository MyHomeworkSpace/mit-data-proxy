package space.myhomework.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Request;

public class ProxyConnectHandler extends ConnectHandler {
	@Override
	public boolean validateDestination(String host, int port) {
		if (port == 443) {
			// allow https traffic ONLY
			return true;
		}

		return false;
	}

	@Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (baseRequest.isHandled()) {
			return;
		}

		String method = request.getMethod();

		if (!method.equalsIgnoreCase("CONNECT")) {
			// this isn't for us
			return;
		}

		super.handle(target, baseRequest, request, response);
	}
}