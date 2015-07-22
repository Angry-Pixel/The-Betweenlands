package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
    public EntityGiantToad(World worldObj)
    {
        super(worldObj);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }
}
