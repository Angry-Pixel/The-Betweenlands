package thebetweenlands.network.packet.server;

import thebetweenlands.network.packet.PacketEntityIdTuple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class PacketRevengeTarget extends PacketEntityIdTuple {
	public PacketRevengeTarget() {
	}

	public PacketRevengeTarget(Entity entity, EntityLivingBase target) {
		super(entity, target);
	}
}
