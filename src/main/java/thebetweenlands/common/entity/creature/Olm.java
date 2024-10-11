package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class Olm extends WaterAnimal {

	public long cooldown;

	public Olm(EntityType<? extends WaterAnimal> type, Level level) {
		super(type, level);
		this.moveControl = new OlmMoveControl(this);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new RandomSwimmingGoal(this, 0.4D, 20));
		this.goalSelector.addGoal(1, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 8.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.4D);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.OLM_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.OLM_DEATH.get();
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}


	public static boolean checkSpawnRules(EntityType<? extends WaterAnimal> olm, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return pos.getY() <= TheBetweenlands.CAVE_WATER_HEIGHT && level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WaterBoundPathNavigation(this, level);
	}

	@Override
	public boolean isInWater() {
		return this.canDrownInFluidType(this.getEyeInFluidType());
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(ItemRegistry.SNOT) && this.getCanLayEgg()) {
			stack.consume(1, player);
			this.setEggCooldown(this.level().getGameTime() + 24000);
			this.spawnAtLocation(ItemRegistry.RAW_OLM_EGG);
			return InteractionResult.sidedSuccess(player.level().isClientSide());
		}
		return super.mobInteract(player, hand);
	}

	private boolean getCanLayEgg() {
		return this.level().getGameTime() >= this.getEggCooldown();
	}

	public void setEggCooldown(long cooldownTime) {
		this.cooldown = cooldownTime;
	}

	public long getEggCooldown() {
		return this.cooldown;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putLong("cooldown", this.getEggCooldown());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setEggCooldown(tag.getLong("cooldown"));
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			this.moveRelative(this.getSpeed(), travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.75D));
			if (this.getTarget() == null) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.003D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}
	}

	static class OlmMoveControl extends MoveControl {
		private final Olm olm;

		public OlmMoveControl(Olm olm) {
			super(olm);
			this.olm = olm;
		}

		@Override
		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO && !this.olm.getNavigation().isDone()) {
				double targetX = this.getWantedX() - this.olm.getX();
				double targetY = this.getWantedY() - this.olm.getY();
				double targetZ = this.getWantedZ() - this.olm.getZ();
				double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
				targetDistance = Mth.sqrt((float) targetDistance);
				targetY = targetY / targetDistance;
				float targetAngle = (float) (Mth.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
				this.olm.setYRot(this.rotlerp(this.olm.getYRot(), targetAngle, 90.0F));
				this.olm.yBodyRot = this.olm.getYRot();
				float travelSpeed = (float) (this.getSpeedModifier() * this.olm.getAttributeValue(Attributes.MOVEMENT_SPEED));
				this.olm.setSpeed(this.olm.getSpeed() + (travelSpeed - this.olm.getSpeed()) * 0.125F);
				double wiggleSpeed = Math.sin((double) (this.olm.tickCount + this.olm.getId()) * 0.5D) * this.olm.getBbHeight() * 0.05D;
				double wiggleOffsetX = Math.cos(this.olm.getYRot() * 0.017453292F);
				double wiggleOffsetZ = Math.sin(this.olm.getYRot() * 0.017453292F);
				this.olm.setDeltaMovement(this.olm.getDeltaMovement().add(wiggleSpeed * wiggleOffsetX, 0.0D, wiggleSpeed * wiggleOffsetZ));
				wiggleSpeed = Math.sin((double) (this.olm.tickCount + this.olm.getId()) * 0.75D) * 0.05D;
				this.olm.setDeltaMovement(this.olm.getDeltaMovement().add(0.0D, wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D, 0.0D));
				this.olm.setDeltaMovement(this.olm.getDeltaMovement().add(0.0D, this.olm.getSpeed() * targetY * 0.1D, 0.0D));
				LookControl entitylookhelper = this.olm.getLookControl();
				double targetDirectionX = this.olm.getX() + targetX / targetDistance * 2.0D;
				double targetDirectionY = (double) this.olm.getEyeHeight() + this.olm.getY() + targetY / targetDistance;
				double targetDirectionZ = this.olm.getZ() + targetZ / targetDistance * 2.0D;
				double lookX = entitylookhelper.getWantedX();
				double lookY = entitylookhelper.getWantedY();
				double lookZ = entitylookhelper.getWantedZ();

				if (!entitylookhelper.isLookingAtTarget()) {
					lookX = targetDirectionX;
					lookY = targetDirectionY;
					lookZ = targetDirectionZ;
				}

				this.olm.getLookControl().setLookAt(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
			} else {
				this.olm.setSpeed(0.0F);
			}
		}
	}
}
