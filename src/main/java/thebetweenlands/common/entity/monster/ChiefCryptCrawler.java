package thebetweenlands.common.entity.monster;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.ai.goals.CryptCrawlerChargeGoal;
import thebetweenlands.common.registries.ItemRegistry;

public class ChiefCryptCrawler extends BipedCryptCrawler {
	public ChiefCryptCrawler(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 20;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 100.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.28D)
			.add(Attributes.ATTACK_DAMAGE, 4.25D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.75D);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(2, new CryptCrawlerChargeGoal(this));
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.SYRMORITE_SHIELD.get()));
		this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.OCTINE_SWORD.get()));
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 0.5F;
	}
}
