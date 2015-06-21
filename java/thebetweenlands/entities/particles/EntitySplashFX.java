package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntitySplashFX extends EntityFX {
	public EntitySplashFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, int color) {
		super(world, x, y, z, motionX, motionY, motionZ);
		particleRed = (color >> 16 & 0xFF) / 255F;
		particleGreen = (color >> 8 & 0xFF) / 255F;
		particleBlue = (color & 0xFF) / 255F;
	}
}
