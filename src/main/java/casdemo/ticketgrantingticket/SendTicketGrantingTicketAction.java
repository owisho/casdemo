package casdemo.ticketgrantingticket;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import casdemo.cookie.CookieRetrievingCookieGenerator;

@Component("sendTicketGrantingTicketAction")
public class SendTicketGrantingTicketAction extends AbstractAction {

	@Autowired
	private CentralAuthenticationService centralAuthenticationService;

	@Autowired
	private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

	@Override
	protected Event doExecute(RequestContext context) throws Exception {

		final String ticketGrantingTicketId = WebUtils
				.getTicketGrantingTicketId(context);
		final String ticketGrantingTicketValueFromCookie = (String) context
				.getFlowScope().get("ticketGrantingTicketId");
		if (ticketGrantingTicketId == null) {
			return success();
		}

		this.ticketGrantingTicketCookieGenerator.addCookie(
				WebUtils.getHttpServletRequest(context),
				WebUtils.getHttpServletResponse(context),
				ticketGrantingTicketId);

		if (ticketGrantingTicketValueFromCookie != null
				&& !ticketGrantingTicketId
						.equals(ticketGrantingTicketValueFromCookie)) {
			this.centralAuthenticationService
					.destroyTicketGrantingTicket(ticketGrantingTicketValueFromCookie);
		}

		return success();
	}

}
