package wjt.common;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

@Slf4j
public class AuxUtils {

    public static String byteBuf(final ByteBuf byteBuf) {
        byteBuf.markReaderIndex();
        int bytes = byteBuf.readableBytes();
        byte[] data = new byte[bytes];

        byteBuf.readBytes(data);

        byteBuf.resetReaderIndex();

        log.info("byteBuf_data={};", data);

        StringBuilder stringBuilder = new StringBuilder().append("[");
        int i = 0, size = data.length;
        for (byte v : data) {
            stringBuilder.append(v);
            i++;
            if (i < size) {
                stringBuilder.append(",");
            }else {
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }



    private static final AtomicLong ID=new AtomicLong(1L);

    public static long nextId(){
        return ID.getAndIncrement();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("The thread has been interrupted!", e);
        }
    }

    public static void await(final CountDownLatch countDownLatch, final long timeout) {

        try {
            if (timeout < 0) {
                countDownLatch.await();
            } else {
                long leftTime = timeout, endTs = System.currentTimeMillis() + timeout;
                while (leftTime > 0) {
                    try {
                        countDownLatch.await(leftTime, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("CountDownLatch error!countDownLatch={};leftTime={};", countDownLatch, leftTime, e);
                    } finally {
                        leftTime = (endTs - System.currentTimeMillis());
                    }
                }
            }
        } catch (Exception e) {
            log.error("CountDownLatch error!countDownLatch={};timeout={};", countDownLatch, timeout, e);
        }

    }

    public static void await(final Condition condition, final long millis) {
        try {
            if (millis < 0) {
                condition.await();
            } else {
                long leftTime = millis;
                final long endTs = System.currentTimeMillis() + millis;

                while (leftTime > 0) {
                    try {
                        condition.await(millis, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("cond await error!", e);
                    } finally {
                        leftTime = endTs - System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            log.error("cond await error!", e);
        }

    }


    public static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) (theUnsafe.get(null));
        } catch (Exception e) {
            log.error("get the unsafe error!", e);
        }

        return unsafe;
    }





}
