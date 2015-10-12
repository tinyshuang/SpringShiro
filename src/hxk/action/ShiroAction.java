package hxk.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hxk
 * @description  form表单登录处理
 *2015年10月10日  下午2:45:52
 */
@Controller
@RequestMapping("/shiro/")
public class ShiroAction {
    
    @RequestMapping("login")
    public String login(HttpServletRequest request){
	 //当前Subject  
	 Subject currentUser = SecurityUtils.getSubject();  
	 //传递token给shiro的realm
	 UsernamePasswordToken token = new UsernamePasswordToken(request.getParameter("username"),request.getParameter("password"));  
	 try {  
	     /* 
	      * 在调用了login方法后，SecurityManager会收到AuthenticationToken，并将其发送给已配置的Realm 
	      * ，执行必须的认证检查。每个Realm都能在必要时对提交的AuthenticationTokens作出反应。 
	      * 但是如果登录失败了会发生什么？如果用户提供了错误密码又会发生什么？通过对Shiro的运行时AuthenticationException做出反应 
	      * ，你可以控制失败 
	      */  
	     currentUser.login(token); 
	     return "index";
	 
	 } catch (AuthenticationException e) {//登录失败  
	     e.printStackTrace();  
	     request.setAttribute("msg", "不匹配的用户名和密码");  
	     return "login";
	 }  
    }
}
