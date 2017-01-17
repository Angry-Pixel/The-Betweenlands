package thebetweenlands.common.network.interception;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.function.Function;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.IThreadListener;

public class PacketInterceptor {
	static class InboundHandler extends ChannelInboundHandlerAdapter {
		private final Function<Object, Object> readProxy;

		private InboundHandler(Function<Object, Object> readProxy) {
			this.readProxy = readProxy;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if(ctx.channel().isOpen()) {
				msg = this.readProxy.apply(msg);

				if(msg == null) {
					ctx.fireChannelReadComplete();
					return;
				}
			}

			super.channelRead(ctx, msg);
		}
	}

	private static void injectOutboundInterceptor(NetworkManager manager, IThreadListener scheduler, Function<Packet<?>, Packet<?>> interceptor) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		//Replace channel with proxy
		Field f_channel = NetworkManager.class.getDeclaredField("channel");
		f_channel.setAccessible(true);
		f_channel.set(manager, new ChannelProxy(manager.channel(), obj -> {
			if(obj instanceof Packet && scheduler.isCallingFromMinecraftThread()) {
				return interceptor.apply((Packet<?>) obj);
			}
			return obj;
		}));
	}

	private static void injectInboundInterceptor(NetworkManager manager, IThreadListener scheduler, Function<Packet<?>, Packet<?>> interceptor) {
		@SuppressWarnings("unchecked")
		InboundHandler inboundHandler = new InboundHandler(msg -> {
			try {
				if(msg instanceof Packet && manager.acceptInboundMessage(msg)) {
					final Packet<INetHandler> pkt = (Packet<INetHandler>) msg;
					INetHandler netHandler = manager.getNetHandler();

					if (!scheduler.isCallingFromMinecraftThread()) {
						scheduler.addScheduledTask(new Runnable() {
							@Override
							public void run() {
								Packet<INetHandler> pktResult = (Packet<INetHandler>) interceptor.apply(pkt);
								if(pktResult != null) {
									pktResult.processPacket(netHandler);
								}
							}
						});

						return null;
					} else {
						return (Packet<INetHandler>) interceptor.apply(pkt);
					}
				}
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}

			return msg;
		});

		//Inject packet handler
		try {
			//Try to inject before decoder
			manager.channel().pipeline().addBefore("decoder", "packet_interceptor", inboundHandler);
		} catch(NoSuchElementException ex) {
			//Memory connection, no decoder available
			manager.channel().pipeline().addBefore("packet_handler", "packet_interceptor", inboundHandler);
		}
	}

	/*@SubscribeEvent
	public static void onClientConnected(ServerConnectionFromClientEvent event) {
		NetworkManager manager = event.getManager();
		try {
			injectInboundInterceptor(manager, FMLCommonHandler.instance().getMinecraftServerInstance(), packet -> interceptPacketServerInbound(packet));
			injectOutboundInterceptor(manager, FMLCommonHandler.instance().getMinecraftServerInstance(), packet -> interceptPacketServerOutbound(packet));
		} catch(Exception ex) {
			System.err.println("Failed to inject server packet interceptor handlers!");
			ex.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void onServerConnected(ClientConnectedToServerEvent event) {
		try {
			injectInboundInterceptor(event.getManager(), Minecraft.getMinecraft(), packet -> interceptPacketClientInbound(packet));
			injectOutboundInterceptor(event.getManager(), Minecraft.getMinecraft(), packet -> interceptPacketClientOutbound(packet));
		} catch(Exception ex) {
			System.err.println("Failed to inject client packet interceptor handlers!");
			ex.printStackTrace();
		}
	}*/

	public static Packet<?> interceptPacketClientInbound(Packet<?> pkt) {
		//System.out.println("C <- S PACKET: " + pkt);
		return pkt;
	}

	public static Packet<?> interceptPacketClientOutbound(Packet<?> pkt) {
		//System.out.println("C -> S PACKET: " + pkt);
		return pkt;
	}

	public static Packet<?> interceptPacketServerOutbound(Packet<?> pkt) {
		//System.out.println("S -> C PACKET: " + pkt);
		return pkt;
	}

	public static Packet<?> interceptPacketServerInbound(Packet<?> pkt) {
		//System.out.println("S <- C PACKET: " + pkt);
		return pkt;
	}
}
