package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class EntityMireSnail extends EntityAnimal implements IEntityBL {

	int shagCount = 0;

	public EntityMireSnail(World world) {
		super(world);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 0.4D));
		tasks.addTask(2, new EntityAIMate(this, 0.4D));
		tasks.addTask(3, new EntityAITempt(this, 0.4D, ItemMaterialsBL.createStack(EnumMaterialsBL.SLUDGE_BALL).getItem(), false));
		tasks.addTask(5, new EntityAIWander(this, 0.4D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		setSize(0.8F, 0.6F);
		stepHeight = 0.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(31, new Byte((byte) 0));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
    public boolean getCanSpawnHere() {
        return worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
	
	@Override
	protected boolean canDespawn() {
		if (getHasMated() == 1)
			return false;
		else
			return true;
	}
	
	public boolean isClimbing() {
		return !onGround && isOnLadder();
	}

	@Override
	public boolean isOnLadder() {
		return isCollidedHorizontally;
	}
	@Override
	protected String getLivingSound() {
		return "thebetweenlands:snailLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:snailHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:snailDeath";
	}
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (isBurning())
			entityDropItem(ItemMaterialsBL.createStack(BLItemRegistry.snailFleshCooked, 1, 0), 0.0F);
		else
			entityDropItem(ItemMaterialsBL.createStack(BLItemRegistry.snailFleshRaw, 1, 0), 0.0F);
		
		if (rand.nextBoolean())
			entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.MIRE_SNAIL_SHELL, 1), 0.0F);
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack is = player.inventory.getCurrentItem();

		if (is != null && isBreedingItem(is) && !shagging()) {
			player.swingItem();
			setTame((byte) 1);
			return super.interact(player);
		}
		
		return super.interact(player);
	}

	public boolean shagging() {
		return isInLove();
	}

	@Override
	public boolean isBreedingItem(ItemStack is) {
		return is != null && is.getItem() == BLItemRegistry.materialsBL && is.getItemDamage() == EnumMaterialsBL.SLUDGE_BALL.ordinal();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return new EntityMireSnailEgg(worldObj);
	}

	public void setTame(byte hasMated) {
		dataWatcher.updateObject(31, Byte.valueOf(hasMated));
	}

	public byte getHasMated() {
		return dataWatcher.getWatchableObjectByte(31);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("hasMated", getHasMated());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setTame(nbt.getByte("hasMated"));
	}
    
}
