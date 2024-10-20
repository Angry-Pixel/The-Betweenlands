package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.FollowTargetGoal;
import thebetweenlands.common.entity.ai.goals.JumpRandomlyGoal;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class RootSprite extends PathfinderMob implements BLEntity {

	private static final byte EVENT_STEP = 40;
	private float jumpHeightOverride = -1;

	public RootSprite(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.xpReward = 1;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 5, 0.5F, 1.0F));
		this.goalSelector.addGoal(3, new FollowTargetGoal<>(this, new FollowTargetGoal.FollowClosest<>(this, Sporeling.class, 10), 0.65D, 0.5F, 10.0F, false));
		this.goalSelector.addGoal(4, new JumpRandomlyGoal(this, 10, () -> !RootSprite.this.level().getEntitiesOfClass(Sporeling.class, this.getBoundingBox().inflate(1)).isEmpty()) {
			@Override
			public void start() {
				RootSprite.this.setJumpHeightOverride(0.2F);
				super.start();
			}
		});
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Sporeling.class, 8));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.5D);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.ROOT_SPRITE_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ROOT_SPRITE_DEATH.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.ROOT_SPRITE_LIVING.get();
	}

	@Override
	protected float getSoundVolume() {
		return 0.5F;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 5 * 20;
	}

	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}

	@Override
	protected float getJumpPower() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpPower();
	}

	@Override
	public void tick() {
		super.tick();

		if(this.level().isClientSide() && this.getRandom().nextInt(20) == 0) {
			this.spawnLeafParticles();
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		super.playStepSound(pos, state);

		this.moveDist += 0.7F;

		this.level().broadcastEntityEvent(this, EVENT_STEP);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if(id == EVENT_STEP) {
			this.spawnLeafParticles();
		}
	}

	private void spawnLeafParticles() {
		for(int i = 0; i < 1 + this.getRandom().nextInt(3); i++) {
			TheBetweenlands.createParticle(ParticleRegistry.WEEDWOOD_LEAF.get(), this.level(), this.getX() + this.getDeltaMovement().x(), this.getY() + 0.1F + this.getRandom().nextFloat() * 0.3F, this.getZ() + this.getDeltaMovement().z(), ParticleFactory.ParticleArgs.get()
				.withMotion(this.getDeltaMovement().x() * 0.5F + this.getRandom().nextFloat() * 0.1F - 0.05F, 0.05F, this.getDeltaMovement().z() * 0.5F + this.getRandom().nextFloat() * 0.1F - 0.05F)
				.withScale(0.5F));
		}
	}
}
