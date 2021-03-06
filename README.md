# SpringShiro

##项目说明 :  

这是一个shiro的入门Demo..
使用了Spring MVC,mybaits等技术..



###数据库设计 :

1.User : name--password  

2.Role : id--userid--roleName  

3.Function : id--userid--url  


*tinys普通用户只能访问index.jsp  

*admin用户通过添加了admin的permission,所以可以访问admin.jsp  

*role用户通过添加了role角色,所以可以访问role.jsp  




###这是最基本的shiro的运用..目的是让你快速了解shiro的机制..

*这个Demo体现shiro的地方主要在两个类以及shiro.xml的配置文件   

*CustomRealm : 处理了登录验证以及授权..  

*ShiroAction : 用来传递登录时的用户数据..转换为token传递给realm...之后根据结果做相应的逻辑处理..  

*shiro.xml : shiro的主要配置...    


	规则定义在以下地方 :  
	
	`<!-- 过滤链定义 -->  
        <property name="filterChainDefinitions">  
            <value>  
                /login.jsp* = anon  
                /index.jsp* = authc  
                /index.do* = authc  
                /admin.jsp*=authc,perms[/admin]
                /role.jsp*=authc,roles[role]
             </value>  
        </property> `


-----------------------------------------------------------------------------------------------------------------------------      
##2015-10-28更新 通过添加了以下内容来使用注解方式配置权限....
	
	`<!-- Support Shiro Annotation 必须放在springMVC配置文件中 -->

	<!-- 异常处理，权限注解会抛出异常，根据异常返回相应页面 -->
	<bean
		class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
		<property name="exceptionMappings">
			<props>
				<prop key="org.apache.shiro.authz.UnauthorizedException">unauth</prop>
				<prop key="org.apache.shiro.authz.UnauthenticatedException">login</prop>
			</props>
		</property>
	</bean>
	<bean
		class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"
		depends-on="lifecycleBeanPostProcessor" />
	<bean
		class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
		<property name="securityManager" ref="securityManager" />
	</bean>
	<!-- end -->`
	
### 修改了过滤链  

	`<!-- 过滤链定义 -->   

	//简单的讲就是把需要特别处理的路径写到前面,越特殊写到越前
        <property name="filterChainDefinitions">  
            <value>  
                <!-- 注意这里需要把前缀写全.../shiro这里 -->  
                
            	/shiro/login.do*=anon  
            	
                /login.jsp* = anon   
                
                /admin.jsp*=authc,perms[/admin]  
                
                /role.jsp*=authc,roles[role]  
                
                /** = authc  
                
             </value>  
        </property>`  
        
 ----------------------------------------------------------------------------------------------------------------------------

## 15-10-29  添加了使用ehcache的缓存机制  

    `<!-- securityManager -->  
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">  
        <property name="realm" ref="myRealm" />  
         <property name="cacheManager" ref="shiroEhcacheManager" />
    </bean>  
    
    <!-- 用户授权信息Cache, 采用EhCache，需要的话就配置上此信息 -->
    <bean id="shiroEhcacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
        <property name="cacheManagerConfigFile" value="classpath:ehcache-shiro.xml" />
    </bean>`
    
 

### 添加了redis缓存...  
 
    `<!-- 缓存相关配置  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!begin -->    
   
    <!-- securityManager -->  
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">  
        <property name="realm" ref="myRealm" />  
        <!-- 配置ehcache缓存,如果是本机,没分布式的话,可以考虑就选择ehcache缓存 -->
         <property name="cacheManager" ref="shiroEhcacheManager" />
         <!-- 如果有多台机子的话,可以考虑部署redis分布式缓存.. -->
         <property name="sessionManager" ref="sessionManager" />
    </bean>  
    
    <!-- 用户授权信息Cache, 采用EhCache，需要的话就配置上此信息 -->
    <bean id="shiroEhcacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
        <property name="cacheManagerConfigFile" value="classpath:ehcache-shiro.xml" />
    </bean>
    
    
     <!--保持 Session 到 Redis-->
    <bean id="redisManager" class="hxk.util.redis.RedisManager"/>

	<!-- 配置redis的ＤＡＯ -->
    <bean id="redisSessionDAO" class="hxk.util.redis.RedisSessionDAO">
        <property name="redisManager" ref="redisManager"/>
        <property name="timeToLiveSeconds" value="180"/>
    </bean>

	<!-- 配置shiro提供的session管理者.. -->
    <bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
        <property name="sessionDAO" ref="redisSessionDAO" />
        <!-- sessionIdCookie的实现,用于重写覆盖容器默认的JSESSIONID -->  
        <property name="sessionIdCookie" ref="sharesession" />  
    </bean>
    
    
    
    <!-- 这里很重要,配置每次读取的cookie的名字..不会因为cookie的问题而读取到不同(错误的)jessessionid..
    	   目的就是让用户整个访问过程中,项目读取到用户浏览器的同一个cookie..就会有一样的jessessionid..
     -->
    <!-- sessionIdCookie的实现,用于重写覆盖容器默认的JSESSIONID -->  
    <bean id="sharesession" class="org.apache.shiro.web.servlet.SimpleCookie">  
        <!-- cookie的name,对应的默认是 JSESSIONID -->  
        <constructor-arg name="name" value="SHAREJSESSIONID" />  
        <!-- jsessionId的path为 / 用于多个系统共享jsessionId -->  
        <property name="path" value="/" />  
        <property name="httpOnly" value="true"/>  
    </bean>  
     <!-- 缓存相关配置  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!end -->`  
