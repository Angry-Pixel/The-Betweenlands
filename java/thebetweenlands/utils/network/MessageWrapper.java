package thebetweenlands.utils.network;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

public final class MessageWrapper implements IMessage {
	public MessageWrapper() { }
	
	private SidedPacketHandler packetHandler;
	private ByteBuf buffer;
	private IPacket pkt;
	
	protected MessageWrapper(IPacket pkt, SidedPacketHandler packetHandler) {
		this.pkt = pkt;
		this.packetHandler = packetHandler;
	}
	
	protected void setPacketHandler(SidedPacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
	
	protected IPacket getPacket() {
		return this.pkt;
	}
	
	protected void deserialize() throws Exception {
		this.pkt = this.packetHandler.getPacketSerializer().deserialize(this.buffer);
		this.pkt.deserialize(this.buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		this.packetHandler.getPacketSerializer().serialize(this.pkt, buffer);
		this.pkt.serialize(buffer);
	}
}
