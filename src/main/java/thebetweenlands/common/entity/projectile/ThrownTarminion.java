package thebetweenlands.common.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Tarminion;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

import javax.annotation.Nullable;

public class ThrownTarminion extends ThrowableProjectile {

	public ThrownTarminion(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
	}

	public ThrownTarminion(Level level, LivingEntity entity) {
		super(EntityRegistry.THROWN_TARMINION.get(), entity, level);
	}

	public ThrownTarminion(Level level, double x, double y, double z) {
		super(EntityRegistry.THROWN_TARMINION.get(), x, y, z, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	public void tick() {
		super.tick();
		if(!this.onGround()) {
			if (this.level().isClientSide()) {
				TheBetweenlands.createParticle(ParticleRegistry.DRIPPING_TAR.get(), this.level(), this.getX(), this.getY(), this.getZ(), ParticleFactory.ParticleArgs.get().withColor(0.0F, 0.0F, 0.0F, 1.0F));
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!this.level().isClientSide()) {
			this.spawnTarminion();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if(!(result.getEntity() instanceof Tarminion)) {
			if(!this.level().isClientSide()) {
				result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 2);

				if (this.isOnFire() && !(result.getEntity() instanceof EnderMan)) {
					result.getEntity().igniteForSeconds(5);
				}
			}

			if(this.level().isClientSide()) {
				for (int i = 0; i < 8; i++) {
					TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)), this.level(), this.getX(), this.getY(), this.getZ(), ParticleFactory.ParticleArgs.get().withColor(0.0F, 0.0F, 0.0F, 1.0F));
				}
			}
		}

		if (!this.level().isClientSide()) {
			Tarminion tarminion = this.spawnTarminion();
			if(tarminion != null && result.getEntity() instanceof LivingEntity living) {
				tarminion.setTarget(living);
			}
		}
	}

	@Nullable
	private Tarminion spawnTarminion() {
		if (!this.level().isClientSide()) {
			this.discard();
			Tarminion tarminion = new Tarminion(EntityRegistry.TARMINION.get(), this.level());
			tarminion.setTame(true, true);
			if(this.getOwner() != null) {
				tarminion.setOwnerUUID(this.getOwner().getUUID());
			}
			Vec3 motionVec = this.getDeltaMovement();
			motionVec = motionVec.normalize();
			double speed = 0.25D;
			tarminion.setDeltaMovement(motionVec.scale(speed));
			tarminion.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
			this.level().addFreshEntity(tarminion);
			return tarminion;
		}
		return null;
	}
}
