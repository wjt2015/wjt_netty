package wjt.netty.httpfile;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around(value = "execution(public * java.io.RandomAccessFile.close( .. ))")
    public Object aroundClose(final ProceedingJoinPoint pjp) throws Throwable {
        log.info("before;pjp={};", pjp);
        final Signature signature = pjp.getSignature();
        final Object retObj = pjp.proceed();
        log.info("after;signature={};retObj={};", signature, retObj);
        return retObj;
    }
}
