package com.example.server.handler;

import com.example.server.conf.SpringCfg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * Created by remote on 11/13/16.
 */
@Component
public class ProtocolInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private ServerHandler serverHandler;

    @Autowired
    private SpringCfg cfg;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("httpCodec", new HttpServerCodec());
        pipeline.addLast("handler", serverHandler);
        pipeline.addLast("trafficChannel", new ChannelTrafficShapingHandler(cfg.getWriteLimit(), cfg.getReadLimit()));
    }
}
