package com.example.server.handler;

import com.example.server.conf.SpringCfg;
import com.example.service.WeightService;
import com.sun.jersey.api.uri.UriTemplate;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by remote on 11/13/16.
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler {
    @Autowired
    private SpringCfg springCfg;

    @Autowired
    private WeightService weightService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            if (request.decoderResult().isFailure()) {
                Throwable cause = request.decoderResult().cause();
                response(ctx, request, cause.getMessage(), BAD_REQUEST);
            } else {
                //TODO to refactor
                final Map<String, String> map;
                if (patternHandler1.isMatch(request.uri())) {
                    map = patternHandler1.getMap();
                } else if (patternHandler2.isMatch(request.uri())) {
                    map = patternHandler2.getMap();
                } else {
                    response(ctx, request, null, HttpResponseStatus.NOT_FOUND);
                    return;
                }
                if (request.method() != HttpMethod.GET) {
                    response(ctx, request, null, HttpResponseStatus.METHOD_NOT_ALLOWED);
                    return;
                }
                try {
                    int index = Integer.parseInt(map.get("index"));
                    int level = Integer.parseInt(map.get("level"));
                    Double result = weightService.getHumanEdgeWeight(level, index);
                    response(ctx, request, result.toString(), HttpResponseStatus.OK);
                } catch (IllegalArgumentException e) {
                    response(ctx, request, e.getMessage(), HttpResponseStatus.BAD_REQUEST);
                }
            }
            return;
        }
        response(ctx, null, null, HttpResponseStatus.NOT_IMPLEMENTED);
    }

    private void response(ChannelHandlerContext ctx, HttpRequest request, String message, HttpResponseStatus status) throws UnsupportedEncodingException {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.wrappedBuffer(message != null ? message.getBytes("UTF-8") : new byte[0]));

        boolean keepAlive = springCfg.isKeepAlive();

        String origin = request != null ? request.headers().get(ORIGIN) : null;
        if (origin != null) {
            response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }

        if (keepAlive) {
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            response.headers().set(CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            ctx.writeAndFlush(response);
        } else {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        try {
            response(ctx, null, "An error occurred", INTERNAL_SERVER_ERROR);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ctx.close();
    }


    //quick hack
    PatternHandler patternHandler1 = new PatternHandler("/weight?level={level}&index={index}");
    PatternHandler patternHandler2 = new PatternHandler("/weight/{level}/{index}");

    private class PatternHandler {
        private final UriTemplate uriTemplate;
        private final Map<String, String> map = new HashMap<>();

        PatternHandler(String str) {
            uriTemplate = new UriTemplate(str);
        }

        public boolean isMatch(String str) {
            return uriTemplate.match(str, map);
        }

        public Map<String, String> getMap() {
            return map;
        }

    }


}
