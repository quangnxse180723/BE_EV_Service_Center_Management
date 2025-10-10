package swp.group4.be_ev_service_center_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EntityScan(basePackages = {"swp.group4.be_ev_service_center_management.entity"})
public class BeEvServiceCenterManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeEvServiceCenterManagementApplication.class, args);
	}

}
