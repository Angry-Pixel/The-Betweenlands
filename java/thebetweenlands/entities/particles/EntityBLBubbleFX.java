package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.world.World;

public class EntityBLBubbleFX extends EntityBubbleFX {

	public EntityBLBubbleFX(World world, double x, double y, double z, double vecX, double vecY, double vecZ) {
		super(world, x, y, z, vecX, vecY, vecZ);
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY += 0.002D;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.8500000238418579D;
		motionY *= 0.3500000238418579D;
		motionZ *= 0.8500000238418579D;

		if (particleMaxAge-- <= 0)
			setDead();
	}
}
