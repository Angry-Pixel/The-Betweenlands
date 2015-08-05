package thebetweenlands.entities.mobs;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityTempleGuardian extends EntityMob implements IEntityBL {
    public EntityTempleGuardian(World worldObj)
    {
        super(worldObj);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(5, new EntityAIWander(this, 0.3D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.4D, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }
}
