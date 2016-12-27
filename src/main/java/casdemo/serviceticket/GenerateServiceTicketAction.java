package casdemo.serviceticket;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationContext;
import org.jasig.cas.authentication.AuthenticationContextBuilder;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.AuthenticationSystemSupport;
import org.jasig.cas.authentication.DefaultAuthenticationContextBuilder;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.AbstractTicketException;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.registry.TicketRegistrySupport;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class GenerateServiceTicketAction extends AbstractAction {

	@Autowired
	@Qualifier("centralAuthenticationService")
	private CentralAuthenticationService centralAuthenticationService;

	@Autowired
	@Qualifier("defaultAuthenticationSystemSupport")
	private AuthenticationSystemSupport authenticationSystemSupport;

	@Autowired
	@Qualifier("defaultTicketRegistrySupport")
	private TicketRegistrySupport ticketRegistrySupport;

	@Autowired
	@Qualifier("servicesManager")
	private ServicesManager servicesManager;

	@Override
	protected Event doExecute(RequestContext context) throws Exception {

		final Service service = WebUtils.getService(context);
		
		HttpServletRequest request = WebUtils.getHttpServletRequest(context);
		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie:cookies){
			System.out.println(cookie.getValue());
		}
		final String ticketGrantingTicket = WebUtils
				.getTicketGrantingTicketId(context);
		try {

			final Authentication authentication = this.ticketRegistrySupport
					.getAuthenticationFrom(ticketGrantingTicket);

			if (authentication == null) {
				throw new InvalidTicketException(new AuthenticationException(),
						ticketGrantingTicket);
			}
			final RegisteredService registeredService = servicesManager
					.findServiceBy(service);
			WebUtils.putRegisteredService(context, registeredService);
			WebUtils.putService(context, service);
			WebUtils.putUnauthorizedRedirectUrlIntoFlowScope(context,
					registeredService.getAccessStrategy()
							.getUnauthorizedRedirectUrl());

			final AuthenticationContextBuilder builder = new DefaultAuthenticationContextBuilder(
					this.authenticationSystemSupport
							.getPrincipalElectionStrategy());

			final AuthenticationContext authenticationContext = builder
					.collect(WebUtils.getCredential(context))
					.collect(authentication).build(service);

			final ServiceTicket serviceTicketId = this.centralAuthenticationService
					.grantServiceTicket(ticketGrantingTicket, service,
							authenticationContext);
			WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
			return success();
		} catch (final AuthenticationException e) {
			logger.error(
					"Could not verify credentials to grant service ticket", e);
			return error();
		} catch (final AbstractTicketException e) {
			if (e instanceof InvalidTicketException) {
				this.centralAuthenticationService
						.destroyTicketGrantingTicket(ticketGrantingTicket);
			}
			return new Event(this, "error", new LocalAttributeMap<Object>(
					"error", e));
		}

	}
}
