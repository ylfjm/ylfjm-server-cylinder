package com.github.ylfjm.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceProxyConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    // @Bean
    // public DataSourceProxy dataSourceProxy(DataSource dataSource) {
    //     return new DataSourceProxy(dataSource);
    // }
    //
    // @Bean
    // public SqlSessionFactory sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
    //     SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    //     sqlSessionFactoryBean.setDataSource(dataSourceProxy);
    //     return sqlSessionFactoryBean.getObject();
    // }

}
