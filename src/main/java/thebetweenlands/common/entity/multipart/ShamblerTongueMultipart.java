package thebetweenlands.common.entity.multipart;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import thebetweenlands.common.entity.monster.Shambler;

public class ShamblerTongueMultipart extends GenericPartEntity<Shambler> {

    public ShamblerTongueMultipart(Shambler parentMob, float width, float height) {
        super(parentMob, width, height);
    }

	@Override
    public boolean canCollideWith(Entity entity) {
        return entity.canBeCollidedWith() && !(entity == this.getParent());
    }
}

