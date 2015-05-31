package thebetweenlands.network.base;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import thebetweenlands.network.base.impl.SimplePacketObjectSerializer;

import java.util.List;

public final class SidedPacketHandler implements IMessageHandler<MessageWrapper, IMessage> {
	private static SidedPacketHandler instance;
	private IPacketProxy proxy;
	private SimpleNetworkWrapper networkWrapper;
	private IPacketObjectSerializer packetSerializer = new SimplePacketObjectSerializer();
	
	public MessageWrapper wrapPacket(IPacket pkt) {
		return new MessageWrapper(pkt, this);
	}

	public SidedPacketHandler setPacketSerializer(IPacketObjectSerializer packetSerializer) {
		if(packetSerializer == null) {
			throw new NullPointerException("Packet Serializer must not be null!");
		}
		this.packetSerializer = packetSerializer;
		return this;
	}
	
	public SidedPacketHandler setProxy(IPacketProxy proxy) {
		if(proxy == null) {
			throw new NullPointerException("Packet Proxy must not be null!");
		}
		this.proxy = proxy;
		return this;
	}

	public SidedPacketHandler setNetworkWrapper(SimpleNetworkWrapper networkWrapper, int discriminatorClient, int discriminatorServer) {
		if(networkWrapper == null) {
			throw new NullPointerException("Network Wrapper must not be null!");
		}
		this.networkWrapper = networkWrapper;
		networkWrapper.registerMessage(this, MessageWrapper.class, discriminatorClient, Side.CLIENT);
		networkWrapper.registerMessage(this, MessageWrapper.class, discriminatorServer, Side.SERVER);
		return this;
	}

	public void registerPacketHandler(String className, Side receiverSide) throws Exception {
		this.proxy.registerPacketHandler(className, receiverSide);
	}

	public void registerPacketHandler(Class handlerClass, Side receiverSide) throws Exception {
		this.proxy.registerPacketHandler(handlerClass.getName(), receiverSide);
	}

	protected IPacketObjectSerializer getPacketSerializer() {
		return this.packetSerializer;
	}
	
	@Override
	public IMessage onMessage(MessageWrapper message, MessageContext ctx) {
		message.setPacketHandler(this);
		try {
			message.deserialize();
		} catch(Exception ex) {
			new Exception("An exception occurred during deserialization of a packet", ex).printStackTrace();
			return null;
		}
		IPacket pkt = message.getPacket();
		List<ListenerEntry> listeners = this.proxy.getListeners().get(pkt.getClass());
		if(listeners != null && listeners.size() > 0) {
			for(ListenerEntry l : listeners) {
				try {
					l.getListenerMethod().invoke(null, pkt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
