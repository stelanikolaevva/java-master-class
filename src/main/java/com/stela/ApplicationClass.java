package com.stela;

import com.stela.car.Brand;
import com.stela.car.Car;
import com.stela.car.CarRepository;
import com.stela.user.AppUser;
import com.stela.user.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class ApplicationClass {

    static void main(String[] args) {
        SpringApplication.run(ApplicationClass.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(AppUserRepository appUserRepository,
                                        CarRepository carRepository) {
        return args -> {
            AppUser bob = new AppUser("Bob");
            AppUser alex = new AppUser("Alex");
            appUserRepository.saveAll(List.of(bob, alex));

            Car car = new Car("B 1310 CH",
                    new BigDecimal("23.99"),
                    Brand.MERCEDES,
                    false);
            Car car2 =
                    new Car("CB 2231 TH",
                            new BigDecimal("33.99"),
                            Brand.MERCEDES,
                            true);

            Car car3 =
                    new Car("CB 3399 KH",
                            new BigDecimal("25.99"),
                            Brand.TOYOTA,
                            true);
            Car car4 =
                    new Car("B 1122 OK",
                            new BigDecimal("27.99"),
                            Brand.AUDI,
                            false);

            carRepository.saveAll(List.of(car, car2, car3, car4));
        };
    }
}
