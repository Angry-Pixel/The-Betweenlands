package thebetweenlands.network.base;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

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
		this.pkt = this.packetHandler.getPacketSerializer().deserializePacketObj(this.buffer);
		this.pkt.deserialize(this.buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		this.packetHandler.getPacketSerializer().serializePacketObj(this.pkt, buffer);
		this.pkt.serialize(buffer);
	}
}
