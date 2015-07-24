package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
    float angle = 0;
    int leapOffset;
    public ControlledAnimation leapingAnim = new ControlledAnimation(4);
    public EntityGiantToad(World worldObj)
    {
        super(worldObj);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(5, new EntityAIWander(this, 0));
        tasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));

        leapOffset = rand.nextInt(29);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getAttackTarget() != null) getNavigator().tryMoveToEntityLiving(getAttackTarget(), 0);
        if (getNavigator().getPath() != null && !getNavigator().getPath().isFinished() && onGround)
        {
            if (ticksExisted % 30 == leapOffset)
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
        if (onGround) leapingAnim.decreaseTimer();
        else leapingAnim.increaseTimer();
    }
}
