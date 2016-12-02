package casdemo.ticketgrantingticket;


import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class TicketGrantingTicketCheckAction extends AbstractAction {

    public static final String NOT_EXISTS = "notExists";

    public static final String INVALID = "invalid";

    public static final String VALID = "valid";
    
    @Autowired
    private CentralAuthenticationService centralAuthenticationService;

	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		final String tgtId = WebUtils.getTicketGrantingTicketId(context);
		if (!StringUtils.hasText(tgtId)) {
			return new Event(this, NOT_EXISTS);
		}
		try {
            final Ticket ticket = this.centralAuthenticationService.getTicket(tgtId, Ticket.class);
            if (ticket != null && !ticket.isExpired()) {
            	return new Event(this, VALID);
            }
            return new Event(this,INVALID);
        } catch (InvalidTicketException e) {
            logger.trace("Could not retrieve ticket id {} from registry.", e);
            return new Event(this,INVALID);
        }
	}

}
