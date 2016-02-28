package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thebetweenlands.network.base.Packet;

public class PacketPowerRingHit extends Packet {
	public PacketPowerRingHit() { }

	public int entityHit;

	public PacketPowerRingHit(int entityHit) {
		this.entityHit = entityHit;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.entityHit = buffer.readInt();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(this.entityHit);
	}

	public Entity getEntity(World world) {
		return world.getEntityByID(this.entityHit);
	}
}
