package me.code;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class ChatClient {

    private final String address;
    private final int port;
    private final EventLoopGroup eventLoopGroup;

    public ChatClient(String address, int port) {
        this.address = address;
        this.port = port;
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    public void start() {

        Bootstrap bootstrap = new Bootstrap();

        try {
            Channel channel = bootstrap
                    .group(this.eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addFirst(new ChatClientHandler());
                        }
                    })
                    .connect(this.address, this.port).sync().channel();

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            while (!line.equalsIgnoreCase("exit")) {
                ByteBuf buf = Unpooled.buffer();

                if (line.toLowerCase().startsWith("/setname ")) {
                    String[] split = line.split("/setname ");
                    String name = split[1];

                    System.out.println(name);

                    buf.writeByte(0);
                    buf.writeInt(name.length());
                    buf.writeBytes(name.getBytes());

                    channel.writeAndFlush(buf);
                } else {
                    buf.writeByte(1);
                    buf.writeInt(line.length());
                    buf.writeBytes(line.getBytes());

                    channel.writeAndFlush(buf);
                }

                line = scanner.nextLine();
            }

            this.eventLoopGroup.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
