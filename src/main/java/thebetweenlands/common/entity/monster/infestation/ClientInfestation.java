package thebetweenlands.common.entity.monster.infestation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.audio.DefaultEntitySoundInstance;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AABBUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientInfestation {

	@Nullable
	private SoundInstance idleSound;

	protected void tick(Infestation infestation) {
		Entity view = Minecraft.getInstance().getCameraEntity();

		if (view != null && view.distanceTo(infestation) < 16 && !infestation.isInWater()) {
			SoundManager manager = Minecraft.getInstance().getSoundManager();
			if (this.idleSound == null || !manager.isActive(this.idleSound)) {
				this.idleSound = new DefaultEntitySoundInstance<>(SoundRegistry.SWARM_IDLE.get(), SoundSource.HOSTILE, infestation, LivingEntity::isAlive, 0.8F);
				manager.play(this.idleSound);
			}

			List<AABB> collisionBoxes = new ArrayList<>();

			for (BlockPos offsetPos : BlockPos.betweenClosed(
				new BlockPos(Mth.floor(infestation.getX() - infestation.getBbWidth() - 2), Mth.floor(infestation.getY() - 2), Mth.floor(infestation.getZ() - infestation.getBbWidth() - 2)),
				new BlockPos(Mth.floor(infestation.getX() + infestation.getBbWidth() + 2), Mth.floor(infestation.getY() + infestation.getBbHeight() + 2), Mth.floor(infestation.getZ() + infestation.getBbWidth() + 2)))) {
				BlockState state = infestation.level().getBlockState(offsetPos);

				if (state.isRedstoneConductor(infestation.level(), offsetPos)) {
					collisionBoxes.add(new AABB(offsetPos));
				}
			}

			float swarmSize = infestation.getSwarmSize();

			for (int i = 0; i < Math.max(2 - Minecraft.getInstance().options.particles().get().getId(), 1) * swarmSize + 1; i++) {
				float rx = (infestation.level().getRandom().nextFloat() - 0.5f) * infestation.getBbWidth();
				float ry = (infestation.level().getRandom().nextFloat() - 0.5f) * infestation.getBbHeight();
				float rz = (infestation.level().getRandom().nextFloat() - 0.5f) * infestation.getBbWidth();

				float len = Mth.sqrt(rx * rx + ry * ry + rz * rz);

				rx /= len;
				ry /= len;
				rz /= len;

				len = 0.333f + infestation.level().getRandom().nextFloat() * 0.666f;

				double x = infestation.getX() + infestation.getDeltaMovement().x() * 5 + rx * len * (infestation.getBbWidth() + 0.3f) * swarmSize * 0.5f;
				double y = infestation.getY() + infestation.getDeltaMovement().y() * 5 - 0.15f * swarmSize + (infestation.getBbHeight() + 0.3f) * swarmSize * 0.5f + ry * len * (infestation.getBbHeight() + 0.3f) * swarmSize;
				double z = infestation.getZ() + infestation.getDeltaMovement().z() * 5 + rz * len * (infestation.getBbWidth() + 0.3f) * swarmSize * 0.5f;

				if (infestation.isOnFire() && infestation.getRandom().nextInt(3) == 0) {
					infestation.level().addParticle(ParticleTypes.LAVA, x, y, z, 0, 0, 0);
				}

				if (infestation.getRandom().nextInt(8) == 0) {
					if (infestation.getRandom().nextInt(3) == 0) {
						TheBetweenlands.createParticle(ParticleRegistry.FLYING_SWARM.get(), infestation.level(), x, y, z);
					} else {
						TheBetweenlands.createParticle(ParticleRegistry.FLY.get(), infestation.level(), x, y, z, ParticleFactory.ParticleArgs.get().withScale(0.15F * infestation.getRandom().nextFloat() + 0.25F).withData(40, 0.01F, 0.0025F, false));
					}
				} else {
					AABB particle = new AABB(x - 0.01f, y - 0.01f, z - 0.01f, x + 0.01f, y + 0.01f, z + 0.01f);

					double closestDst = 1;
					double closestDX = 0;
					double closestDY = 0;
					double closestDZ = 0;

					for (AABB box : collisionBoxes) {
						double dx1 = AABBUtil.calculateXOffset(box, particle, -1);
						double dy1 = AABBUtil.calculateYOffset(box, particle, -1);
						double dz1 = AABBUtil.calculateZOffset(box, particle, -1);
						double dx2 = AABBUtil.calculateXOffset(box, particle, 1);
						double dy2 = AABBUtil.calculateYOffset(box, particle, 1);
						double dz2 = AABBUtil.calculateZOffset(box, particle, 1);

						if (Math.abs(dx1) < closestDst) {
							closestDst = Math.abs(dx1);
							closestDX = dx1;
							closestDY = 0;
							closestDZ = 0;
						}

						if (Math.abs(dy1) < closestDst) {
							closestDst = Math.abs(dy1);
							closestDX = 0;
							closestDY = dy1;
							closestDZ = 0;
						}

						if (Math.abs(dz1) < closestDst) {
							closestDst = Math.abs(dz1);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz1;
						}

						if (Math.abs(dx2) < closestDst) {
							closestDst = Math.abs(dx2);
							closestDX = dx2;
							closestDY = 0;
							closestDZ = 0;
						}

						if (Math.abs(dy2) < closestDst) {
							closestDst = Math.abs(dy2);
							closestDX = 0;
							closestDY = dy2;
							closestDZ = 0;
						}

						if (Math.abs(dz2) < closestDst) {
							closestDst = Math.abs(dz2);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz2;
						}
					}

					if (closestDst < 1) {
						x += closestDX - Math.signum(closestDX) * 0.01f;
						y += closestDY - Math.signum(closestDY) * 0.01f;
						z += closestDZ - Math.signum(closestDZ) * 0.01f;

						double ox = 1 - Math.abs(Math.signum(closestDX));
						double oy = 1 - Math.abs(Math.signum(closestDY));
						double oz = 1 - Math.abs(Math.signum(closestDZ));

						ParticleOptions variant;
						if (infestation.getRandom().nextInt(6) == 0) {
							variant = ParticleRegistry.EMISSIVE_SWARM.get();
						} else {
							variant = ParticleRegistry.SWARM.get();
						}

						TheBetweenlands.createParticle(variant, infestation.level(), x, y, z,
							ParticleFactory.ParticleArgs.get()
								.withMotion((infestation.getRandom().nextFloat() - 0.5f) * 0.05f * ox, (infestation.getRandom().nextFloat() - 0.5f) * 0.05f * oy, (infestation.getRandom().nextFloat() - 0.5f) * 0.05f * oz)
								.withScale(0.25F)
								.withData(Direction.getNearest((float) -closestDX, (float) -closestDY, (float) -closestDZ), 40, infestation.position(), (Supplier<Vec3>) infestation::position));
					}
				}
			}
		}
	}
}
