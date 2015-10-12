package hxk.interceptor;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
/**
 * @author hxk
 * @description
 *2015年10月10日  下午2:43:06
 */
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import hxk.dao.FunctionDao;
import hxk.dao.RoleDao;
import hxk.dao.UserDao;
import hxk.model.Function;
import hxk.model.Role;
import hxk.model.User;  


/**
 * 
 * @author hxk
 * @description 自定义的验证Realm
 * 	登录时的账号验证..
 * 	访问时的权限验证..
 *2015年10月12日  下午3:32:33
 */
public class CustomRealm extends AuthorizingRealm{  
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private FunctionDao functionDao;
    
    @Resource
    private RoleDao roleDao;
    
    /** @description 添加角色	
     * @param username
     * @param info
     *2015年10月12日  下午2:40:39
     *返回类型:void	
     */
    private void addRole(String username, SimpleAuthorizationInfo info) {
	List<Role> roles = roleDao.findByName(username);
	for (Role role : roles) {
	    info.addRole(role.getRoleName());
	}
    }

    /**
     * @description 添加权限	
     * @param username
     * @param info
     * @return
     *2015年10月12日  下午2:42:15
     *返回类型:SimpleAuthorizationInfo
     */
    private SimpleAuthorizationInfo addPermission(String username,SimpleAuthorizationInfo info) {
	List<Function> functions = functionDao.findByName(username);
	for (Function function : functions) {
	    info.addStringPermission(function.getUrl());//添加权限  
	}
	return info;  
    }  
  
    
    
    
    /**
     * 获取授权信息
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {  
        //用户名  
        String username = (String) principals.fromRealm(getName()).iterator().next(); 
        
        //根据用户名来添加相应的权限和角色
        if(!StringUtils.isEmpty(username)){
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            addPermission(username,info);
            addRole(username, info);
            return info;
            
        }
         
        return null;  
    }

   
   /** 
    * 登录验证 
    */  
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken ) throws AuthenticationException {  
        //令牌——基于用户名和密码的令牌  
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;  
        //令牌中可以取出用户名密码  
        String accountName = token.getUsername();
        
        //让shiro框架去验证账号密码
        if(!StringUtils.isEmpty(accountName)){
            User user = userDao.find(accountName);
            if(user != null){
        	return new SimpleAuthenticationInfo(user.getName(), user.getPwd(), getName());
            }
        }
        
        return null;
    }  
  
}  
