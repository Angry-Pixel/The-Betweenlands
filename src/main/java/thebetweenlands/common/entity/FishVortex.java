package thebetweenlands.common.entity;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ParticleRegistry;

import java.util.List;

public class FishVortex extends Entity {

	public FishVortex(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		boolean wasInWater = this.isInWater();

		super.tick();

		if(!this.level().isClientSide()) {
			if(!this.isVehicle() || this.tickCount >= 80 || (wasInWater && !this.isInWater())) {
				this.discard();
			}
		} else {
			this.spawnParticles();
		}

		if(this.isVehicle()) {
			Entity rider = this.getPassengers().getFirst();
			AABB box = rider.getBoundingBox().inflate(0D, 0.1D, 0D);

			if(this.level().getEntityCollisions(rider, box).isEmpty()) {
				this.setDeltaMovement(new Vec3(0.0D, 0.05D, 0.0D));
			} else {
				this.setDeltaMovement(Vec3.ZERO);
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
		}
	}

	protected void spawnParticles() {
		if(true) {
			if(this.getRandom().nextInt(3) != 0) {
				ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withData(400, this.getRandom().nextFloat(), this);
				args.withScale((1.5F + this.getRandom().nextFloat() * 1.5F) * 0.5f);
				var options = new EntitySwirlParticleOptions(ParticleRegistry.FISH_VORTEX.get(), new Vec3(0, -1.5D, 0), Vec3.ZERO, Vec3.ZERO, new Vec3(0, 1.0D, 0), 1.5f + this.getRandom().nextFloat() * 8.0f, true);
				TheBetweenlands.createParticle(options, this.level(), this.getX() + this.getRandom().nextFloat() * 4F, this.getY(), this.getZ() + this.getRandom().nextFloat() * 4F, args);
			}
		} else if(this.tickCount < 70) {
			List<VoxelShape> aabbs = this.level().getEntityCollisions(null, this.getBoundingBox().inflate(0, -8, 0));

			if(!aabbs.isEmpty()) {
				double surfaceY = 0;
				for(VoxelShape aabb : aabbs) {
					surfaceY = Math.max(aabb.bounds().maxY, surfaceY);
				}

				double radius = 0.25D;
				double ox = (this.getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double oz = (this.getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double velX = ox * this.getRandom().nextFloat() * 2.0f;
				double velY = 0.5f + this.tickCount / 160.0f;
				double velZ = oz * this.getRandom().nextFloat() * 2.0f;

				int waterColor = BiomeColors.getAverageWaterColor(this.level(), this.blockPosition());

				float r = (waterColor >> 16 & 255) / 255.0f * 0.5f;
				float g = (waterColor >> 8 & 255) / 255.0f * 0.5f;
				float b = (waterColor & 255) / 255.0f;

				for(int i = 0; i < 3; i++) {
					TheBetweenlands.createParticle(new DripParticleOptions(false, false), this.level(), this.getX() + ox, surfaceY, this.getZ() + oz, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withScale(1.20f).withColor(r, g, b, 1.0f));
					TheBetweenlands.createParticle(new DripParticleOptions(false, false), this.level(), this.getX() + ox, surfaceY, this.getZ() + oz, ParticleFactory.ParticleArgs.get().withMotion(velX * 0.05f, velY, velZ * 0.05f).withScale(0.9f + this.getRandom().nextFloat() * 0.8f).withColor(r, g, b, 1.0f));
				}

				if(this.level().getRandom().nextInt(10) == 0) {
					TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(), this.level(), this.getX() + (this.level().getRandom().nextFloat() - 0.5f) * radius, surfaceY, this.getZ() + (this.level().getRandom().nextFloat() - 0.5f) * radius,
						ParticleFactory.ParticleArgs.get().withMotion((this.level().getRandom().nextFloat() - 0.5f) * 0.01f, 0.05f, (this.level().getRandom().nextFloat() - 0.5f) * 0.01f)
							.withScale(0.5f + this.level().getRandom().nextFloat() * 1.5f)
							.withData(true)
							.withColor(r * 0.9f, g * 0.9f, b * 0.9f, 0.9f));
				}
			}
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}
}
