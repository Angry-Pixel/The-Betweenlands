package thebetweenlands.common.entity.monster.spirit_tree;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class SmallSpiritTreeFace extends AbstractSmallSpritTreeFace implements Enemy {
	public SmallSpiritTreeFace(EntityType<? extends AbstractSmallSpritTreeFace> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 2.0D)
			.add(Attributes.ATTACK_DAMAGE, 4.0D)
			.add(Attributes.FOLLOW_RANGE, 48.0D);
	}

	@Override
	public boolean isActive() {
		return this.level().getDifficulty() != Difficulty.PEACEFUL;
	}
}
