package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;

public class EntityFrog extends EntityCreature {
	private int ticksOnGround = 0;
	public int jumpticks;
	public int prevJumpticks;
	private boolean prevOnGround;
	public EntityFrog(World world) {
		super(world);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(5, new EntityAIWander(this, 0.0D));
		tasks.addTask(7, new EntityAILookIdle(this));
		setSize(0.5F, 0.5F);
		stepHeight = 1.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(18, (byte) 0);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onUpdate() {
		prevOnGround = onGround;
		prevJumpticks = jumpticks;
		super.onUpdate();
			if(this.onGround) {
				this.ticksOnGround++;
				if(jumpticks > 0)
					jumpticks = 0;
			} else {
				this.ticksOnGround = 0;
				jumpticks ++;
			}
			//TODO: RIP TPS, 2016 - 2016
			//Keeps trying to pathfind causing huge stress on TPS
			if (!worldObj.isRemote) {
				PathEntity path = getNavigator().getPath();
				if (path != null && !path.isFinished() && onGround && !this.isMovementBlocked()) {
					if (this.ticksOnGround > 20) {
						int index = path.getCurrentPathIndex();
						if (index < path.getCurrentPathLength()) {
							PathPoint nextHopSpot = path.getPathPointFromIndex(index);
							float x = (float) (nextHopSpot.xCoord - posX);
							float z = (float) (nextHopSpot.zCoord - posZ);
							float angle = (float) (Math.atan2(z, x));
							float distance = (float) Math.sqrt(x * x + z * z);
							if (distance > 1) {
								motionY += 0.6;
								motionX += 0.3 * MathHelper.cos(angle);
								motionZ += 0.3 * MathHelper.sin(angle);
							}/* else {
								getNavigator().clearPathEntity();
							}*/
						}
					}
				}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		// TODO Texture types
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		// TODO Texture types
	}
/*
	protected String getJumpingSound() {
		return null;

	}

	@Override
	protected String getLivingSound() {
		return null;

	}

	@Override
	protected String getHurtSound() {
		return null;

	}

	@Override
	protected String getDeathSound() {
		return null;
	}
*/
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (isBurning())
			entityDropItem(ItemGeneric.createStack(BLItemRegistry.frogLegsCooked, 1, 0), 0.0F);
		else
			entityDropItem(ItemGeneric.createStack(BLItemRegistry.frogLegsRaw, 1, 0), 0.0F);
	}
}
