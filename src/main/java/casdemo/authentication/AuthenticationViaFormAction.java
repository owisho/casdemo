package casdemo.authentication;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationContext;
import org.jasig.cas.authentication.AuthenticationContextBuilder;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.AuthenticationSystemSupport;
import org.jasig.cas.authentication.AuthenticationTransaction;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.DefaultAuthenticationContextBuilder;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component("authenticationViaFormAction")
public class AuthenticationViaFormAction {

	@Autowired
	private AuthenticationSystemSupport authenticationSystemSupport;//DefaultAuthenticationSystemSupport
	
	@Autowired
	private CentralAuthenticationService centralAuthenticationService;
	
	public final Event submit(final RequestContext context,
			final Credential credential) {
		return createTicketGrantingTicket(context, credential);
	}

	public Event createTicketGrantingTicket(final RequestContext context,
			final Credential credential) {
		try {
			final Service service = WebUtils.getService(context);
			final AuthenticationContextBuilder builder = new DefaultAuthenticationContextBuilder(
                    this.authenticationSystemSupport.getPrincipalElectionStrategy());//DefaultPrincipalElectionStrategy
			final AuthenticationTransaction transaction = AuthenticationTransaction.wrap(credential);
			//将credential信息放入authenticationContext中
			this.authenticationSystemSupport.getAuthenticationTransactionManager().handle(transaction, builder);//DefaultAuthenticationTransactionManager
			final AuthenticationContext authenticationContext = builder.build(service);
			
			final TicketGrantingTicket tgt = this.centralAuthenticationService.createTicketGrantingTicket(authenticationContext);
			WebUtils.putTicketGrantingTicketInScopes(context, tgt);
			return new Event(this,"success");
			
		}catch(AuthenticationException e){
			return new Event(this, "authenticationFailure", new LocalAttributeMap("error", e));
		} catch (Exception e) {
			return new Event(this, "error", new LocalAttributeMap("error", e));
		}
	}

}
