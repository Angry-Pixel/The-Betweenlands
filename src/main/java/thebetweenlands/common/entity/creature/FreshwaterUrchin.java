package thebetweenlands.common.entity.creature;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.particle.options.SpikeParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ProximitySpawner;
import thebetweenlands.common.registries.DamageTypeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class FreshwaterUrchin extends PathfinderMob implements ProximitySpawner {

	private static final EntityDataAccessor<Integer> SPIKE_COOLDOWN = SynchedEntityData.defineId(FreshwaterUrchin.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SPIKE_BOX_SIZE = SynchedEntityData.defineId(FreshwaterUrchin.class, EntityDataSerializers.INT);
	private boolean shootSpikes;
	public final int MAX_SPIKE_TIMER = 10;
	public static final byte EVENT_ATTACK = 66;

	public FreshwaterUrchin(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0D, 1.0D));
		this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 40));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SPIKE_COOLDOWN, 80);
		builder.define(SPIKE_BOX_SIZE, 0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 3.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.05D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.STEP_HEIGHT, 1.0D);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().getBlockState(pos).liquid() ? 10.0F + this.level().getMaxLocalRawBrightness(pos) - 0.5F : super.getWalkTargetValue(pos);
	}

	public int getSpikeGrowTimer() {
		return this.getEntityData().get(SPIKE_COOLDOWN);
	}

	public void setSpikeGrowTimer(int count) {
		this.getEntityData().set(SPIKE_COOLDOWN, count);
	}

	public int getSpikeBoxTimer() {
		return this.getEntityData().get(SPIKE_BOX_SIZE);
	}

	public void setSpikeBoxTimer(int count) {
		this.getEntityData().set(SPIKE_BOX_SIZE, count);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!this.level().isClientSide() && reason.shouldDestroy()) {
			if (this.getKillCredit() instanceof Lurker lurker) {
				lurker.setHuntingTimer(2400); // 2 minute cooldown
			}
		}
		super.remove(reason);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			this.checkAOEDamage();

			if (this.getSpikeGrowTimer() < 80)
				this.setSpikeGrowTimer(this.getSpikeGrowTimer() + 1);

			if (this.getSpikeGrowTimer() >= 80)
				if (this.level().getGameTime() % 5 == 0 && this.isInWater())
					this.checkArea(this, LivingEntity.class);

			if (this.shootSpikes) {
				if (this.getSpikeBoxTimer() < MAX_SPIKE_TIMER)
					this.setSpikeBoxTimer(this.getSpikeBoxTimer() + 1);
				if (this.getSpikeBoxTimer() >= MAX_SPIKE_TIMER) {
					this.shootSpikes = false;
					this.setSpikeBoxTimer(0);
				}
			}
		}
	}

	private void checkAOEDamage() {
		if (!this.level().isClientSide() && this.level().getDifficulty() != Difficulty.PEACEFUL) {
			List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.spikesBox(), entity -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity1 -> !(entity1 instanceof FreshwaterUrchin)).test(entity));

			for (LivingEntity entity : list) {
				if (entity.invulnerableTime <= 0) {
					entity.hurt(this.damageSources().source(DamageTypeRegistry.URCHIN_SPIKE, this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
					this.shootSpikes = false;
					this.setSpikeBoxTimer(0);
				}
			}
		}
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
		this.playSound(SoundRegistry.URCHIN_SHOOT.get(), 1.0F, 1.5F + (this.level().getRandom().nextFloat() - this.level().getRandom().nextFloat()) * 0.5F);
		this.setSpikeGrowTimer(0);
		this.shootSpikes = true;
		this.level().broadcastEntityEvent(this, EVENT_ATTACK);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == EVENT_ATTACK) {
			Vec3 frontCenter = this.position();
			for (int i = 0; i < 64; i++) {
				RandomSource rnd = this.level().getRandom();
				float rx = rnd.nextFloat() * 4.0F - 2.0F;
				float ry = rnd.nextFloat() * 4.0F - 2.0F;
				float rz = rnd.nextFloat() * 4.0F - 2.0F;
				Vec3 vec = new Vec3(rx, ry, rz);
				vec = vec.normalize();
				TheBetweenlands.createParticle(new SpikeParticleOptions(SpikeParticle.URCHIN_TEXTURE, this.level().getRandom().nextInt(15) == 0), this.level(), frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.175F, vec.y * 0.15F + 0.35F, vec.z * 0.175F).withScale(0.2F));
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		Entity attacker = source.getDirectEntity();
		if (attacker instanceof LivingEntity entity && attacker.invulnerableTime <= 0 && entity.getMainHandItem().isEmpty() && !(attacker instanceof FreshwaterUrchin))
			attacker.hurt(this.damageSources().source(DamageTypeRegistry.URCHIN_SPIKE, this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

		return super.hurt(source, damage);
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi()) {
			if (this.isInWater()) {
				this.moveRelative(0.1F, travelVector);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.75).subtract(0.0D, 0.006D, 0.0D));
			} else {
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(Vec3.ZERO.subtract(0.0D, 0.2D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}
	}

	public AABB spikesBox() {
		float x = (this.getProximityHorizontal() / MAX_SPIKE_TIMER) * this.getSpikeBoxTimer();
		float y = (this.getProximityVertical() / MAX_SPIKE_TIMER) * this.getSpikeBoxTimer();
		float z = (this.getProximityHorizontal() / MAX_SPIKE_TIMER) * this.getSpikeBoxTimer();
		return new AABB(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D, this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D).inflate(x, y, z).move(0.0D, y, 0.0D);
	}

	@Override
	public float getProximityHorizontal() {
		return 2.0F;
	}

	@Override
	public float getProximityVertical() {
		return 1.0F;
	}

	@Override
	public AABB proximityBox(LivingEntity spawner) {
		return getBoundingBox().inflate(this.getProximityHorizontal(), this.getProximityVertical(), this.getProximityHorizontal()).move(0.0D, this.getProximityVertical() + this.getBbHeight(), 0.0D);
	}

	@Override
	public boolean canSneakPast() {
		return true;
	}

	@Override
	public boolean checkSight() {
		return true;
	}

	@Override
	public boolean isSingleUse() {
		return false;
	}
}
