package thebetweenlands.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class Termite extends Monster implements BLEntity {

	private static final AttributeModifier SMALL_SCALE = new AttributeModifier(TheBetweenlands.prefix("small_termite"), -0.5D, AttributeModifier.Operation.ADD_VALUE);

	public Termite(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	public void setSmall(boolean small) {
		if (small) {
			this.getAttribute(Attributes.SCALE).addOrReplacePermanentModifier(SMALL_SCALE);
		} else {
			this.getAttribute(Attributes.SCALE).removeModifier(SMALL_SCALE);
		}
		this.refreshDimensions();
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 8.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.35D)
			.add(Attributes.ATTACK_DAMAGE, 1.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.TERMITE_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH.get();
	}
}
