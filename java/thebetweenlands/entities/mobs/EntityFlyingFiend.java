package thebetweenlands.entities.mobs;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.world.World;

public class EntityFlyingFiend extends EntityBat {

	public EntityFlyingFiend(World world) {
		super(world);
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:flyingFiendLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:flyingFiendHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:flyingFiendDeath";
	}

}
