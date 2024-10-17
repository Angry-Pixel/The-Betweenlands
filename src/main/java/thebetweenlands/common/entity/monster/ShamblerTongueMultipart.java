package thebetweenlands.common.entity.monster;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import thebetweenlands.common.entity.GenericPartEntity;

public class ShamblerTongueMultipart extends GenericPartEntity<Shambler> {

    public ShamblerTongueMultipart(Shambler parentMob, float width, float height) {
        super(parentMob, width, height);
    }
    
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }
}

