package casdemo.logout;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.logout.LogoutRequest;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import casdemo.cookie.CookieRetrievingCookieGenerator;

@Component("terminateSessionAction")
public class TerminateSessionAction {

	@Autowired
	private CentralAuthenticationService centralAuthenticationService;
	
	@Autowired
	private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator ;
	
	public Event terminate(final RequestContext context){
		
		String tgtId = WebUtils.getTicketGrantingTicketId(context);
		if(tgtId!=null){
			final List<LogoutRequest> logoutRequests = centralAuthenticationService.destroyTicketGrantingTicket(tgtId);
			WebUtils.putLogoutRequests(context, logoutRequests);
		}
		final HttpServletResponse response = WebUtils.getHttpServletResponse(context);
		ticketGrantingTicketCookieGenerator.removeCookie(response);
		return new Event(this,"success");
		
	}
	
}
