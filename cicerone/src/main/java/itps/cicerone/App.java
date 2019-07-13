package itps.cicerone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration 
@Configuration 
@SpringBootApplication(scanBasePackages = {"itps"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
