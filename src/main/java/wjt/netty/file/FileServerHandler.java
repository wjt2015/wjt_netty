package wjt.netty.file;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 文件服务器;
 */
@Slf4j
public class FileServerHandler implements ChannelInboundHandler {

    private static final String CR = System.getProperty("line.separator");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ctx={};msg={};", ctx, msg);
        if (msg instanceof String) {
            final long start = System.currentTimeMillis();
            long size = 0;
            String fileName = (String) msg;
            File file = new File(fileName);
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file:" + file + CR);
                return;
            }
            ctx.write(file + " " + file.length() + CR);

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r")) {
                FileRegion fileRegion = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
                size = fileRegion.transferred();
                ctx.write(fileRegion);
                ctx.writeAndFlush(CR);
            } catch (Exception e) {
                log.error("randomAccessFile error!file={};", file, e);
            } finally {
                log.info("transfer finish!size={};elapsed={}ms;", size, (System.currentTimeMillis() - start));
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
