package thebetweenlands.common.entity;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IEntityMultiPartPitTarget
{
    World getWorld();

    boolean attackEntityFromPart(EntityDecayPitTargetPart part, DamageSource source, float damage);

}