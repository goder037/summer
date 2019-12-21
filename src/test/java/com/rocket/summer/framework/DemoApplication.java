package com.rocket.summer.framework;


import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.cglib.core.DebuggingClassWriter;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;
import com.rocket.summer.framework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class DemoApplication {

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) {
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\clazz\\summer");
		SpringApplication.run(DemoApplication.class, args);
	}

}
