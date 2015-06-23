package thebetweenlands.network.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class PacketRevengeTarget extends PacketEntityIdTuple {
	public PacketRevengeTarget() {
	}

	public PacketRevengeTarget(Entity entity, EntityLivingBase target) {
		super(entity, target);
	}
}
