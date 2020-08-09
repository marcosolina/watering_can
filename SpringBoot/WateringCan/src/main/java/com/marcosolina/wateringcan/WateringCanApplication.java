package com.marcosolina.wateringcan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;
import com.marcosolina.wateringcan.utils.WUtils;

@SpringBootApplication
public class WateringCanApplication {

	@Autowired
	private Environment env;
	
	@Autowired
	private WateringConfigService config;

	public static void main(String[] args) {
		SpringApplication.run(WateringCanApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			WUtils.setContextPath(env.getProperty("server.servlet.context-path"));
			WUtils.arduinoCommandsPort(Integer.parseInt(env.getProperty("com.marcosolina.wateringcan.arduino.commands.port")));
			WUtils.setMlPerSecond(Integer.parseInt(env.getProperty("com.marcosolina.wateringcan.pump.mlpersecond", "0")));
			config.loadPumpsConfig();
		};
	}

}
