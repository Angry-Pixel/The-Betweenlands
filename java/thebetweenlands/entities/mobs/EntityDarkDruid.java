package thebetweenlands.entities.mobs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
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
	private Entity lastAttackTarget = null;
	//private EntityLivingBase getEnityToAttack() = null;
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
		//tasks.addTask(6, new EntityAILookIdle(this));
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
			setTarget(null);

		if (tar != getEntityToAttack())
			if (getDistanceToEntity(tar) <= 8.0F)
				setTarget(tar);

		if (getEntityToAttack() != null && getEntityToAttack() != lastAttackTarget && getEntitySenses().canSee(getEntityToAttack())) {
			if (attackCounter == 0) {
				attackCounter++;
				tasks.removeTask(meleeAI);
			}
			else if (attackCounter < attackTimer) {
				attackCounter++;
				isCasting = 1;
				chargeSpell(getEntityToAttack());
			}
			else {
				attackCounter = 0;
				isCasting = 0;
				castSpell(getEntityToAttack());
				lastAttackTarget = getEntityToAttack();
				tasks.addTask(2, meleeAI);
				forgetCounter = forgetTimer;
			}
		}
		else {
			attackCounter = 0;
			isCasting = 0;
			setTarget(null);
			if (forgetCounter <= 0)
				lastAttackTarget = null;
			else
				forgetCounter--;
		}
		
        if (getEntityToAttack() != null)
            faceEntity(getEntityToAttack(), 100.0F, 100.0F);
		
		if (worldObj.isRemote && isCasting == 1) {
			spawnParticles();
		}
		
		//Update data watcher
		dataWatcher.updateObject(20, Byte.valueOf((byte) isCasting));
	}

	public void spawnParticles() {
		double a = Math.toRadians(renderYawOffset);
		double offSetX = -Math.sin(a) * 0.5D;
		double offSetZ = Math.cos(a) * 0.5D;
		
		double pX = -Math.sin(a) * Math.random() * 0.25;
		double pY = Math.random() * 0.25 - 0.125;
		double pZ = Math.cos(a) * Math.random() * 0.25;

		EntityDruidCastingFX particle = new EntityDruidCastingFX(worldObj, posX + offSetX, posY + 1.3, posZ + offSetZ, pX, pY, pZ);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);

	}

	public void chargeSpell(Entity entity) {
		entity.motionX *= 0.0D;
		entity.motionZ *= 0.0D;
		entity.motionY = 0.05D;
	}

	public void castSpell(Entity entity) {
		entity.motionX = 2.0D * Math.signum(entity.posX - posX);
		entity.motionZ = 2.0D * Math.signum(entity.posZ - posZ);
		entity.motionY = 1.5D;
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
		if(rand.nextBoolean())
			return "thebetweenlands:darkdruidliving1";
		else
			return "thebetweenlands:darkdruidliving2";
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