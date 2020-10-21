package wjt.netty.heart;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.SourceLocation;
import org.springframework.stereotype.Component;

/**
 * 练习spring aop;
 */
@Slf4j
@Aspect
@Component
public class HeartAspect {

    @Around(value = "execution(public * wjt.netty.heart.HeartbeatServerHandler.*( .. ))")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();
        Object[] args = pjp.getArgs();
        SourceLocation sourceLocation = pjp.getSourceLocation();
        Signature signature = pjp.getSignature();
        Object ret = pjp.proceed();
        log.info("service_finish!elapsed={}ms;args={};sourceLocation={};signature={};",
                (System.currentTimeMillis() - start), args, sourceLocation, signature);
        return ret;
    }
}
