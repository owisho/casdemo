package casdemo.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.CookieGenerator;

public class CookieRetrievingCookieGenerator extends CookieGenerator {

	public String retrieveCookieValue(final HttpServletRequest request) {
		try {
			final Cookie cookie = org.springframework.web.util.WebUtils.getCookie(request, getCookieName());
			return cookie.getValue();
		} catch (final Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}
	
}
