package thebetweenlands.common.entity.mobs;

import net.minecraft.world.World;

public class EntitySmollSludge extends EntitySludge{
    public EntitySmollSludge(World worldIn) {
        super(worldIn);
    }


    @Override
    protected float getSoundPitch() {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F;
    }
}
