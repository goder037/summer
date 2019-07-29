package com.rocket.test.web;

import com.rocket.summer.framework.context.annotation.ComponentScan;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan("com.csonezp")
public class Config {
}
