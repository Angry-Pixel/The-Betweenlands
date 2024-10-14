package thebetweenlands.common.entity.monster;

import thebetweenlands.common.entity.GenericPartEntity;

public class SludgeWormMultipart extends GenericPartEntity<SludgeWorm> {

    public SludgeWormMultipart(SludgeWorm parentMob, float width, float height) {
        super(parentMob, width, height);
    }

	@Override
    protected double getDefaultGravity() {
        return 0.1;
    }

}

