package thebetweenlands.common.entity.infection;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.CapabilityRegistry;

public class SporeInfectionBehavior extends AbstractInfectionBehavior implements IInfectionBehaviorOverlay {

	private int ticks = 0;

	public SporeInfectionBehavior(EntityLivingBase entity) {
		super(entity);
	}

	@Override
	public void update() {
		++this.ticks;
		
		if(this.ticks > 200) {
			float amount = Math.min(1.0f, this.ticks / 600.0f);
			
			if(!this.world.isRemote) {
				this.infectNearby(amount);
			} else {
				this.spawnParticles(amount);
			}
		}

		super.update();
	}

	private void infectNearby(float amount) {
		List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.entity.getEntityBoundingBox().grow(amount * 8.0f));
		
		for(EntityLivingBase entity : entities) {
			IInfectionCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);
			
			if(cap != null && cap.isInfectable()) {
				cap.setInfectionPercent(cap.getInfectionPercent() + (entity == this.entity ? 0.00001f : 0.005f) * amount);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(float amount) {
		
		for(int i = 0; i < 8 * amount; ++i) {
			BLParticles particle;
			switch(this.world.rand.nextInt(4)) {
			default:
			case 0:
				particle = BLParticles.MOULD_HORN_1;
				break;
			case 1:
				particle = BLParticles.MOULD_HORN_2;
				break;
			case 2:
				particle = BLParticles.MOULD_HORN_3;
				break;
			case 3:
				particle = BLParticles.MOULD_HORN_4;
				break;
			}
			float dx = this.world.rand.nextFloat() * 0.4f - 0.2f;
			float dz = this.world.rand.nextFloat() * 0.4f - 0.2f;
			particle.spawn(this.world,
					this.entity.posX + dx, this.entity.posY + this.entity.getEyeHeight() * this.world.rand.nextFloat(), this.entity.posZ + dz,
					ParticleArgs.get()
					.withMotion(dx * 0.25D, 0.025D, dz * 0.25D)
					.withColor(1.0f, 1.0f, 1.0f, 0.5f)
					.withData(-1, false, 5));
		}

		for(int i = 0; i < 8 * amount; ++i) {
			Random rand = this.world.rand;
			float size = rand.nextFloat();
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(this.world,
					this.entity.posX + this.entity.motionX * 3.0f + (amount * 3.0f + 2.0f) * (rand.nextFloat() - 0.5f),
					this.entity.posY + this.entity.height * this.world.rand.nextFloat(),
					this.entity.posZ + this.entity.motionZ * 3.0f + (amount * 3.0f + 2.0f) * (rand.nextFloat() - 0.5f),
					ParticleArgs.get()
					.withMotion(this.entity.motionX * 0.5f + (rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.02f, this.entity.motionZ * 0.5f + (rand.nextFloat() - 0.5f) * 0.04f)
					.withScale(2f + size * 10.0F)
					.withColor(0.8F, 0.6F, 0.3F, (1 - size) * 0.1f + 0.1f)
					.withData(80, true, 0.01F, true)));
		}
	}

	@Override
	public boolean isDone() {
		return this.ticks > 600;
	}

	@Override
	public float getOverlayPercentage() {
		return this.ticks * 0.0016f;
	}

}
