package thebetweenlands.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import thebetweenlands.network.base.Packet;

public class PacketPlayIdleSound extends Packet {
	public String name;
	public int entityID;
	public float volume;
	public float pitch;

	public PacketPlayIdleSound() {}

	public PacketPlayIdleSound(String name, Entity entity, float volume, float pitch) {
		this.name = name;
		this.entityID = entity.getEntityId();
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		try {
			PacketBuffer pkt = new PacketBuffer(buffer);
			this.name = pkt.readStringFromBuffer(128);
			this.entityID = pkt.readInt();
			this.volume = pkt.readFloat();
			this.pitch = pkt.readFloat();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void serialize(ByteBuf buffer) {
		try {
			PacketBuffer pkt = new PacketBuffer(buffer);
			pkt.writeStringToBuffer(this.name);
			pkt.writeInt(this.entityID);
			pkt.writeFloat(this.volume);
			pkt.writeFloat(this.pitch);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Entity getEntity(World world) {
		return world.getEntityByID(this.entityID);
	}
}
