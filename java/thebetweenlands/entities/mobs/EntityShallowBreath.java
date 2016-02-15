package thebetweenlands.entities.mobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;

import java.util.Random;

/**
 * Created by Bart on 15/02/2016.
 */
public class EntityShallowBreath extends EntityMob {
    public EntityShallowBreath(World world) {
        super(world);
        setSize(0.8F, 0.6F);
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1D));
    }


    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1d);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 5;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote && ticksExisted % 4 == 0)
            renderParticles(worldObj, posX, posY, posZ, rand, motionX, motionZ);
    }

    @SideOnly(Side.CLIENT)
    public void renderParticles(World world, double x, double y, double z, Random rand, double motionX, double motionZ) {
        for (int count = 0; count < 5; ++count) {
            BLParticle.SWAMP_SMOKE.spawn(world, x + rand.nextInt(7) / 10f, y + rand.nextInt(5) / 10f, z + rand.nextInt(7) / 10f, 0f - motionX / 10, 0.5f, 0f - motionZ / 10, 0);
        }
    }


    @Override
    public boolean canBePushed() {
        return false;
    }
}
