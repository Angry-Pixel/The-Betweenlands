package thebetweenlands.common.network.interception;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketAddress;
import java.util.function.Function;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.network.Packet;

public class ChannelProxy implements Channel {
	private final Channel parent;
	private final Function<Object, Object> writeProxy;

	public ChannelProxy(Channel parent, Function<Object, Object> writeProxy) {
		this.parent = parent;
		this.writeProxy = writeProxy;
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return this.parent.attr(key);
	}

	@Override
	public int compareTo(Channel o) {
		return this.parent.compareTo(o);
	}

	@Override
	public EventLoop eventLoop() {
		return new EventLoopProxy(this.parent.eventLoop(), runnable -> {
			try {
				Field packetField = null;
				for(Field f : runnable.getClass().getDeclaredFields()) {
					if(Packet.class.isAssignableFrom(f.getType())) {
						packetField = f;
						break;
					}
				}
				if(packetField != null) {
					packetField.setAccessible(true);
					Packet<?> pkt = (Packet<?>) packetField.get(runnable);
					if(pkt != null) {
						Object newPkt = this.writeProxy.apply(pkt);
						if(pkt != newPkt) {
							Field modifiersField = Field.class.getDeclaredField("modifiers");
							modifiersField.setAccessible(true);
							modifiersField.setInt(packetField, packetField.getModifiers() & ~Modifier.FINAL);

							packetField.set(runnable, newPkt);
						}
					}
				}
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
			return runnable;
		});
	}

	@Override
	public Channel parent() {
		return this.parent.parent();
	}

	@Override
	public ChannelConfig config() {
		return this.parent.config();
	}

	@Override
	public boolean isOpen() {
		return this.parent.isOpen();
	}

	@Override
	public boolean isRegistered() {
		return this.parent.isRegistered();
	}

	@Override
	public boolean isActive() {
		return this.parent.isActive();
	}

	@Override
	public ChannelMetadata metadata() {
		return this.parent.metadata();
	}

	@Override
	public SocketAddress localAddress() {
		return this.parent.localAddress();
	}

	@Override
	public SocketAddress remoteAddress() {
		return this.parent.remoteAddress();
	}

	@Override
	public ChannelFuture closeFuture() {
		return this.parent.closeFuture();
	}

	@Override
	public boolean isWritable() {
		return this.parent.isWritable();
	}

	@Override
	public Unsafe unsafe() {
		return this.parent.unsafe();
	}

	@Override
	public ChannelPipeline pipeline() {
		return this.parent.pipeline();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.parent.alloc();
	}

	@Override
	public ChannelPromise newPromise() {
		return this.parent.newPromise();
	}

	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return this.parent.newProgressivePromise();
	}

	@Override
	public ChannelFuture newSucceededFuture() {
		return this.parent.newSucceededFuture();
	}

	@Override
	public ChannelFuture newFailedFuture(Throwable cause) {
		return this.parent.newFailedFuture(cause);
	}

	@Override
	public ChannelPromise voidPromise() {
		return this.parent.voidPromise();
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress) {
		return this.parent.bind(localAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress) {
		return this.parent.connect(remoteAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		return this.parent.connect(remoteAddress, localAddress);
	}

	@Override
	public ChannelFuture disconnect() {
		return this.parent.disconnect();
	}

	@Override
	public ChannelFuture close() {
		return this.parent.close();
	}

	@Override
	public ChannelFuture deregister() {
		return this.parent.deregister();
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return this.parent.bind(localAddress);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return this.parent.connect(remoteAddress, promise);
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress,
			ChannelPromise promise) {
		return this.parent.connect(remoteAddress, localAddress, promise);
	}

	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return this.parent.disconnect();
	}

	@Override
	public ChannelFuture close(ChannelPromise promise) {
		return this.parent.close();
	}

	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return this.parent.deregister(promise);
	}

	@Override
	public Channel read() {
		return this.parent.read();
	}

	@Override
	public ChannelFuture write(Object msg) {
		msg = this.writeProxy.apply(msg);
		return this.parent.write(msg);
	}

	@Override
	public ChannelFuture write(Object msg, ChannelPromise promise) {
		msg = this.writeProxy.apply(msg);
		return this.parent.write(msg, promise);
	}

	@Override
	public Channel flush() {
		return this.parent.flush();
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		msg = this.writeProxy.apply(msg);
		return this.parent.writeAndFlush(msg, promise);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		msg = this.writeProxy.apply(msg);
		return this.parent.writeAndFlush(msg);
	}
}
