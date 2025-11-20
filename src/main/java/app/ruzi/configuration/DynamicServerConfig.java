package app.ruzi.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.InetAddress;

@Configuration
public class DynamicServerConfig {

    private String localIp;

    @PostConstruct
    public void init() throws Exception {
        // 🔹 Lokal IP olish (DHCP bo‘lsa ham to‘g‘ri topadi)
        localIp = InetAddress.getLocalHost().getHostAddress();
        System.out.println("✅ Local IP detected: " + localIp);

        // 🔹 Keycloak va MinIO uchun global System properties
        System.setProperty("SERVER_IP", localIp);
        System.setProperty("OAUTH_ISSUER_URI", "http://" + localIp + ":8080/realms/ruzi-realm");
        System.setProperty("OAUTH_JWK_URI", "http://" + localIp + ":8080/realms/ruzi-realm/protocol/openid-connect/certs");
        System.setProperty("MINIO_URL", "http://" + localIp + ":9000");
        System.setProperty("DB_URL", "jdbc:postgresql://" + localIp + ":5432/sales");
    }

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password
    ) {
        String jdbcUrl = System.getProperty("DB_URL");
        System.out.println("🔗 Using dynamic JDBC URL: " + jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setPoolName("DynamicHikariPool");

        return new HikariDataSource(config);
    }

    public String getLocalIp() {
        return localIp;
    }
}
