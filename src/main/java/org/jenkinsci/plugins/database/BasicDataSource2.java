package org.jenkinsci.plugins.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * HikariCP-based {@link DataSource} with convenience methods.
 * Replaces the deprecated commons-dbcp2 BasicDataSource.
 *
 * @author Kohsuke Kawaguchi
 */
public class BasicDataSource2 {
    private final HikariConfig config;
    
    public BasicDataSource2() {
        this.config = new HikariConfig();
        // Set sensible defaults
        config.setAutoCommit(true);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(10);
    }

    public void setDriverClass(Class<? extends Driver> driverClass) {
        config.setDriverClassName(driverClass.getName());
    }

    public void setDriverClassName(String driverClassName) {
        config.setDriverClassName(driverClassName);
    }

    public void setDriverClassLoader(ClassLoader classLoader) {
        // HikariCP doesn't have direct setDriverClassLoader, but we can set it on the config
        // The driver will be loaded by the current thread's context classloader
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    public void setUrl(String url) {
        config.setJdbcUrl(url);
    }

    public void setUsername(String username) {
        config.setUsername(username);
    }

    public void setPassword(String password) {
        config.setPassword(password);
    }

    public void setValidationQuery(String validationQuery) {
        if (validationQuery != null && !validationQuery.isEmpty()) {
            config.setConnectionTestQuery(validationQuery);
        }
    }

    public void addConnectionProperty(String name, String value) {
        config.addDataSourceProperty(name, value);
    }

    public void setInitialSize(Integer initialSize) {
        if (initialSize != null) {
            config.setMinimumIdle(initialSize);
        }
    }

    public void setMaxTotal(Integer maxTotal) {
        if (maxTotal != null) {
            config.setMaximumPoolSize(maxTotal);
        }
    }

    public void setMaxIdle(Integer maxIdle) {
        // HikariCP manages idle connections automatically
        // We don't need to set this explicitly
    }

    public void setMinIdle(Integer minIdle) {
        if (minIdle != null) {
            config.setMinimumIdle(minIdle);
        }
    }

    public DataSource createDataSource() throws SQLException {
        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
            throw new SQLException("Failed to create HikariCP DataSource", e);
        }
    }
}
