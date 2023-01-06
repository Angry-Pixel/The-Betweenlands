package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntitySilkyPebble extends EntityAngryPebble {
	public EntitySilkyPebble(World world) {
		super(world);
	}

	public EntitySilkyPebble(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	protected void explode() {
		boolean blockDamage = this.world.getGameRules().getBoolean("mobGriefing");
		this.world.createExplosion(this, this.posX, this.posY, this.posZ, 4.5F, blockDamage);
	}
}