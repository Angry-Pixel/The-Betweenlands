package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.entityAI.EntityAIBLBreakDoor;
import thebetweenlands.items.*;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.utils.AnimationMathHelper;

public class EntitySwampHag extends EntityMob implements IEntityBL {
	public float jawFloat;
	public float breatheFloat;
	AnimationMathHelper animationTalk = new AnimationMathHelper();
	AnimationMathHelper animationBreathe = new AnimationMathHelper();
	private int animationTick;
	private byte randomLivingSound;

	public EntitySwampHag(World world) {
		super(world);
		this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBLBreakDoor(this, Blocks.wooden_door, 10));
       // this.tasks.addTask(2, new EntityAIBLBreakDoor(this, Blocks.iron_door, 20));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1D, false));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 1D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
        this.setSize(0.6F, 1.8F);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(19, (byte) 0);
		dataWatcher.addObject(20, (byte) 0);
		dataWatcher.addObject(21, 0);
	}

	@Override
    protected boolean isAIEnabled() {
    	return true;
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox)&& worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(4) + 1;
		setTalkSound((byte) randomSound);
		return "thebetweenlands:swampHagLiving" + getTalkSound();
	}

	private void setTalkSound(int soundIndex) {
		dataWatcher.updateObject(19, (byte) soundIndex);
	}
	
	private byte getTalkSound() {
		return dataWatcher.getWatchableObjectByte(19);
	}

	@Override
	protected String getHurtSound() {
		setTalkSound(4);
		setShouldJawMove(true);
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:swampHagHurt" + randomSound;
	}

	@Override
	protected String getDeathSound() {
		setTalkSound(4);
		setShouldJawMove(true);
		return "thebetweenlands:swampHagDeath";
	}

	@Override
	public void onLivingUpdate() {
		breatheFloat = animationBreathe.swing(0.2F, 0.5F, false);

		if (!worldObj.isRemote) {
			updateLivingSoundTime();
		}

		if (animationTick > 0) {
			animationTick--;
		}

		if (animationTick == 0) {
			setShouldJawMove(false);
			jawFloat = animationTalk.swing(0F, 0F, true);
		}

		if (getLivingSoundTime() == -getTalkInterval())
			setShouldJawMove(true);

		if (!shouldJawMove())
			jawFloat = animationTalk.swing(0F, 0F, true);
		else if (shouldJawMove() && getTalkSound() != 3 && getTalkSound() != 4)
			jawFloat = animationTalk.swing(2.0F, 0.1F, false);
		else if (shouldJawMove() && getTalkSound() == 3 || shouldJawMove() && getTalkSound() == 4)
			jawFloat = animationTalk.swing(0.4F, 1.2F, false);
		super.onLivingUpdate();	
	}

	public void setShouldJawMove(boolean jawState) {
		dataWatcher.updateObject(20, (byte) (jawState ? 1 : 0));
		if (jawState)
			animationTick = 20;
	}

	public boolean shouldJawMove() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	private void updateLivingSoundTime() {
		dataWatcher.updateObject(21, livingSoundTime);
	}

	private int getLivingSoundTime() {
		return dataWatcher.getWatchableObjectInt(21);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SLIMY_BONE, 3), 0F);
	}
}
