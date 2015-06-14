package thebetweenlands.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thebetweenlands.event.render.FogHandler;

public class EntityBugFX extends EntityFX {
	private ResourceLocation particleTexture;
	private float scale;
	private int color;

	public EntityBugFX(World world, double x, double y, double z, double speed, double jitter, float scale, int color, ResourceLocation texture) {
		super(world, x, y, z, 0, 0, 0);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleMaxAge = (int)500;
		this.noClip = true;
		this.color = 0xFFFFFFFF;
		this.scale = scale;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		
	}

	@Override
	public void onUpdate() {
		
	}
}
