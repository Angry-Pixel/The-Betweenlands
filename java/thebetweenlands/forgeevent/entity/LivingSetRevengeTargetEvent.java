package thebetweenlands.forgeevent.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingSetRevengeTargetEvent extends LivingEvent {
	public final EntityLivingBase target;

	public LivingSetRevengeTargetEvent(EntityLivingBase entity, EntityLivingBase target) {
		super(entity);
		this.target = target;
	}
}
