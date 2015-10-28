# SpringShiro

这是一个shiro的入门Demo..
使用了Spring MVC,mybaits等技术..

数据库设计 :

User : name--password
Role : id--userid--roleName
Function : id--userid--url

tinys普通用户只能访问index.jsp
admin用户通过添加了admin的permission,所以可以访问admin.jsp
role用户通过添加了role角色,所以可以访问role.jsp

这是最基本的shiro的运用..目的是让你快速了解shiro的机制..


这个Demo体现shiro的地方主要在两个类以及shiro.xml的配置文件
CustomRealm : 处理了登录验证以及授权..
ShiroAction : 用来传递登录时的用户数据..转换为token传递给realm...之后根据结果做相应的逻辑处理..
shiro.xml : shiro的主要配置...
	规则定义在以下地方 :
		<!-- 过滤链定义 -->  
        <property name="filterChainDefinitions">  
            <value>  
                /login.jsp* = anon  
                /index.jsp* = authc  
                /index.do* = authc  
                /admin.jsp*=authc,perms[/admin]
                /role.jsp*=authc,roles[role]
             </value>  
        </property>  


2015-10-28更新
--通过添加了以下内容来使用注解方式配置权限....
    <!-- Support Shiro Annotation 必须放在springMVC配置文件中 -->

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
    <!-- end -->
    
--修改了过滤链
<!-- 过滤链定义 -->  
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
        </property>  