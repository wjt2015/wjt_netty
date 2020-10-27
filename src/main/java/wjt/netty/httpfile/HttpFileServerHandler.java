package wjt.netty.httpfile;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.MyChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

@Slf4j
@ChannelHandler.Sharable
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.info("recv;method={};uri={};", request.method(), request.uri());
        messageReceived(ctx, request);
    }

    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) {

        if (request.decoderResult().isFailure()) {
            //400;
            log.error("decode error!");
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (request.method() != HttpMethod.GET) {
            //405;
            log.error("request method error!");
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = request.uri();
        //根据uri构造本地文件路径;
        final String path = sanitizeUri(uri);
        if (path == null) {
            //403;路径不合法;
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        final File file = new File(path);

        if (file.isHidden() || !file.exists()) {
            log.error("the file is hidden or lost!");
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                //如果文件是文件夹且以"/"结尾,则认为是访问一个文件夹,展示文件列表;
                sendFileList(ctx, file);
            } else {
                sendRedirect(ctx, uri + "/");
            }
            return;
        }

        if (!file.isFile()) {
            //如果新建的文件不是文件类型;
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        log.info("ctx={};request={};", ctx, request);
        HttpResponse httpResponse = null;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

            final long fileSize = randomAccessFile.length();
            //创建响应对象;
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            //设置响应头和响应体;
            httpResponse.headers().set("Content-Length", String.valueOf(fileSize));
            setContentTypeHeader(httpResponse, file);
            httpResponse.headers().set("connection", "keep-alive");

            //发出response;
            if (httpResponse != null) {
                //向客户端返回;
                ctx.write(httpResponse);
            }
            //发送文件;
            ctx.write(new MyChunkedFile(randomAccessFile, 0, fileSize, 8192), ctx.newProgressivePromise())
                    .addListener(new ChannelProgressiveFutureListener() {
                        @Override
                        public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                            log.info("process={};total={};", progress, total);
                        }

                        @Override
                        public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                            log.info("transfer complete!");
                        }
                    });
            //应用chunk编码时,最后需要发送一个结束编码的看空消息体进行标记,表示所有消息体已成功发送完成;
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            log.error("build httpResonse error!", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.close();
            log.error("netty server error!cause={};", cause);
        }
    }

    private String sanitizeUri(String uri) {
        try {
            String decode = URLDecoder.decode(uri, "UTF-8");
            decode = (decode.startsWith("/") ? decode.replaceFirst("/", "") : decode);
            final String userDir = System.getProperty("user.dir");
            String path = (userDir.endsWith("/") ? (userDir + decode) : (userDir + "/" + decode));
            log.info("userDir={};path={};", userDir, path);

            return path;
        } catch (UnsupportedEncodingException e) {
            log.error("decode error!uri={};", uri);
            return null;
        }
    }

    private static final Pattern ALLOW_FILE_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private static void sendFileList(ChannelHandlerContext ctx, final File dir) {

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        httpResponse.headers().set("Content-Type", "text/html; charset=UTF-8");
        //追加文本;
        final StringBuilder ret = new StringBuilder();
        final String dirPath = dir.getPath();

        ret.append("<!DOCTYPE html>\r\n");
        ret.append("<html><head><title>");
        ret.append(dirPath);
        ret.append(" 目录：");
        ret.append("</title></head><body>\r\n");
        ret.append("<h3>");
        ret.append(dirPath).append(" 目录：");
        ret.append("</h3>\r\n");
        ret.append("<ul>");
        ret.append("<li>链接：<a href=\"../\">..</a></li>\r\n");

        log.info("dir.listFiles={};", dir.listFiles());
        //遍历文件,添加超链接;
        for (File f : dir.listFiles()) {
            //文件过滤;
            if (!f.isHidden() && f.canRead() /*&& ALLOW_FILE_FILE_NAME.matcher(f.getName()).matches()*/) {
                //拼接超链接即可
                ret.append("<li>链接：<a href=\"");
                ret.append(f.getName());
                ret.append("\">");
                ret.append(f.getName());
                ret.append("</a></li>\r\n");
            }
        }
        ret.append("</ul></body></html>\r\n");
        httpResponse.content().writeCharSequence(ret.toString(), CharsetUtil.UTF_8);
        //发送响应之后关闭发送链接;
        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        //加入新的uri;
        defaultFullHttpResponse.headers().set("location", newUri);
        // 响应发送完毕之后关闭发送链接;
        ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(final ChannelHandlerContext ctx, HttpResponseStatus httpResponseStatus) {
        //建立响应对象;
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, Unpooled.copiedBuffer("Fail " + httpResponseStatus.toString(), CharsetUtil.UTF_8));
        defaultFullHttpResponse.headers().set("Content-Type", "text/plain; charset=UTF-8");
        //发送响应并关闭发送链接;
        ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
    }


    private static void setContentTypeHeader(HttpResponse httpResponse, File file) {
        //mime获取文件类型;
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        httpResponse.headers().set("Content-Type", mimetypesFileTypeMap.getContentType(file));
    }
}
