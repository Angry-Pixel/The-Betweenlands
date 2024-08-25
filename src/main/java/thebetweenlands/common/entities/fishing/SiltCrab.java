package thebetweenlands.common.entities.fishing;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.entities.ai.goals.AttackOnCollideGoal;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class SiltCrab extends PathfinderMob implements BLEntity {

	protected MeleeAttackGoal attackGoal;
	protected AvoidEntityGoal<Player> avoidPlayerGoal;
	protected NearestAttackableTargetGoal<Player> targetPlayerGoal;

	public int aggroCooldown = 200;
	protected boolean canAttack = false;

	public SiltCrab(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return WaterAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 8.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.35D)
			.add(Attributes.ATTACK_DAMAGE, 3.0D)
			.add(Attributes.STEP_HEIGHT, 2.0D);
	}

	@Override
	protected void registerGoals() {
		this.attackGoal = new MeleeAttackGoal(this, 1.0D, true);
		this.avoidPlayerGoal = new AvoidEntityGoal<>(this, Player.class, 10.0F, 0.7D, 0.7D);
		this.targetPlayerGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);

		this.goalSelector.addGoal(0, this.attackGoal);
		this.goalSelector.addGoal(1, this.avoidPlayerGoal);
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new AttackOnCollideGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, this.targetPlayerGoal);
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();
		if (this.aggroCooldown == 200 && !this.canAttack) {
			this.goalSelector.removeGoal(this.avoidPlayerGoal);
			this.goalSelector.addGoal(0, this.attackGoal);
			this.targetSelector.addGoal(1, this.targetPlayerGoal);
			this.canAttack = true;
		}

		if (this.aggroCooldown == 0 && this.canAttack) {
			this.goalSelector.removeGoal(this.attackGoal);
			this.targetSelector.removeGoal(this.targetPlayerGoal);
			this.goalSelector.addGoal(1, this.avoidPlayerGoal);
			this.canAttack = false;
		}

		if (this.aggroCooldown < 201) {
			this.aggroCooldown++;
		}
	}

	@Override
	public void playerTouch(Player player) {
		if (!this.level().isClientSide() && this.distanceTo(player) <= 1.5F && this.canAttack) {
			this.aggroCooldown = 0;
		}
	}

	@Override
	protected void playAttackSound() {
		this.playSound(SoundRegistry.CRAB_SNIP.get(), 1.0F, 1.0F);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return 0.5F;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
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
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 5;
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	public boolean canDrownInFluidType(FluidType type) {
		return false;
	}

	public boolean lureToCrabPot(BlockPos pos) {
		return this.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, 1.0D);
	}

	@Nullable
	public MobItem getCrabPotItem() {
		return ItemRegistry.SILT_CRAB.get();
	}
}
