package thebetweenlands.entities.mobs;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.entityAI.EntityAIDruidTeleport;
import thebetweenlands.entities.entityAI.EntityAIHurtByTargetDruid;
import thebetweenlands.entities.entityAI.EntityAINearestAttackableTargetDruid;
import thebetweenlands.items.misc.ItemSwampTalisman;
import thebetweenlands.items.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.network.packet.server.PacketDruidTeleportParticle;
import thebetweenlands.utils.MathUtils;

public class EntityDarkDruid extends EntityMob {
	private static final int MIN_ATTACK_DELAY = 40, MAX_ATTACK_DELAY = 120;
	private static final int MAX_ATTACK_TIME = 20;

	private static final int MAX_ATTACK_ANIMATION_TIME = 8;

	private EntityAIAttackOnCollide meleeAI = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.23F, false);
	private EntityAIWander wanderAI = new EntityAIWander(this, 0.23F);
	private EntityAIWatchClosest watchAI = new EntityAIWatchClosest(this, EntityPlayer.class, 16);

	private int attackDelayCounter;
	private int attackCounter;
	private int teleportCooldown;
	private boolean isWatching = true;

	private int prevAttackAnimationTime;
	private int attackAnimationTime;

	public EntityDarkDruid(World world) {
		super(world);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIBreakDoor(this));
		tasks.addTask(2, meleeAI);
		tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 0.23F));
		tasks.addTask(4, wanderAI);
		tasks.addTask(5, watchAI);
		tasks.addTask(6, new EntityAIDruidTeleport(this));
		targetTasks.addTask(1, new EntityAIHurtByTargetDruid(this));
		targetTasks.addTask(2, new EntityAINearestAttackableTargetDruid(this));
		setSize(0.9F, 1.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getAttackTarget() != null) {
			if(this.attackDelayCounter > 0 && !this.isCasting() && getAttackTarget().getDistanceToEntity(this) < 10.0D) {
				this.attackDelayCounter--;
			}
			if(this.attackDelayCounter <= 0 || this.attackCounter > 0) {
				if(getEntitySenses().canSee(getAttackTarget())) {
					if (attackCounter == 0) {
						if(getAttackTarget().onGround) {
							attackCounter++;
							if (!worldObj.isRemote) {
								tasks.removeTask(meleeAI);
							}
						}
					} else if (attackCounter < MAX_ATTACK_TIME) {
						attackCounter++;
						startCasting();
						if (!worldObj.isRemote) {
							chargeSpell(getAttackTarget());
						}
					} else if (attackCounter >= MAX_ATTACK_TIME) {
						this.attackDelayCounter = MIN_ATTACK_DELAY + this.rand.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
						attackCounter = 0;
						stopCasting();
						if (!worldObj.isRemote) {
							castSpell(getAttackTarget());
							tasks.addTask(2, meleeAI);
						}
					}
				}
			}
		} else if (isCasting() || attackCounter != 0) {
			if(this.attackDelayCounter <= 0) {
				this.attackDelayCounter = MIN_ATTACK_DELAY + this.rand.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
			}
			attackCounter = 0;
			stopCasting();
		}
		if (worldObj.isRemote) {
			prevRenderYawOffset = prevRotationYaw;
			renderYawOffset = rotationYaw;
			prevAttackAnimationTime = attackAnimationTime;
			if (isCasting()) {
				if (attackAnimationTime < MAX_ATTACK_ANIMATION_TIME) {
					attackAnimationTime++;
				}
				spawnParticles();
			} else {
				if (attackAnimationTime > 0) {
					attackAnimationTime--;
				}
			}
		} else {
			if (getAttackTarget() != null) {
				faceEntity(getAttackTarget(), 100, 100);
			}
			if (teleportCooldown > 0) {
				teleportCooldown--;
			}
		}
	}

	private void enableWatch() {
		if (!isWatching) {
			isWatching = true;
			tasks.addTask(5, watchAI);
		}
	}

	private void disableWatch() {
		if (isWatching) {
			isWatching = false;
			tasks.removeTask(watchAI);
		}
	}

	public boolean teleportNearEntity(Entity entity) {
		double targetX = entity.posX + (rand.nextDouble() - 0.5D) * 6.0D;
		double targetY = entity.posY + (rand.nextInt(3) - 1);
		double targetZ = entity.posZ + (rand.nextDouble() - 0.5D) * 6.0D;
		double x = posX;
		double y = posY;
		double z = posZ;
		boolean successful = false;
		int blockX = MathHelper.floor_double(posX);
		int blockY = MathHelper.floor_double(posY);
		int blockZ = MathHelper.floor_double(posZ);
		if (worldObj.blockExists(blockX, blockY, blockZ)) {
			boolean validBlock = false;
			while (!validBlock && blockY > 0) {
				Block block = worldObj.getBlock(blockX, blockY - 1, blockZ);

				if (block.getMaterial().blocksMovement()) {
					validBlock = true;
				} else {
					posY--;
					blockY--;
				}
			}
			if (validBlock) {
				teleportCooldown = rand.nextInt(20 * 2) + 20 * 2;
				EntityDarkDruid newDruid = new EntityDarkDruid(worldObj);
				newDruid.copyDataFrom(this, true);
				newDruid.setPosition(targetX, targetY, targetZ);
				newDruid.faceEntity(entity, 100, 100);
				newDruid.attackDelayCounter = MIN_ATTACK_DELAY + this.rand.nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
				if (worldObj.getCollidingBoundingBoxes(newDruid, newDruid.boundingBox).isEmpty() && !worldObj.isAnyLiquid(newDruid.boundingBox)) {
					successful = true;
					setDead();
					worldObj.spawnEntityInWorld(newDruid);
					druidParticlePacketOrigin();
					druidParticlePacketTarget(newDruid);
				} else
					newDruid.setDead();
			}
		}

		if (successful) {
			worldObj.playSoundEffect(x, y, z, "thebetweenlands:druidTeleport", 1.0F, 1.0F);
			playSound("thebetweenlands:druidTeleport", 1.0F, 1.0F);
			return true;
		}
		setPosition(x, y, z);
		return false;
	}

	private void druidParticlePacketTarget(EntityDarkDruid newDruid) {
		World world = worldObj;
		if (world instanceof WorldServer) {
			int dim = ((WorldServer) world).provider.dimensionId;
			TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidTeleportParticle(newDruid)), new TargetPoint(dim, newDruid.posX + 0.5D, newDruid.posY + 1.0D, newDruid.posZ + 0.5D, 64D));
		}
	}

	private void druidParticlePacketOrigin() {
		World world = worldObj;
		if (world instanceof WorldServer) {
			int dim = ((WorldServer) world).provider.dimensionId;
			TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidTeleportParticle(this)), new TargetPoint(dim, posX + 0.5D, posY + 1.0D, posZ + 0.5D, 64D));
		}
	}

	public void spawnParticles() {
		double yaw = rotationYaw * MathUtils.DEG_TO_RAD;
		double y = Math.cos(-rotationPitch * MathUtils.DEG_TO_RAD);
		double offsetX = -Math.sin(yaw) * 0.5D * y;
		double offsetY = 1.2 - Math.sin(-rotationPitch * MathUtils.DEG_TO_RAD) * 0.5D * y;
		double offsetZ = Math.cos(yaw) * 0.5D * y;
		double motionX = -Math.sin(yaw) * y * 0.2 * (rand.nextDouble() * 0.7 + 0.3) + rand.nextDouble() * 0.05 - 0.025;
		double motionY = Math.sin(-rotationPitch * MathUtils.DEG_TO_RAD) + rand.nextDouble() * 0.25 - 0.125;
		double motionZ = Math.cos(yaw) * y * 0.2 * (rand.nextDouble() * 0.7 + 0.3) + rand.nextDouble() * 0.05 - 0.025;
		BLParticle.DRUID_MAGIC.spawn(worldObj, posX + offsetX, posY + offsetY, posZ + offsetZ, motionX, motionY, motionZ, rand.nextFloat() + 0.5F);
	}

	public void chargeSpell(Entity entity) {
		if (entity.getDistanceToEntity(this) <= 4) {
			double dx = entity.posX - this.posX;
			double dz = entity.posZ - this.posZ;
			double len = Math.sqrt(dx*dx+dz*dz);
			entity.motionX = 1.5 * dx / len;
			entity.motionZ = 1.5 * dz / len;
		} else {
			entity.motionX = 0;
			entity.motionZ = 0;
		}
		entity.motionY = 0.1;
		entity.velocityChanged = true;
	}

	public void castSpell(Entity entity) {
		double dx = entity.posX - this.posX;
		double dz = entity.posZ - this.posZ;
		double len = Math.sqrt(dx*dx+dz*dz);
		entity.motionX = 0.5 * dx / len;
		entity.motionZ = 0.5 * dz / len;
		entity.motionY = 1.05D;
		entity.velocityChanged = true;
	}

	@Override
	protected void dropRareDrop(int looting) {
		entityDropItem(ItemSwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN, 1), 0);
	}

	@Override
	public void dropFewItems(boolean recentlyHit, int looting) {
		int randomPiece = rand.nextInt(4);
		switch (randomPiece) {
		case 0:
			entityDropItem(ItemSwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_1, 1), 0);
			break;
		case 1:
			entityDropItem(ItemSwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_2, 1), 0);
			break;
		case 2:
			entityDropItem(ItemSwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_3, 1), 0);
			break;
		case 3:
			entityDropItem(ItemSwampTalisman.createStack(EnumTalisman.SWAMP_TALISMAN_4, 1), 0);
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
		return "thebetweenlands:darkDruidLiving";
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
		return 1;
	}

	public int getAttackCounter() {
		return attackCounter;
	}

	public void startCasting() {
		dataWatcher.updateObject(20, (byte) 1);
		enableWatch();
	}

	public void stopCasting() {
		dataWatcher.updateObject(20, (byte) 0);
		disableWatch();
	}


	public boolean isCasting() {
		return dataWatcher.getWatchableObjectByte(20) == 1 ? true : false;
	}

	public boolean canTeleport() {
		return !isCasting() && teleportCooldown == 0;
	}

	public float getAttackAnimationTime(float partialRenderTicks) {
		return (prevAttackAnimationTime + (attackAnimationTime - prevAttackAnimationTime) * partialRenderTicks) / MAX_ATTACK_ANIMATION_TIME;
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("Teleport", teleportCooldown);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		teleportCooldown = tagCompound.getInteger("Teleport");
	}
}
