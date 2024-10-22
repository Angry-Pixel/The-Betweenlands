package thebetweenlands.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class SmolSludge extends Sludge {
	public SmolSludge(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Sludge.registerAttributes()
			.add(Attributes.MAX_HEALTH, 14.0D)
			.add(Attributes.ATTACK_DAMAGE, 1.0D);
	}

	@Override
	protected boolean canMakeTrail() {
		return false;
	}

	@Override
	public float getVoicePitch() {
		return (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.5F;
	}
}
