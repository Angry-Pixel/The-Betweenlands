package thebetweenlands.common.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.GlStateManager.TexGen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.ParticleBatchTypeBuilder;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleVisionOrb;

public class TileEntityWindChime extends TileEntity implements ITickable {
	private int timer;

	@SideOnly(Side.CLIENT)
	private ParticleBatch particleBatch;

	@SideOnly(Side.CLIENT)
	public ParticleBatch getParticleBatch() {
		if(this.particleBatch == null) {
			this.particleBatch = ParticleVisionOrb.createParticleBatch(() -> new ResourceLocation("thebetweenlands:textures/events/thunderstorm.png"));
		}
		return this.particleBatch;
	}

	@Override
	public void update() {
		if(this.world.isRemote) {
			this.updateParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateParticles() {
		ParticleBatch batch = this.getParticleBatch();

		double cx = this.pos.getX() + 0.5f;
		double cy = this.pos.getY() + 0.5f;
		double cz = this.pos.getZ() + 0.5f;

		double rx = this.world.rand.nextFloat() - 0.5f;
		double ry = this.world.rand.nextFloat() - 0.5f;
		double rz = this.world.rand.nextFloat() - 0.5f;
		double len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
		rx /= len;
		ry /= len;
		rz /= len;
		
		int size = this.world.rand.nextInt(3);
		rx *= 0.75f + size * 0.1f;
		ry *= 0.75f + size * 0.1f;
		rz *= 0.75f + size * 0.1f;

		ParticleVisionOrb particle = (ParticleVisionOrb) BLParticles.WIND_CHIME_VISION
				.create(this.world, cx + rx, cy + ry, cz + rz, ParticleFactory.ParticleArgs.get()
						.withData(cx, cy, cz, 150)
						.withMotion(0, 0, 0)
						.withColor(1.0f, 1.0f, 1.0f, 0.85f)
						.withScale(1.1f));

		BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);

		BatchedParticleRenderer.INSTANCE.updateBatch(batch);
	}
}
