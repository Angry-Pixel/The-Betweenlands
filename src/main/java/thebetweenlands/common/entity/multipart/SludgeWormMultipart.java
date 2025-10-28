package thebetweenlands.common.entity.multipart;

import net.minecraft.world.damagesource.DamageSource;
import thebetweenlands.common.entity.monster.SludgeWorm;

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

