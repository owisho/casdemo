package casdemo.authentication;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Principal;
import org.springframework.stereotype.Component;

/**
 * 用来校验用户登录
 * @author owisho
 *
 */
@Component("myAuthenticationHandler")
public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler{
	
	@Override
	protected HandlerResult authenticateUsernamePasswordInternal(
			UsernamePasswordCredential credential)
			throws GeneralSecurityException, PreventedException {
		final String username = credential.getUsername();
		final String password = credential.getPassword();
		final Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put("password", password);
		Principal principal = this.principalFactory.createPrincipal(credential.getId(), attributes);
		
		try {
			check(username,password);
		} catch (AuthenticationException e) {
			throw new FailedLoginException("用户名或密码错误");
		}
		return createHandlerResult(credential, principal, null);
	}

	private void check(String username,String password) throws AuthenticationException{
		if(username.equals("owisho")&&password.equals("123456")){
			return;
		}else{
			throw new AuthenticationException();
		}
	}
	
}
