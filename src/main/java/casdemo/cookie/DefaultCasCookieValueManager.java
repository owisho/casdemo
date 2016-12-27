package casdemo.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.CipherExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("defaultCookieValueManager")
public class DefaultCasCookieValueManager implements CookieValueManager{

	private static final char COOKIE_FIELD_SEPARATOR = '@';
    private static final int COOKIE_FIELDS_LENGTH = 3;
	
    @Autowired
    @Qualifier("tgcCipherExecutor")
	private CipherExecutor<String, String> cipherExecutor;
	
	@Override
	public String buildCookieValue(String givenCookieValue,
			HttpServletRequest request) {
		final StringBuilder builder = new StringBuilder(givenCookieValue);
		final String remoteAddr = request.getRemoteAddr();
		builder.append(COOKIE_FIELD_SEPARATOR);
		builder.append(remoteAddr);
		final String userAgent = request.getHeader("user-agent");
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

        if (!userAgent.equals(request.getHeader("user-agent"))) {
            throw new IllegalStateException("Invalid cookie. Required user-agent does not match " + request.getHeader("user-agent"));
        }
        return value;
	}

}
