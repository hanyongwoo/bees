package com.automation.abi.bees.conf;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // DEV DB
public class DevDataSource {
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
		return new TomcatServletWebServerFactory() {
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatWebServer(tomcat);
			}
			
			@Override
			protected void postProcessContext(Context context) {
				// 개발 jndi1
				context.getNamingResources().addResource(getResource("jndi1", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/devjndi1?serverTimezone=UTC", "root", "root"));
				
				// 개발 jndi2
				context.getNamingResources().addResource(getResource("jndi2", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/devjndi2?serverTimezone=UTC", "root", "root")); 
			}
		};
	}
	
	public ContextResource getResource(String name, String driverClassName, String url, String username, String password) {
		ContextResource resource = new ContextResource();
		resource.setName(name); // 사용될 jndi 이름
		resource.setType("javax.sql.DataSource");
		resource.setAuth("Container");
		resource.setProperty("factory", "org.apache.commons.dbcp2.BasicDataSourceFactory");
		
		// datasource 정보
		resource.setProperty("driverClassName", driverClassName);
		resource.setProperty("url", url);
		resource.setProperty("username", username);
		resource.setProperty("password", password);
		
		return resource;
	}
}
