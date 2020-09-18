package wjt.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.MyDelegatingWebMvcConfiguration;
import wjt.controller.AdminController;

@Slf4j
@Configuration
@EnableWebMvc
@Import(value = {MyWebMvcConfigurer.class, MyDelegatingWebMvcConfiguration.class})
@ComponentScan(basePackageClasses = {AdminController.class})
public class WebConfig {
}
