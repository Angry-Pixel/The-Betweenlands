package thebetweenlands.common.entity.multipart;

import net.minecraft.world.entity.Entity;
import thebetweenlands.common.entity.SwingingHammerTrap;

public class SwingngTrapMultipart extends GenericPartEntity<SwingingHammerTrap> {

    public SwingngTrapMultipart(SwingingHammerTrap parentMob, float width, float height) {
        super(parentMob, width, height);
    }

	@Override
    public boolean canCollideWith(Entity entity) {
        return entity.canBeCollidedWith() && !(entity == this.getParent());
    }
}

