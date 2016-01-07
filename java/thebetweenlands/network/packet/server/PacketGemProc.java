package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thebetweenlands.network.base.IPacket;

public class PacketGemProc implements IPacket {
	public PacketGemProc() { }

	public byte type;
	public int entityHit;

	public PacketGemProc(byte type, int entityHit) {
		this.type = type;
		this.entityHit = entityHit;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.type = buffer.readByte();
		this.entityHit = buffer.readInt();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeByte(this.type);
		buffer.writeInt(this.entityHit);
	}
	
	public Entity getEntity(World world) {
		return world.getEntityByID(this.entityHit);
	}
}
