package casdemo.cookie;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {

	public static final String USER_AGENT_HEADER = "user-agent";
	
	public static String getHttpServletRequestUserAgent(final HttpServletRequest request) {
        if (request != null) {
            return request.getHeader(USER_AGENT_HEADER);
        }
        return null;
    }
	
}
