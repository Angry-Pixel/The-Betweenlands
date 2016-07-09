package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySporeling extends EntityCreature implements IEntityBL {

    public boolean isFalling;

    public EntitySporeling(World world) {
        super(world);
        setSize(0.3F, 0.6F);
        stepHeight = 1.0F;

        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAvoidEntity<EntityLivingBase>(this, EntityLivingBase.class, 10.0F, 0.7D, 0.5D));
        tasks.addTask(2, new EntityAIPanic(this, 0.7D));
        tasks.addTask(3, new EntityAIWander(this, 0.5D));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        //TODO particles
        //worldObj.spawnParticle("reddust", posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * width, 1.0D + rand.nextDouble(), 1.0D + rand.nextDouble(), 1.0D + rand.nextDouble());
        super.onLivingUpdate();
    }

    @Override
    public void onUpdate() {
        if (!this.isInWater()) {
            if (!onGround && motionY < 0D && worldObj.getBlockState(getPosition().down()).getBlock() == Blocks.AIR) {
                motionY *= 0.7D;
                renderYawOffset += 10;
                setIsFalling(true);
            } else if (getIsFalling())
                setIsFalling(false);
        }
        super.onUpdate();
    }

    public boolean getIsFalling() {
        return isFalling;
    }

    private void setIsFalling(boolean state) {
        isFalling = state;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox()) && this.worldObj.getCollisionBoxes(this.getEntityBoundingBox()).isEmpty() && isOnShelfFungus();
    }

    private boolean isOnShelfFungus() {
        //TODO add shelf fungus
        return true;//worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBox().minY) - 1, MathHelper.floor_double(posZ))).getBlock() == Registries.INSTANCE.blockRegistry.treeFungus;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SPORELING_LIVING;
    }

    @Override
    protected SoundEvent getHurtSound() {
        return SoundRegistry.SPORELING_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SPORELING_DEATH;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int chance = rand.nextInt(4) + rand.nextInt(1 + looting);
        int amount;
        /*TODO add spores
        for (amount = 0; amount < chance; ++amount)
            entityDropItem(new ItemStack(Registries.INSTANCE.itemRegistry.spores), 0.0F);*/
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
