package thebetweenlands.entities.mobs;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.SwampTalisman;
import thebetweenlands.items.SwampTalisman.EnumTalisman;
import thebetweenlands.network.packets.PacketDruidTeleportParticle;

public class EntityDarkDruid extends EntityMob {
	private int attackTimer = 20;
	private int attackCounter;
	private byte isCasting;
	private int teleportDelay;
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
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		setSize(1.1F, 1.7F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
		if (target != null) {
			if (target.onGround && attackCounter == 0)
				setAttackTarget(target);
			if (!target.onGround && attackCounter == 0)
				setAttackTarget(null);
		}

		if (worldObj.isRemote) {
			if (getAttackTarget() != null && getEntitySenses().canSee(getAttackTarget()) && getDistanceSqToEntity(getAttackTarget()) <= 49F) {
				if (attackCounter == 0) {
					attackCounter++;
					tasks.removeTask(meleeAI);
				} else if (attackCounter < attackTimer) {
					attackCounter++;
					isCasting = 1;
					chargeSpell(getAttackTarget());
				}
				if (attackCounter >= attackTimer) {
					attackCounter = 0;
					isCasting = 0;
					castSpell(getAttackTarget());
					tasks.addTask(2, meleeAI);
				}
			} else {
				attackCounter = 0;
				isCasting = 0;
			}
		}

		if (getAttackTarget() != null)
			faceEntity(getAttackTarget(), 100.0F, 100.0F);

		if (worldObj.isRemote && isCasting == 1)
			spawnParticles();
		dataWatcher.updateObject(20, (byte) isCasting);

		if (!worldObj.isRemote && isEntityAlive() && getAttackTarget() != null)
			if (getAttackTarget().getDistanceSqToEntity(this) > 36.0D && isCasting == 0 && teleportDelay++ >= 20 && getAttackTarget().onGround)
				teleportNearEntity(getAttackTarget());
	}

	protected boolean teleportNearEntity(Entity entity) {
		double targetX = entity.posX + (rand.nextDouble() - 0.5D) * 6.0D;
		double targetY = entity.posY + (double) (rand.nextInt(3) - 1);
		double targetZ = entity.posZ + (rand.nextDouble() - 0.5D) * 6.0D;
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
				druidParticlePacketOrigin();
				EntityDarkDruid newDruid = new EntityDarkDruid(worldObj);
				newDruid.copyDataFrom(this, true);
				newDruid.setPosition(targetX, targetY, targetZ);
				if (worldObj.getCollidingBoundingBoxes(newDruid, newDruid.boundingBox).isEmpty() && !worldObj.isAnyLiquid(newDruid.boundingBox)) {
					flag = true;
					setDead();
					worldObj.spawnEntityInWorld(newDruid);
					druidParticlePacketTarget(newDruid);
					}
				else
					newDruid.setDead();
				}
			}

		if (!flag) {
			setPosition(x, y, z);
			return false;
		} else {
			teleportDelay = 0;
			worldObj.playSoundEffect(x, y, z, "thebetweenlands:druidTeleport", 1.0F, 1.0F);
			playSound("thebetweenlands:druidTeleport", 1.0F, 1.0F);
			return true;
		}
	}

	private void druidParticlePacketTarget(EntityDarkDruid newDruid) {
		World world = worldObj;
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
			//TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidTeleportParticle((float)newDruid.posX, (float) newDruid.posY, (float) newDruid.posZ), new TargetPoint(dim, newDruid.posX + 0.5D, newDruid.posY + 1.0D, newDruid.posZ + 0.5D, 64D));
			TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidTeleportParticle(newDruid)), new TargetPoint(dim, newDruid.posX + 0.5D, newDruid.posY + 1.0D, newDruid.posZ + 0.5D, 64D));
		}
	}

	private void druidParticlePacketOrigin() {
		World world = worldObj;
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
			//TheBetweenlands.networkWrapper.sendToAllAround(new MessageDruidTeleportParticle((float) posX, (float) posY, (float) posZ), new TargetPoint(dim, posX + 0.5D, posY + 1.0D, posZ + 0.5D, 64D));
			TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidTeleportParticle(this)), new TargetPoint(dim, posX + 0.5D, posY + 1.0D, posZ + 0.5D, 64D));
		}
	}

	public void spawnParticles() {
		double a = Math.toRadians(renderYawOffset);
		double offSetX = -Math.sin(a) * 0.5D;
		double offSetZ = Math.cos(a) * 0.5D;
		double pX = -Math.sin(a) * Math.random() * 0.25;
		double pY = Math.random() * 0.25 - 0.125;
		double pZ = Math.cos(a) * Math.random() * 0.25;
		TheBetweenlands.proxy.spawnCustomParticle("druidmagic", worldObj, posX + offSetX, posY + 1.0D, posZ + offSetZ, pX, pY, pZ, rand.nextFloat() * 1F + 0.5F);
	}

	public void chargeSpell(Entity entity) {
		if (entity.getDistanceToEntity(this) <= 4) {
			entity.motionX = 1.5D * Math.signum(entity.posX - posX);
			entity.motionZ = 1.5D * Math.signum(entity.posZ - posZ);
		} else {
			entity.motionX *= 0D;
			entity.motionZ *= 0D;
		}
		entity.motionY = 0.1D;
	}

	public void castSpell(Entity entity) {
		entity.motionX = 0.5D * Math.signum(entity.posX - posX);
		entity.motionZ = 0.5D * Math.signum(entity.posZ - posZ);
		entity.motionY = 1.5D;
	}

	@Override
	protected void dropRareDrop(int looting) {
		entityDropItem(SwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN, 1), 0.0F);
	}

	@Override
	public void dropFewItems(boolean recentlyHit, int looting) {
		int randomPiece = rand.nextInt(4);
		switch (randomPiece) {
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
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 5;
	}

	@Override
	protected String getLivingSound() {
		if (rand.nextBoolean())
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
