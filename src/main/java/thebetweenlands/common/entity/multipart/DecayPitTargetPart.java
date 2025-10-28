package thebetweenlands.common.entity.multipart;

import net.minecraft.world.damagesource.DamageSource;
import thebetweenlands.common.entity.DecayPitTarget;

public class DecayPitTargetPart extends GenericPartEntity<DecayPitTarget> {

	public final boolean shield;

	public DecayPitTargetPart(DecayPitTarget parent, float width, float height, boolean shield) {
		super(parent, width, height);
		this.shield = shield;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.shield;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return this.getParent().attackEntityFromPart(this, source, amount);
	}
}
