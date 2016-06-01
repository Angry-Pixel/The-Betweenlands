package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityMireSnail extends EntityAnimal implements IEntityBL {
    private static final DataParameter<Boolean> HAS_MATED = EntityDataManager.createKey(EntityMireSnail.class, DataSerializers.BOOLEAN);
    int shagCount = 0;

    public EntityMireSnail(World world) {
        super(world);
        setPathPriority(PathNodeType.WATER, -1.0f);

        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 0.4D));
        tasks.addTask(2, new EntityAIMate(this, 0.4D));
        //FIXME I think this wont work because of meta?
        tasks.addTask(3, new EntityAITempt(this, 0.4D, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.SLUDGE_BALL).getItem(), false));
        tasks.addTask(5, new EntityAIWander(this, 0.4D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        setSize(0.75F, 0.6F);
        stepHeight = 0.0F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(HAS_MATED, false);
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj.getCollisionBoxes(getEntityBoundingBox()).isEmpty() && !worldObj.containsAnyLiquid(getEntityBoundingBox());
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 3;
    }

    @Override
    protected boolean canDespawn() {
        return !hasMated();
    }

    //TODO add snailLiving sound
    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    //TODO add snailHurt sound
    @Override
    protected SoundEvent getHurtSound() {
        return super.getHurtSound();
    }

    //TODO add snailDeath sound
    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        if (isBurning())
            entityDropItem(new ItemStack(ItemRegistry.SNAIL_FLESH_COOKED, 1, 0), 0.0F);
        else
            entityDropItem(new ItemStack(ItemRegistry.SNAIL_FLESH_RAW, 1, 0), 0.0F);

        if (rand.nextBoolean())
            entityDropItem(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.MIRE_SNAIL_SHELL), 0.0F);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (stack != null && isBreedingItem(stack) && !shagging()) {
            player.swingArm(hand);
            setHasMated(true);
            return super.processInteract(player, hand, stack);
        }

        return super.processInteract(player, hand, stack);
    }

    public boolean shagging() {
        return isInLove();
    }

    @Override
    public boolean isBreedingItem(ItemStack is) {
        return is != null && is.getItem() == ItemRegistry.ITEMS_GENERIC && is.getItemDamage() == ItemGeneric.EnumItemGeneric.SLUDGE_BALL.ordinal();
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return new EntityMireSnailEgg(worldObj);
    }

    public void setHasMated(boolean hasMated) {
        dataManager.set(HAS_MATED, hasMated);
    }

    public boolean hasMated() {
        return dataManager.get(HAS_MATED);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("hasMated", hasMated());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setHasMated(nbt.getBoolean("hasMated"));
    }

}
