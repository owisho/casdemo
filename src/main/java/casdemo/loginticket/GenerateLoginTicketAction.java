package casdemo.loginticket;

import org.jasig.cas.ticket.UniqueTicketIdGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

public class GenerateLoginTicketAction{

	private static final String PREFIX = "LT";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UniqueTicketIdGenerator ticketIdGenerator;
	
	public final String generate(final RequestContext context) {
        final String loginTicket = this.ticketIdGenerator.getNewTicketId(PREFIX);
        logger.debug("Generated login ticket {}", loginTicket);
        WebUtils.putLoginTicket(context, loginTicket);
        return "generated";
    }
	
}
