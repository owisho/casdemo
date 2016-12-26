package casdemo.init;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceAccessStrategy;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import casdemo.cookie.CookieRetrievingCookieGenerator;

@Component
public class InitialFlowSetupAction extends AbstractAction {

	@Autowired
	private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator ;
	
	@Autowired
	private List<ArgumentExtractor> argumentExtractors;
	
	@Autowired
	private ServicesManager serviceManager;
	
	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		
		//获取cookie并将cookie放置到请求域和flow域中
		HttpServletRequest request = WebUtils.getHttpServletRequest(context);
		WebUtils.putTicketGrantingTicketInScopes(context,
                this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request));
		
		//获取service
		final Service service = WebUtils.getService(this.argumentExtractors, context);
		
		if(null!=service){
			final RegisteredService registeredService = this.serviceManager.findServiceBy(service);
			
			if(registeredService!=null&&registeredService.getAccessStrategy().isServiceAccessAllowed()){
				WebUtils.putRegisteredService(context, registeredService);
				final RegisteredServiceAccessStrategy accessStrategy = registeredService.getAccessStrategy();
				if(null!=accessStrategy){
					WebUtils.putUnauthorizedRedirectUrl(context, accessStrategy.getUnauthorizedRedirectUrl());
				}
			}
		}
		
		WebUtils.putService(context, service);
		
		return success();
	}

}
