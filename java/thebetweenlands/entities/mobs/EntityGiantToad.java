package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
    float angle = 0;
    public EntityGiantToad(World worldObj)
    {
        super(worldObj);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        tasks.addTask(5, new EntityAIWander(this, 0));
        //tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        //tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getNavigator().getPath() != null && !getNavigator().getPath().isFinished() && onGround)
        {
            if (ticksExisted % 30 == 0)
            {
                int index = getNavigator().getPath().getCurrentPathIndex();
                if (index < getNavigator().getPath().getCurrentPathLength()) {
                    PathPoint nextHopSpot = getNavigator().getPath().getPathPointFromIndex(index);
                    float x = (float) (nextHopSpot.xCoord - posX);
                    float y = (float) (nextHopSpot.yCoord - posY);
                    float z = (float) (nextHopSpot.zCoord - posZ);
                    angle = (float) (Math.atan2(z, x));
                    float distance = (float) Math.sqrt(x*x + z*z);
                    if (distance > 1) {
                        motionY += 0.5;
                        motionX += 0.3 * MathHelper.cos(angle);
                        motionZ += 0.3 * MathHelper.sin(angle);
                    }
                    else {
                        getNavigator().clearPathEntity();
                    }
                }
            }
        }
    }
}
