package thebetweenlands.common.entity.boss.malevolence;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.monster.Wight;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.Optional;
import java.util.UUID;

public class PrimordialMalevolenceSpawner extends Entity implements BLEntity, OwnableEntity {

	protected static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(PrimordialMalevolenceSpawner.class, EntityDataSerializers.OPTIONAL_UUID);

	public int spawnDelay = 40;

	public PrimordialMalevolenceSpawner(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	public PrimordialMalevolenceSpawner(Level level, Entity source) {
		this(EntityRegistry.PRIMORDIAL_MALEVOLENCE_SPAWNER.get(), level);
		this.setOwner(source.getUUID());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(OWNER, Optional.empty());
	}

	public void setOwner(@Nullable UUID uuid) {
		this.getEntityData().set(OWNER, Optional.ofNullable(uuid));
	}

	@Nullable
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER).orElse(null);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("spawnDelay", this.spawnDelay);
		if(this.getOwnerUUID() != null) {
			compound.putUUID("owner", this.getOwnerUUID());
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.spawnDelay = compound.getInt("spawnDelay");
		if(compound.hasUUID("owner")) {
			this.setOwner(compound.getUUID("owner"));
		} else {
			this.setOwner(null);
		}
	}


	@Override
	public void tick() {
		if(!this.level().isClientSide() && (this.level().getDifficulty() == Difficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isAlive()))) {
			this.discard();
			return;
		}

		this.setDeltaMovement(Vec3.ZERO);

		super.tick();

		if(this.level().isClientSide()) {
			Entity owner = this.getOwner();
			if(owner != null) {
				for(int i = 0; i < 3; i++) {
					double sx = this.getX() + (this.getRandom().nextDouble() - 0.5D) * (double)this.getBbWidth();
					double sy = this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25D;
					double sz = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * (double)this.getBbWidth();
					double ex = owner.getX() + (this.getRandom().nextDouble() - 0.5D) * (double)owner.getBbWidth();
					double ey = owner.getY() + this.getRandom().nextDouble() * (double)owner.getBbHeight() - 0.25D;
					double ez = owner.getZ() + (this.getRandom().nextDouble() - 0.5D) * (double)owner.getBbWidth();
					this.level().addParticle(ParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}

		if(this.spawnDelay > 0) {
			this.spawnDelay--;
		} else {
			if(!this.level().isClientSide()) {
				Wight wight = new Wight(EntityRegistry.WIGHT.get(), this.level());
				wight.moveTo(this.getX(), this.getY(), this.getZ(), 0, 0);
				wight.setCanTurnVolatile(false);
				wight.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
				wight.setHealth(wight.getMaxHealth());
				if(this.getOwner() instanceof Mob mob) {
					wight.setTarget(mob.getTarget());
				}
				this.level().addFreshEntity(wight);
				this.discard();
			} else {
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
				this.discard();
			}
		}
	}

	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.getX();
		final double cy = this.getY() + 0.35D;
		final double cz = this.getZ();
		for(int i = 0; i < 8; i++) {
			double px = this.level().getRandom().nextFloat() * 0.7F;
			double py = this.level().getRandom().nextFloat() * 0.7F;
			double pz = this.level().getRandom().nextFloat() * 0.7F;
			Vec3 vec = new Vec3(px, py, pz).subtract(new Vec3(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.x * radius;
			py = cy + vec.y * radius;
			pz = cz + vec.z * radius;
			//TheBetweenlands.createParticle(ParticleRegistry.STEAM_PURIFIER.get(), this.level(), px, py, pz);
		}
	}
}
