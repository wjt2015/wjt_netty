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

    @Around(value = "execution(public * java.io.RandomAccessFile.*( .. ))")
    public Object aroundClose(final ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();

        log.info("before;pjp={};", pjp);
        final Signature signature = pjp.getSignature();
        final Object retObj = pjp.proceed();
        log.info("after;elapsed={}ms;signature={};retObj={};", (System.currentTimeMillis() - start), signature, retObj);
        return retObj;
    }

    @Around(value = "execution(public * io.netty.channel.SimpleChannelInboundHandler.*( .. ))")
    public Object wjtAround(final ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();
        log.info("wjt;before;pjp={};", pjp);
        final Signature signature = pjp.getSignature();
        final Object retObj = pjp.proceed();
        log.info("wjt;after;elapsed={}ms;signature={};retObj={};", (System.currentTimeMillis() - start), signature, retObj);
        return retObj;
    }
}
