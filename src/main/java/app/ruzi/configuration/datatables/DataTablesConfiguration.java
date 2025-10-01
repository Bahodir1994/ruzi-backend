package app.ruzi.configuration.datatables;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "app.ruzi.repository",
        repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class
)
public class DataTablesConfiguration {
}
