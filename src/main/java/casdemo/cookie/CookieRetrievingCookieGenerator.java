package casdemo.cookie;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.authentication.RememberMeCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.CookieGenerator;

@Component("ticketGrantingTicketCookieGenerator")
public class CookieRetrievingCookieGenerator extends CookieGenerator {

	private static final int DEFAULT_REMEMBER_ME_MAX_AGE = 7889231;

    private int rememberMeMaxAge = DEFAULT_REMEMBER_ME_MAX_AGE;
    
    @Override
    @Autowired
	public void setCookieName(@Value("${tgc.name:TGC}")String cookieName) {
		super.setCookieName(cookieName);
	}

	@Autowired
	private CookieValueManager casCookieValueManager;

	public String retrieveCookieValue(final HttpServletRequest request) {
		try {
			final Cookie cookie = org.springframework.web.util.WebUtils
					.getCookie(request, getCookieName());
			return cookie.getValue();
		} catch (final Exception e) {
			logger.debug(e.getMessage(), e);
			return null;
		}
	}

	public void addCookie(final HttpServletRequest request,
			final HttpServletResponse response, final String cookieValue) {
		final String theCookieValue = this.casCookieValueManager
				.buildCookieValue(cookieValue, request);

		if (StringUtils
				.isBlank(request
						.getParameter(RememberMeCredential.REQUEST_PARAMETER_REMEMBER_ME))) {
			super.addCookie(response, theCookieValue);
		} else {
			final Cookie cookie = createCookie(theCookieValue);
			cookie.setMaxAge(this.rememberMeMaxAge);
			if (isCookieSecure()) {
				cookie.setSecure(true);
			}
			if (isCookieHttpOnly()) {
				final Method setHttpOnlyMethod = ReflectionUtils.findMethod(
						Cookie.class, "setHttpOnly", boolean.class);
				if (setHttpOnlyMethod != null) {
					cookie.setHttpOnly(true);
				} else {
					logger.debug("Cookie cannot be marked as HttpOnly; container is not using servlet 3.0.");
				}
			}
			response.addCookie(cookie);
		}
	}

}
