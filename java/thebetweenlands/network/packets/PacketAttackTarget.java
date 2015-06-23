package thebetweenlands.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thebetweenlands.network.base.IPacket;

public class PacketAttackTarget implements IPacket {
	private int entityId;
	private int targetId;

	public PacketAttackTarget() {
	}

	public PacketAttackTarget(Entity entity, EntityLivingBase target) {
		entityId = entity.getEntityId();
		targetId = target == null ? -1 : target.getEntityId();
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		entityId = buffer.readInt();
		targetId = buffer.readInt();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeInt(targetId);
	}

	public int getEntityId() {
		return entityId;
	}

	public int getTargetId() {
		return targetId;
	}
}
