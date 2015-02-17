package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
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
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.SwampTalisman;
import thebetweenlands.items.SwampTalisman.EnumTalisman;
import thebetweenlands.network.packet.DruidTeleportParticleMessage;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityDarkDruid extends EntityMob {
	private int attackTimer = 50;
	private int attackCounter;
	private int forgetTimer = 40;
	private int forgetCounter = 0;
	private byte isCasting;
	private Entity lastAttackTarget = null;

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
			if (getDistanceToEntity(tar) <= 6.0F)
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
		
		if (!worldObj.isRemote && isEntityAlive() && getEntityToAttack() != null)
			if (tar.getDistanceSqToEntity(this) > 9.0D && isCasting == 0 && forgetCounter == 0)
				teleportNearEntity(tar);

		//Update data watcher
		dataWatcher.updateObject(20, Byte.valueOf((byte) isCasting));
	}

	protected boolean teleportNearEntity(Entity entity) {
		double targetX = entity.posX + (rand.nextDouble() - 0.5D) * 4.0D;
		double targetY = posY + (double) (rand.nextInt(3) - 1);
		double targetZ = entity.posZ + (rand.nextDouble() - 0.5D) * 4.0D;
		return teleportTo(targetX, targetY, targetZ);
	}

	protected boolean teleportTo(double targetX, double targetY, double targetZ) {
		double x = posX;
		double y = posY;
		double z = posZ;
		boolean flag = false;
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(i, j, k)) {
			boolean flag1 = false;

			while (!flag1 && j > 0) {
				Block block = worldObj.getBlock(i, j - 1, k);

				if (block.getMaterial().blocksMovement()) {
					flag1 = true;
				} else {
					--posY;
					--j;
				}
			}

			if (flag1) {
				spawnDruidParticlePacket();
				setPosition(targetX, targetY, targetZ);
				if (worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)) {
					flag = true;
				}
			}
		}

		if (!flag) {
			setPosition(x, y, z);
			return false;
		} else {
			spawnDruidParticlePacket();
			worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1.0F, 1.0F);
			playSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	private void spawnDruidParticlePacket() {
     	World world = worldObj;
		int dim = 0;
		if(world instanceof WorldServer) {
			dim = ((WorldServer)world).provider.dimensionId;
            TheBetweenlands.networkWrapper.sendToAllAround(new DruidTeleportParticleMessage((float)posX, (float)posY + 1, (float)posZ),  new TargetPoint(dim, posX + 0.5D, posY + 1.0D, posZ + 0.5D, 64D));
		}	
	}

	public void spawnParticles() {
		double a = Math.toRadians(renderYawOffset);
		double offSetX = -Math.sin(a) * 0.5D;
		double offSetZ = Math.cos(a) * 0.5D;
		double pX = -Math.sin(a) * Math.random() * 0.25;
		double pY = Math.random() * 0.25 - 0.125;
		double pZ = Math.cos(a) * Math.random() * 0.25;
		TheBetweenlands.proxy.spawnCustomParticle("druidmagic", worldObj, posX + offSetX, posY + 1.0D, posZ + offSetZ, pX, pY, pZ, rand.nextFloat() * 0.5F + 0.5F);
	}

	public void chargeSpell(Entity entity) {
		entity.motionX *= 0.0D;
		entity.motionZ *= 0.0D;
		entity.motionY = 0.05D;
	}

	public void castSpell(Entity entity) {
		entity.motionX = 0.5D * Math.signum(entity.posX - posX);
		entity.motionZ = 0.5D * Math.signum(entity.posZ - posZ);
		entity.motionY = 1D;
	}

	@Override
	protected void dropRareDrop(int looting) {
		entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN, 1), 0.0F);
	}

	@Override
	public void dropFewItems(boolean recentlyHit, int looting) {
		int randomPiece = rand.nextInt(4);
		switch(randomPiece) {
		case 0:
			entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_1, 1), 0.0F);
			break;
		case 1:
			entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_2, 1), 0.0F);
			break;
		case 2:
			entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_3, 1), 0.0F);
			break;
		case 3:
			entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_4, 1), 0.0F);
			break;
		}
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox) ;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 5;
	}

	@Override
	protected String getLivingSound() {
		if(rand.nextBoolean())
			return "thebetweenlands:darkDruidLiving1";
		else
			return "thebetweenlands:darkDruidLiving2";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:darkDruidHit";
	}

	@Override
	protected String getDeathSound() { 
		return "thebetweenlands:darkDruidDeath"; 
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}
}
