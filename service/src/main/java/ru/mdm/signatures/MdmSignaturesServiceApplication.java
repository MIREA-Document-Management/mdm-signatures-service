package ru.mdm.signatures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MdmSignaturesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdmSignaturesServiceApplication.class, args);
	}

}
