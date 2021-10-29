package com.database2;


import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

/****
 *
 * Configuration for Oracle Database
 * **/

@Configuration
@EnableAutoConfiguration
public class OracleDataSourceConfig {

    @Bean(name = "oracleDatasource")
    @ConfigurationProperties("spring.datasource.oracle")
    public DataSource dbDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="tm")
    @Autowired
    DataSourceTransactionManager tm(@Qualifier ("oracleDatasource") DataSource datasource) {
        DataSourceTransactionManager txm  = new DataSourceTransactionManager(datasource);
        return txm;
    }

    @Bean("jdbi")
    public Jdbi secondaryJdbi(@Qualifier("oracleDatasource") DataSource ds, List<JdbiPlugin> plugins, List<RowMapper<?>> mappers) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
        Jdbi jdbi = Jdbi.create(proxy);
        plugins.forEach(plugin -> jdbi.installPlugin(plugin));
        mappers.forEach(mapper -> jdbi.registerRowMapper(mapper));
        return jdbi;
    }

    @Bean
    public JdbiPlugin sqlObjectPlugin() {
        return new SqlObjectPlugin();
    }

//    @Bean("reviewRepository")
//    public ReviewRepository reviewRepo(@Qualifier("jdbi-1") Jdbi jdbi) {
//        return jdbi.onDemand(ReviewRepository.class);
//    }

}
