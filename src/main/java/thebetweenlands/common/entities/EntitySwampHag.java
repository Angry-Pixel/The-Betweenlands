package thebetweenlands.common.entities;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntitySwampHag extends BetweenlandsEntity {

	public float jawFloat = 0;
	public int animationTick = 0;
	public static final EntityDataAccessor<Byte> TALK_SOUND = SynchedEntityData.defineId(EntitySwampHag.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> SHOULD_JAW_MOVE = SynchedEntityData.defineId(EntitySwampHag.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> LIVING_SOUND_TIMER = SynchedEntityData.defineId(EntitySwampHag.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_THROWING = SynchedEntityData.defineId(EntitySwampHag.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> THROW_TIMER = SynchedEntityData.defineId(EntitySwampHag.class, EntityDataSerializers.INT);
	AnimationMathHelper animationTalk = new AnimationMathHelper();


	public EntitySwampHag(EntityType<? extends Monster> p_33002_, Level p_33003_) {
		super(p_33002_, p_33003_);
		this.ambientSoundTime = 120;
	}

	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(BetweenlandsEntity.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 40.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 4.0D)
			.add(Attributes.FOLLOW_RANGE, 35.0D).build());
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SWAMP_HAG_DEATH.get();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(4, new SwampHagAttackGoal(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new SwampHagTargetGoal<>(this, Player.class));
		this.targetSelector.addGoal(3, new SwampHagTargetGoal<>(this, IronGolem.class));
	}


	static class SwampHagAttackGoal extends MeleeAttackGoal {
		public SwampHagAttackGoal(EntitySwampHag p_33822_) {
			super(p_33822_, 1.0D, false);
		}
	}

	static class SwampHagTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
		public SwampHagTargetGoal(EntitySwampHag p_33832_, Class<T> p_33833_) {
			super(p_33832_, p_33833_, true);
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
		super.onSyncedDataUpdated(p_21104_);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(TALK_SOUND, (byte) 0);
		this.entityData.define(SHOULD_JAW_MOVE, false);
		this.entityData.define(LIVING_SOUND_TIMER, 0);
		this.entityData.define(IS_THROWING, false);
		this.entityData.define(THROW_TIMER, 0);
	}

	@Override
	public void tick() {
		super.tick();

		// NO BREATHING ALLOWED! ~random mojank dev who decided not to alow indiviual modelpart scaling
		//breatheFloat = animationBreathe.swing(0.2F, 0.5F, false);

		/*
		// Fixed in this build
		if(this.getAttackTarget() != null && !this.getRecursivePassengersByType(EntityWight.class).isEmpty()) {
			if(this.pathingCooldown <= 0) {
				//No idea why the swamp hag doesn't want to move when possessed, this seems to work as hackish solution
				if(!this.navigator.tryMoveToEntityLiving(this.getAttackTarget(), 1)) {
					this.pathingCooldown = 20;
				}
			} else {
				this.pathingCooldown--;
			}
		}
		*/


		if (!this.level.isClientSide) {
			updateLivingSoundTime();
		}

		if (animationTick > 0) {
			animationTick--;
		}

		if (animationTick == 0) {
			setShouldJawMove(false);
			jawFloat = animationTalk.swing(0F, 0F, true);
		}

		if (this.getLivingSoundTime() == -this.getAmbientSoundInterval())
			setShouldJawMove(true);

		if (!shouldJawMove())
			jawFloat = animationTalk.swing(0F, 0F, true);
		else if (shouldJawMove() && getTalkSound() != 3 && getTalkSound() != 4)
			jawFloat = animationTalk.swing(2.0F, 0.1F, false);
		else if (shouldJawMove() && getTalkSound() == 3 || shouldJawMove() && getTalkSound() == 4)
			jawFloat = animationTalk.swing(0.4F, 1.2F, false);
	}

	private void updateLivingSoundTime() {
		this.entityData.set(LIVING_SOUND_TIMER, this.ambientSoundTime);
	}

	private int getLivingSoundTime() {
		return this.entityData.get(LIVING_SOUND_TIMER);
	}

	private byte getTalkSound() {
		return this.entityData.get(TALK_SOUND);
	}

	private void setTalkSound(byte value) {
		this.entityData.set(TALK_SOUND, value);
	}

	public void setShouldJawMove(boolean jawState) {
		entityData.set(SHOULD_JAW_MOVE, jawState);
		if (jawState)
			animationTick = 20;
	}

	public boolean shouldJawMove() {
		return entityData.get(SHOULD_JAW_MOVE);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		setTalkSound((byte) 4);
		setShouldJawMove(true);
		return SoundRegistry.SWAMP_HAG_HURT.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		int randomSound = random.nextInt(4) + 1;
		setTalkSound((byte) randomSound);
		switch (getTalkSound()) {
			case 1:
				return SoundRegistry.SWAMP_HAG_LIVING_1.get();
			case 2:
				return SoundRegistry.SWAMP_HAG_LIVING_2.get();
			case 3:
				return SoundRegistry.SWAMP_HAG_LIVING_3.get();
			default:
				return SoundRegistry.SWAMP_HAG_LIVING_4.get();
		}
	}

	// This uses the combination event
	/*
	protected SoundEvent getAmbientSound() {
		return ModSounds.SWAMP_HAG_LIVING.get();
	}
	*/

	public boolean isRidingMummy() {
		return false;
	}

	public int getThrowTimer() {
		return 0;
	}
}
