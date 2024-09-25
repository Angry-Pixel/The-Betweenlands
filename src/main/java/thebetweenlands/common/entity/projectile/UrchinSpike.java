package thebetweenlands.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;
import java.util.UUID;

public class UrchinSpike extends Entity implements OwnableEntity {
	private static final EntityDataAccessor<Integer> SPIKE_BOX_SIZE = SynchedEntityData.defineId(UrchinSpike.class, EntityDataSerializers.INT);
	public final int MAX_SPIKE_TIMER = 10;
	@Nullable
	private Player owner;
	private int damage;
	public static final byte EVENT_ATTACK = 66;

	public UrchinSpike(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	public UrchinSpike(Level level, @Nullable Player owner, int damage) {
		super(EntityRegistry.URCHIN_SPIKE.get(), level);
		this.owner = owner;
		this.damage = damage;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(SPIKE_BOX_SIZE, 0);
	}

	public int getSpikeBoxTimer() {
		return this.getEntityData().get(SPIKE_BOX_SIZE);
	}

	public void setSpikeBoxTimer(int count) {
		this.getEntityData().set(SPIKE_BOX_SIZE, count);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			this.checkAOEDamage();
			if (this.getSpikeBoxTimer() < MAX_SPIKE_TIMER)
				this.setSpikeBoxTimer(this.getSpikeBoxTimer() + 1);
			if (this.getSpikeBoxTimer() >= MAX_SPIKE_TIMER)
				this.discard();
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	private void checkAOEDamage() {
		if (!this.level().isClientSide() && this.level().getDifficulty() != Difficulty.PEACEFUL) {
			List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.spikesBox(), e -> e instanceof Enemy);
			list.removeIf(entity -> entity == this.owner || entity instanceof Player player && (player.isSpectator() || player.isCreative()));

			if (!list.isEmpty()) {
				LivingEntity entity = list.getFirst();
				if(entity.hurtTime <= 0) {
					entity.hurt(this.damageSources().mobProjectile(this, this.owner), 2.0F * this.damage);
				}
			}
		}
	}

	public void shootSpikes() {
		this.playSound(SoundRegistry.URCHIN_SHOOT.get(), 1.0F, 1.5F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.5F);
		this.level().broadcastEntityEvent(this, EVENT_ATTACK);
	}

	public AABB spikesBox() {
		float x = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float y = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float z = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		return new AABB(this.blockPosition()).inflate(x, y, z);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == EVENT_ATTACK) {
			Vec3 frontCenter = this.position();
			for (int i = 0; i < 64; i++) {
				float rx = this.getRandom().nextFloat() * 4.0F - 2.0F;
				float ry = this.getRandom().nextFloat() * 4.0F - 2.0F;
				float rz = this.getRandom().nextFloat() * 4.0F - 2.0F;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
				TheBetweenlands.createParticle(ParticleRegistry.URCHIN_SPIKE.get(), this.level(), frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.175F, vec.y * 0.15F + 0.35F, vec.z * 0.175F).withScale(0.2F));
			}
		}
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.owner != null ? this.owner.getUUID() : null;
	}
}
