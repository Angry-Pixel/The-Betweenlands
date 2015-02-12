package thebetweenlands.entities.mobs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.particles.EntityDruidCastingFX;

public class EntityDarkDruid extends EntityMob {
	private int attackTimer = 140;
	private int attackCounter;
	private int forgetTimer = 40;
	private int forgetCounter = 0;
	private byte isCasting;
	private EntityLivingBase lastAttackTarget = null;
	private EntityLivingBase currentAttackTarget = null;
	public EntityAIAttackOnCollide meleeAI = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.23F, false); 
	public EntityAIWander wanderAI = new EntityAIWander(this, 0.23F);
	
	public EntityDarkDruid(World par1World) {   
		super(par1World); 
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIBreakDoor(this));
		tasks.addTask(2, meleeAI);
		tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 0.23F));
		tasks.addTask(4, wanderAI);
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 24.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24, true));
		setSize(1.1F, 1.7F);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, Byte.valueOf((byte) 0));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		EntityPlayer tar = worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);
		if (tar == null)
			currentAttackTarget = null;

		if (tar != currentAttackTarget)
			if (getDistanceToEntity(tar) <= 8.0F)
				currentAttackTarget = tar;

		if (currentAttackTarget != null && currentAttackTarget != lastAttackTarget && getEntitySenses().canSee(currentAttackTarget)) {
			if (attackCounter == 0) {
				attackCounter++;
				tasks.removeTask(meleeAI);
			}
			else if (attackCounter < attackTimer) {
				attackCounter++;
				isCasting = 1;
				chargeSpell(currentAttackTarget);
				spawnParticles();
			}
			else {
				attackCounter = 0;
				isCasting = 0;
				castSpell(currentAttackTarget);
				lastAttackTarget = currentAttackTarget;
				currentAttackTarget = null;
				tasks.addTask(2, meleeAI);
				forgetCounter = forgetTimer;
			}
		}
		else {
			attackCounter = 0;
			isCasting = 0;
			currentAttackTarget = null;
			if (forgetCounter <= 0)
				lastAttackTarget = null;
			else
				forgetCounter--;
		}
		
		//Update data watcher
		dataWatcher.updateObject(20, Byte.valueOf((byte) isCasting));
	}

	public void spawnParticles() {
		double pX = Math.random() * 0.25 - 0.125;
		double pY = Math.random() * 0.25 - 0.125;
		double pZ = Math.random() * 0.25 - 0.125;

		double pPosX = posX + Math.sin(-rotationYaw * Math.PI / 180);
		double pPosZ = posZ + Math.cos(-rotationYaw * Math.PI / 180);

		EntityDruidCastingFX particle = new EntityDruidCastingFX(worldObj, 
				pPosX + Math.sin(-rotationYaw * Math.PI / 180 - Math.PI / 2) / 2.5 + pX, 
				posY + 1.4 + pY, 
				pPosZ + Math.cos(-rotationYaw * Math.PI / 180 - Math.PI / 2) / 2.5 + pZ,
				-Math.sin(-rotationYaw * Math.PI / 180) + pX,
				pY,
				-Math.cos(-rotationYaw * Math.PI / 180) + pZ);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);

		pX = Math.random() * 0.25 - 0.125;
		pY = Math.random() * 0.25 - 0.125;
		pZ = Math.random() * 0.25 - 0.125;

		EntityDruidCastingFX particle2 = new EntityDruidCastingFX(worldObj, 
				pPosX + Math.sin(-rotationYaw * Math.PI / 180 + Math.PI / 2) / 2.5 + pX, 
				posY + 1.4 + pY, 
				pPosZ + Math.cos(-rotationYaw * Math.PI / 180 + Math.PI / 2) / 2.5 + pZ,
				-Math.sin(-rotationYaw * Math.PI / 180) + pX,
				pY,
				-Math.cos(-rotationYaw * Math.PI / 180) + pZ);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle2);
	}

	public void chargeSpell(EntityLivingBase currentAttackTarget2) {
		currentAttackTarget2.motionX *= 0.0D;
		currentAttackTarget2.motionZ *= 0.0D;
		currentAttackTarget2.motionY = 0.05D;
	}

	public void castSpell(EntityLivingBase currentAttackTarget2) {
		currentAttackTarget2.motionX = 2.0D * Math.signum(currentAttackTarget2.posX - posX);
		currentAttackTarget2.motionZ = 2.0D * Math.signum(currentAttackTarget2.posZ - posZ);
		currentAttackTarget2.motionY = 1.5D;
	}

	//TODO: Fix
	/*@Override
	protected int getDropItemId()
	{
		return ID;
	}*/

	@Override
	public void dropFewItems(boolean par1, int par2) {
		//TODO: Fix
		/*int RM;
		RM = rand.nextInt(4);
		if (RM == 0) {
			ID = Ids.swampTalismanPiece1_actual+Ids.idShift;
		}
		else if (RM == 1) {
			ID = Ids.swampTalismanPiece2_actual+Ids.idShift;
		}
		else if (RM == 2) {
			ID = Ids.swampTalismanPiece3_actual+Ids.idShift;
		}
		else if (RM == 3) {
			ID = Ids.swampTalismanPiece4_actual+Ids.idShift;
		}

		dropItem(ID, 1);*/
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox) ;
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:darkdruidliving"; 
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:darkdruidhit";
	}

	@Override
	protected String getDeathSound() { 
		return "thebetweenlands:darkdruiddie"; 
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}
}