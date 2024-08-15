package thebetweenlands.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entities.ai.goals.ThrowWormGoal;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class SwampHag extends Monster {

	public static final EntityDataAccessor<Byte> TALK_SOUND = SynchedEntityData.defineId(SwampHag.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> SHOULD_JAW_MOVE = SynchedEntityData.defineId(SwampHag.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> LIVING_SOUND_TIMER = SynchedEntityData.defineId(SwampHag.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_THROWING = SynchedEntityData.defineId(SwampHag.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> THROW_TIMER = SynchedEntityData.defineId(SwampHag.class, EntityDataSerializers.INT);

	public float jawFloat;
	public float breatheFloat;
	private final AnimationMathHelper animationTalk = new AnimationMathHelper();
	private final AnimationMathHelper animationBreathe = new AnimationMathHelper();
	private int animationTick;
	public boolean playPullSound;
	private int pathingCooldown = 0;


	public SwampHag(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
	}

	public static AttributeSupplier.Builder makeAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 40.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 4.0D)
			.add(Attributes.FOLLOW_RANGE, 35.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new BreakDoorGoal(this, difficulty -> true));
		this.goalSelector.addGoal(2, new ThrowWormGoal(this));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TALK_SOUND, (byte) 0);
		builder.define(SHOULD_JAW_MOVE, false);
		builder.define(LIVING_SOUND_TIMER, 0);
		builder.define(IS_THROWING, false);
		builder.define(THROW_TIMER, 0);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}


	@Override
	protected void playHurtSound(DamageSource source) {
		super.playHurtSound(source);
		this.setTalkSound((byte) 4);
		this.setShouldJawMove(true);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.SWAMP_HAG_HURT.get();
	}

	@Override
	public void playAmbientSound() {
		int randomSound = this.getRandom().nextInt(4) + 1;
		this.setTalkSound((byte) randomSound);
		this.makeSound(switch (this.getTalkSound()) {
			case 1 -> SoundRegistry.SWAMP_HAG_LIVING_1.get();
			case 2 -> SoundRegistry.SWAMP_HAG_LIVING_2.get();
			case 3 -> SoundRegistry.SWAMP_HAG_LIVING_3.get();
			default -> SoundRegistry.SWAMP_HAG_LIVING_4.get();
		});
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SWAMP_HAG_DEATH.get();
	}

	@Override
	public void aiStep() {
		this.breatheFloat = this.animationBreathe.swing(0.2F, 0.5F, false);

		if (this.getTarget() != null && !this.hasPassenger(entity -> entity.getType() == EntityRegistry.WIGHT.get())) {
			if (this.pathingCooldown <= 0) {
				//No idea why the swamp hag doesn't want to move when possessed, this seems to work as hackish solution
				if (!this.getNavigation().moveTo(this.getTarget(), 1)) {
					this.pathingCooldown = 20;
				}
			} else {
				this.pathingCooldown--;
			}
		}

		if (!this.level().isClientSide()) {
			this.updateLivingSoundTime();
		}

		if (this.animationTick > 0) {
			this.animationTick--;
		}

		if (this.animationTick == 0) {
			this.setShouldJawMove(false);
			this.jawFloat = this.animationTalk.swing(0F, 0F, true);
		}

		if (this.getLivingSoundTime() == -this.getAmbientSoundInterval())
			this.setShouldJawMove(true);

		if (!this.shouldJawMove())
			this.jawFloat = this.animationTalk.swing(0F, 0F, true);
		else if (this.shouldJawMove() && this.getTalkSound() != 3 && this.getTalkSound() != 4)
			this.jawFloat = this.animationTalk.swing(2.0F, 0.1F, false);
		else if (this.shouldJawMove() && this.getTalkSound() == 3 || this.shouldJawMove() && this.getTalkSound() == 4)
			this.jawFloat = this.animationTalk.swing(0.4F, 1.2F, false);
		super.aiStep();
	}


	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		if (!this.level().isClientSide() && this.isRidingMummy()) {
			if (this.getTarget() != null) {
				if (this.getThrowTimer() < 90 && !this.getIsThrowing()) {
					this.setThrowTimer(Math.min(90, this.getThrowTimer() + 1));
				}
				if (this.getThrowTimer() >= 90 && this.getIsThrowing() && this.getThrowTimer() < 101) {
					this.setThrowTimer(Math.min(102, this.getThrowTimer() + 2));
				}
			} else {
				if (this.getThrowTimer() > 0 && !this.getIsThrowing()) {
					this.setThrowTimer(Math.max(0, this.getThrowTimer() - 1));
				}
			}

			if (this.getThrowTimer() <= 0)
				this.playPullSound = true;

			if (this.isRaisingArm())
				if (this.playPullSound) {
					this.playSound(SoundRegistry.SLINGSHOT_CHARGE.get(), 1F, 1F);
					this.playPullSound = false;
				}
		}
	}

	public boolean isRidingMummy() {
		return false; //this.getVehicle() instanceof PeatMummy;
	}

	@Nullable
	public Entity getMummyMount() {
		return null; //this.isRidingMummy() ? (PeatMummy) this.getVehicle() : null;
	}

	private byte getTalkSound() {
		return this.getEntityData().get(TALK_SOUND);
	}

	private void setTalkSound(int soundIndex) {
		this.getEntityData().set(TALK_SOUND, (byte) soundIndex);
	}

	public void setShouldJawMove(boolean jawState) {
		this.getEntityData().set(SHOULD_JAW_MOVE, jawState);
		if (jawState)
			animationTick = 20;
	}

	public boolean shouldJawMove() {
		return this.getEntityData().get(SHOULD_JAW_MOVE);
	}

	private void updateLivingSoundTime() {
		this.getEntityData().set(LIVING_SOUND_TIMER, this.ambientSoundTime);
	}

	private int getLivingSoundTime() {
		return this.getEntityData().get(LIVING_SOUND_TIMER);
	}

	public boolean getIsThrowing() {
		return this.getEntityData().get(IS_THROWING);
	}

	public void setIsThrowing(boolean shooting) {
		this.getEntityData().set(IS_THROWING, shooting);
	}

	public int getThrowTimer() {
		return this.getEntityData().get(THROW_TIMER);
	}

	public void setThrowTimer(int timer) {
		this.getEntityData().set(THROW_TIMER, timer);
	}

	public boolean isRaisingArm() {
		return this.getEntityData().get(THROW_TIMER) > 0 && this.getEntityData().get(THROW_TIMER) < 90;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		if (source.is(DamageTypes.IN_WALL) && this.isRidingMummy()) return false;
		return super.isInvulnerableTo(source);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return 0.5F;
	}
}
