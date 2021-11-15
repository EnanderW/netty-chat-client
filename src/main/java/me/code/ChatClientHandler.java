package me.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        byte packetId = buf.readByte();

        if (packetId == 0) {
            String name = readString(buf);
            String message = readString(buf);

            System.out.println(name + ": " + message);
        }
    }

    private static String readString(ByteBuf buf) {
        int length = buf.readInt();

        byte[] content = new byte[length];
        buf.readBytes(content, 0, length);

        return new String(content, 0, length);
    }

}
