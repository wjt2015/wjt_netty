package wjt.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.MyAbstractAnnotationConfigDispatcherServletInitializer;
import wjt.biz.BizConfig;

@Slf4j
public class MyWebAppInitializer extends MyAbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{BizConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        log.info("wjt;");
        return new String[]{"/*"};
    }
}
