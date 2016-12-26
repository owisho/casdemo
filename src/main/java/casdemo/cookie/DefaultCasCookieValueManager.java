package casdemo.cookie;

import java.io.Serializable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.CipherExecutor;
import org.jasig.inspektr.common.web.ClientInfo;
import org.jasig.inspektr.common.web.ClientInfoHolder;
import org.springframework.stereotype.Component;

@Component("defaultCookieValueManager")
public class DefaultCasCookieValueManager implements CookieValueManager{

	private static final char COOKIE_FIELD_SEPARATOR = '@';
    private static final int COOKIE_FIELDS_LENGTH = 3;
	
	private CipherExecutor<Serializable, String> cipherExecutor = NoOpCipherExecutor.getInstance();
	
	@Override
	public String buildCookieValue(String givenCookieValue,
			HttpServletRequest request) {
		final StringBuilder builder = new StringBuilder(givenCookieValue);
		final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
		builder.append(COOKIE_FIELD_SEPARATOR);
		builder.append(clientInfo.getClientIpAddress());
		final String userAgent = WebUtils.getHttpServletRequestUserAgent(request);
		builder.append(COOKIE_FIELD_SEPARATOR);
        builder.append(userAgent);
        final String res = builder.toString();
        return this.cipherExecutor.encode(res);
		
	}

	@Override
	public String obtainCookieValue(Cookie cookie, HttpServletRequest request) {
		final String cookieValue = this.cipherExecutor.decode(cookie.getValue());
        if (StringUtils.isBlank(cookieValue)) {
            return null;
        }

        final String[] cookieParts = cookieValue.split(String.valueOf(COOKIE_FIELD_SEPARATOR));
        if (cookieParts.length != COOKIE_FIELDS_LENGTH) {
            throw new IllegalStateException("Invalid cookie. Required fields are missing");
        }
        final String value = cookieParts[0];
        final String remoteAddr = cookieParts[1];
        final String userAgent = cookieParts[2];

        if (StringUtils.isBlank(value) || StringUtils.isBlank(remoteAddr)
                || StringUtils.isBlank(userAgent)) {
            throw new IllegalStateException("Invalid cookie. Required fields are empty");
        }

        if (!remoteAddr.equals(request.getRemoteAddr())) {
            throw new IllegalStateException("Invalid cookie. Required remote address does not match "
                    + request.getRemoteAddr());
        }

        final String agent = WebUtils.getHttpServletRequestUserAgent(request);
        if (!userAgent.equals(agent)) {
            throw new IllegalStateException("Invalid cookie. Required user-agent does not match " + agent);
        }
        return value;
	}

}
