package gongnon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GongNonApplication {
    public static void main(String[] args) {
        SpringApplication.run(GongNonApplication.class, args);
        System.out.println("GongNon Application is running...");
    }
}
