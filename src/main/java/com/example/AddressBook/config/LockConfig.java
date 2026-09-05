package com.example.AddressBook.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;

@Configuration
public class LockConfig {

    @Bean
    public JdbcLockRegistry jdbcLockRegistry(DataSource dataSource) {
        DefaultLockRepository lockRepository =
                new DefaultLockRepository(dataSource);

        return new JdbcLockRegistry(lockRepository);
    }
}
