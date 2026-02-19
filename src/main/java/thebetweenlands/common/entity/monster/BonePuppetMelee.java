package thebetweenlands.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;

public class BonePuppetMelee extends BonePuppetBase {

	public BonePuppetMelee(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public BonePuppetMelee(Level level, LivingEntity master) {
        super(EntityRegistry.BONE_PUPPET_MELEE.get(), level);
        setParentEntity(master);
    }

    @Override
    protected void registerGoals() {
    	goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PuppetMeleeAttackGoal(this, 1D, true));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

	public static class PuppetMeleeAttackGoal extends MeleeAttackGoal {

		private final BonePuppetMelee puppet;

		public PuppetMeleeAttackGoal(BonePuppetMelee puppet, double speedModifier, boolean mustSee) {
			super(puppet, speedModifier, mustSee);
			this.puppet = puppet;
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity target) {
			if (canPerformAttack(target)) {
				puppet.setAttacking(true);
				if (puppet.getAttackTimer() >= 10) {
					puppet.doHurtTarget(target);
					resetAttackCooldown();
				}
			}
		}
	}

}
