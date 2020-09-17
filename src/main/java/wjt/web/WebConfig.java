package wjt.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import wjt.controller.AdminController;

@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {AdminController.class})
public class WebConfig {
}
