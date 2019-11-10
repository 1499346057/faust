package org.mpi.faust;

import org.mpi.faust.exception.AppException;
import org.mpi.faust.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackageClasses = {
        FaustApplication.class,
        Jsr310JpaConverters.class
})
public class FaustApplication {

    public FaustApplication() {
    }

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
    public static void main(String[] args) {
        SpringApplication.run(FaustApplication.class, args);
    }
}
