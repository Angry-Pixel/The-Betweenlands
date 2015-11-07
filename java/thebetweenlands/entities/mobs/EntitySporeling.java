package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class EntitySporeling extends EntityCreature implements IEntityBL {
	public boolean isFalling;
	public EntitySporeling(World world) {
		super(world);
		setSize(0.3F, 0.6F);
		stepHeight = 1.0F;
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAvoidEntity(this, EntityLivingBase.class, 10.0F, 0.7D, 0.5D));
		tasks.addTask(2, new EntityAIPanic(this, 0.7D));
		tasks.addTask(3, new EntityAIWander(this, 0.5D));
		tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		worldObj.spawnParticle("reddust", posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * width, 1.0D + rand.nextDouble(), 1.0D + rand.nextDouble(), 1.0D + rand.nextDouble());
		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if (!onGround && motionY < 0D && worldObj.getBlock((int)posX, (int)posY - 1, (int)posZ) == Blocks.air) {
			motionY *= 0.7D;
			renderYawOffset += 10;
			setIsFalling(true);
		}
		else
			if(getIsFalling())
				setIsFalling(false);
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
		int by = MathHelper.floor_double(this.posY);
		int bx = MathHelper.floor_double(this.posX);
		int bz = MathHelper.floor_double(this.posZ);
		for(int yo = 0; yo < 16; yo++) {
			Block cb = this.worldObj.getBlock(bx, by - yo, bz);
			if(cb == BLBlockRegistry.treeFungus) {
				by = by - yo + 1;
				break;
			}
		}
		this.posY = by;
		this.setPosition(this.posX, this.posY, this.posZ);
		boolean canSpawn = super.getCanSpawnHere() && isOnShelfFungus();
		return canSpawn;
	}

	private boolean isOnShelfFungus() {
		return worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)) == BLBlockRegistry.treeFungus;
	}

	//To prevent wrong spawns on chunk generate because the spawning system is broken and doesn't check getCanSpawnHere() on chunk generate...
	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityData) {
		if(!this.isOnShelfFungus()) this.setDead();
		return super.onSpawnWithEgg(entityData);
	}

	@Override
	protected void fall(float damage) {
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:sporelingLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:sporelingHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:sporelingDeath";
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int chance = rand.nextInt(4) + rand.nextInt(1 + looting);
		int amount;
		for (amount = 0; amount < chance; ++amount)
			entityDropItem(ItemGeneric.createStack(EnumItemGeneric.SPORES), 0.0F);
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}
}