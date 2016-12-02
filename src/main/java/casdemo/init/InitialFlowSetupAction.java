package casdemo.init;

import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class InitialFlowSetupAction extends AbstractAction {

	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		return success();
	}

}
