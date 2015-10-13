package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.network.base.IPacket;

public class PacketDruidTeleportParticle implements IPacket {
	public PacketDruidTeleportParticle() { }
	
	public double x, y, z;
	
	public PacketDruidTeleportParticle(EntityDarkDruid druid) {
		this.x = druid.posX;
		this.y = druid.posY;
		this.z = druid.posZ;
	}
	
	@Override
	public void deserialize(ByteBuf buffer) {
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeDouble(this.x);
		buffer.writeDouble(this.y);
		buffer.writeDouble(this.z);
	}
}
