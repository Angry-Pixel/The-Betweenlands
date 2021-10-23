package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleLightningArc;
import thebetweenlands.common.network.MessageEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageShockParticle extends MessageEntity {
	public MessageShockParticle() { }

	public MessageShockParticle(Entity entity) {
		this.addEntity(entity);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		Entity entity = this.getEntity(0);

		if(entity != null && entity.world.isRemote) {
			this.spawnParticle(entity);
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticle(Entity entity) {
		if(entity.world.isRemote) {
			Entity view = Minecraft.getMinecraft().getRenderViewEntity();

			if(view != null) {
				float dst = view.getDistance(entity);

				if(dst < 100) {
					float ox = (entity.world.rand.nextFloat() - 0.5f) * 4;
					float oy = (entity.world.rand.nextFloat() - 0.5f) * 4;
					float oz = (entity.world.rand.nextFloat() - 0.5f) * 4;

					ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(entity.world, entity.posX, entity.posY, entity.posZ, 
							ParticleArgs.get()
							.withColor(0.5f, 0.4f, 1.0f, 0.9f)
							.withData(new Vec3d(entity.posX + ox, entity.posY + oy, entity.posZ + oz)));

					if(dst > 30) {
						//lower quality
						particle.setBaseSize(0.1f);
						particle.setSubdivs(2, 1);
						particle.setSplits(2);
					}

					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);

					if(dst < 16) {
						entity.world.playSound(entity.posX, entity.posY, entity.posZ, SoundRegistry.ZAP, SoundCategory.AMBIENT, 0.85f, 1, false);
					}
				}
			}
		}
	}
}
