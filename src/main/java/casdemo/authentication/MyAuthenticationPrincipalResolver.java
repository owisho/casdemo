package casdemo.authentication;

import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.PrincipalFactory;
import org.jasig.cas.authentication.principal.PrincipalResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("myAuthenticationPrincipalResolver")
public class MyAuthenticationPrincipalResolver implements PrincipalResolver{

	@Autowired
	@Qualifier("defaultPrincipalFactory")
	private PrincipalFactory defaultPrincipalFactory;
	
	@Override
	public org.jasig.cas.authentication.principal.Principal resolve(
			Credential credential) {
		
		UsernamePasswordCredential ucredential = (UsernamePasswordCredential)credential;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("username", ucredential.getUsername());
		return this.defaultPrincipalFactory.createPrincipal(credential.getId(),map);
	}

	@Override
	public boolean supports(Credential credential) {
		return credential != null && credential.getId() != null;
	}

}
