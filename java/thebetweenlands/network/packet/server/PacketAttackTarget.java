package thebetweenlands.network.packet.server;

import thebetweenlands.network.packet.PacketEntityIdTuple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class PacketAttackTarget extends PacketEntityIdTuple {
	public PacketAttackTarget() {
	}

	public PacketAttackTarget(Entity entity, EntityLivingBase target) {
		super(entity, target);
	}
}
