package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;
/** * Created by jnad325 on 7/14/15. */

public class EntityTempleGuardian extends EntityMob implements IEntityBL {
    public ControlledAnimation active = new ControlledAnimation(20);
    private boolean hadAttackTarget;

    public EntityTempleGuardian(World worldObj) {
        super(worldObj);
        getNavigator().setAvoidsWater(true);tasks.addTask(0, new EntityAISwimming(this));
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

    @Override

    protected String getHurtSound() {
        int randomSound = rand.nextInt(3) + 1;
        return "thebetweenlands:templeGuardianHurt" + randomSound;
    }

    @Override
    protected String getDeathSound() {return "thebetweenlands:templeGuardianDeath";}

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {// playStepSound
        if (!getActive()) return;
        int randomSound = rand.nextInt(3) + 1;
        playSound("thebetweenlands:templeGuardianStep" + randomSound, 1F, 1F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getAttackTarget() != null) {
            if (hadAttackTarget) setActive(true);
            hadAttackTarget = true;
        }
        else {
            setActive(false);
        }
        if (getActive()) active.increaseTimer();
        else active.decreaseTimer();
        if (active.getTimer() == 0) {
            getNavigator().clearPathEntity();
            rotationYaw = prevRotationYaw;
            renderYawOffset = rotationYaw;
            posX = prevPosX;
            posZ = prevPosZ;
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(19, (byte) 0);
    }

    @Override
    public boolean canBeCollidedWith() {
        return getActive();
    }

    public boolean getActive() {
        return dataWatcher.getWatchableObjectByte(19) == 1;
    }

    public void setActive(boolean active) {
        dataWatcher.updateObject(19, active ? (byte) 1 : (byte) 0);
    }

    @Override
    public String pageName() {
        return "templeGuardian";
    }
}