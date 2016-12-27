package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class MyFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String str = ((HttpServletRequest) request).getHeader("Cookie");
		System.out.println("服务器头文件："+str);
		//查看cookie
		Cookie[] cookies = ((HttpServletRequest)request).getCookies();
		if(null!=cookies){
			for(Cookie cookie:cookies){
				System.out.println("服务器cookie值："+cookie.getValue());
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}

}
