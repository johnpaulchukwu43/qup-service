package com.jworks.qup.service.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.jworks"})
@ComponentScan(basePackages = {"com.jworks"})
public class RepositoryConfig {

    @Value("${qup.service.datasource.primary.max.connection.pool-size:5}")
    private int dataSourceMaxPoolSize;

    @Value("${qup.service.datasource.primary.driverClassName}")
    private String dbDriverClassName;

    //--
    @Value("${qup.service.datasource.primary.url}")
    private String dataSourceUrl;

    @Value("${qup.service.datasource.primary.username}")
    private String dataSourceUsername;

    @Value("${qup.service.datasource.primary.password}")
    private String dataSourcePassword;


    @Value("${qup.service.liquibase.changelog}")
    private String liquidbaseChangeLogPath;

    @Value("${qup.service.liquibase.enable}")
    private boolean enableLiquibase;



    @Bean(name = {"dataSource"})
    @Primary
    public DataSource dataSource() {
        log.debug("Providing datasource bean..");
        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("AfroPixelsHikariPool");
        ds.setDriverClassName(dbDriverClassName);
        ds.setJdbcUrl(dataSourceUrl);
        ds.setUsername(dataSourceUsername);
        ds.setPassword(dataSourcePassword);
        ds.setMaximumPoolSize(dataSourceMaxPoolSize);
        return ds;
    }

    @Bean
    public DataSource throttlerDataSource(@Qualifier("dataSource") DataSource dataSource) {
        return dataSource;
    }

    @Bean(name = {"entityManagerFactory"})
    @DependsOn("dataSource")
    @Primary
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("dataSource") DataSource dataSource,
                                                                                         @Value("${qup.service.jpa.packages-to-scan}") String packagesToScan,
                                                                                         @Value("${qup.service.jpa.hibernate.ddl-auto: none}") String ddl,
                                                                                         @Value("${qup.service.jpa.hibernate.dialect: none}") String dialect,
                                                                                         @Value("${qup.service.jpa.hibernate.show-sql: false}") Boolean showSql) {

        log.debug("Providing entity manager factory bean..");
        ArrayList asList = new ArrayList((Collection) Arrays.asList(packagesToScan.split(",")).stream().filter((s) -> {
            return !s.isEmpty();
        }).collect(Collectors.toList()));

        log.debug("Net packagesToScan = " + asList);
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setPackagesToScan((String[])asList.toArray(new String[0]));
        factoryBean.setJpaPropertyMap(this.additionalProperties(dialect, showSql, ddl));
        return factoryBean;
    }

    @Bean(name = "defaultNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate defaultNamedParameterJdbcTemplate(@Qualifier("dataSource") DataSource dSource) {
        return new NamedParameterJdbcTemplate(dSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    private Map<String, String> additionalProperties(String hibernateDialect, Boolean showSql, String ddl) {
        Map<String, String> props = new HashMap();
        props.put("hibernate.show_sql", showSql.toString());
        props.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.hbm2ddl.auto", ddl);
        return props;
    }

    @Bean
    @Primary
    public SpringLiquibase liquibase(@Qualifier("dataSource") DataSource dSource) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dSource);
        liquibase.setChangeLog(liquidbaseChangeLogPath);
        liquibase.setShouldRun(enableLiquibase);
        return liquibase;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
