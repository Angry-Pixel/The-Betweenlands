package thebetweenlands.network.packets;

import io.netty.buffer.ByteBuf;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.network.base.IPacket;

public class PacketSnailHatchParticle implements IPacket {
	public PacketSnailHatchParticle() { }
	
	public double x, y, z;
	
	public PacketSnailHatchParticle(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
