package Application;

import Database.UserDatabase;
import Database.ClassroomDatabase;
import Database.GroupDatabase;
import Database.GroupCalDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


import javax.annotation.PostConstruct;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {"Application", "Controller", "Service"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        UserDatabase.fillHashMap();
        ClassroomDatabase.fillHashMap();
        GroupDatabase.fillHashMap();
        GroupCalDatabase.fillHashMap();
        System.out.println("user,group,classroom databases loaded .");
    }
}
