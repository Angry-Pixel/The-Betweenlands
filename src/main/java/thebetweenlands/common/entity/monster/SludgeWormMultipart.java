package thebetweenlands.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import thebetweenlands.common.entity.GenericPartEntity;

public class SludgeWormMultipart extends GenericPartEntity<SludgeWorm> {

    public SludgeWormMultipart(SludgeWorm parentMob, float width, float height) {
        super(parentMob, width, height);
    }

	@Override
    protected double getDefaultGravity() {
        return 0.1D;
    }

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return !this.isInvulnerableTo(source) && this.getParent().hurtSegment(this, source, amount);
	}
}

