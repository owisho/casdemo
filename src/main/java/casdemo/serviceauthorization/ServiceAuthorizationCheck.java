package casdemo.serviceauthorization;

import java.util.List;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.web.support.ArgumentExtractor;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;



@Component
public class ServiceAuthorizationCheck extends AbstractAction{

	@Autowired
	private ServicesManager servicesManager;
	
	@Autowired
	private List<ArgumentExtractor> argumentExtractors;
	
	private final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		final Service service = WebUtils.getService(argumentExtractors,context);
		if(service==null){
			return success();
		}
		if(this.servicesManager.getAllServices().isEmpty()){
			final String msg = String.format("No service definitions are found in the service manager."
					+ "Service [%s] will not be automatically authorized to request authentication.", service.getId());
			logger.warn(msg);
			throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_EMPTY_SVC_MGMR, msg);
		}
		
		final RegisteredService registeredService = this.servicesManager.findServiceBy(service);
		
		if(registeredService==null){
			final String msg = String.format("Service Management:missing service ."
					+ "Service [%s] is not found in service registry.", service.getId());
			logger.warn(msg);
			throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, msg);
		}
		if(!registeredService.getAccessStrategy().isServiceAccessAllowed()){
			
			final String msg = String.format("Service Managerment:Unauthorized Service Access."
					+ "Service [%s] is not allowed access via the service registry.", service.getId());
			logger.warn(msg);
			WebUtils.putUnauthorizedRedirectUrlIntoFlowScope(context, registeredService.getAccessStrategy().getUnauthorizedRedirectUrl());
			throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, msg);
		}
		
		return success();
	}

}
