package com.zchi.common.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@ConditionalOnClass({DruidDataSource.class,StatViewServlet.class})
@ConditionalOnWebApplication
public class DruidStatsAutoConfiguration {


    @Bean ServletRegistrationBean servletRegistrationBean(DataSource dataSource) {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        StatViewServlet statViewServlet = new StatViewServlet();
        servletRegistrationBean.setName("DruidStatView");
        servletRegistrationBean.setServlet(statViewServlet);
        servletRegistrationBean.setUrlMappings(Arrays.asList("/druid/*"));
        if(dataSource instanceof DruidDataSource) {
            DruidDataSource ddataSource = (DruidDataSource)dataSource;
            try {
                ddataSource.addFilters("stat");
                ddataSource.setStatLogger(null);
            } catch (Exception e) {
            }
        }

        return servletRegistrationBean;
    }
}
