package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityFrog extends EntityCreature implements IEntityBL {
	private static final DataParameter<Byte> DW_SWIM_STROKE = EntityDataManager.createKey(EntityFrog.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> SKIN = EntityDataManager.createKey(EntityFrog.class, DataSerializers.VARINT);
	public int jumpAnimationTicks;
	public int prevJumpAnimationTicks;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;

	public EntityFrog(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.WATER, 1.0F);
		setSize(0.7F, 0.5F);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIPanic(this, 0.1D));
		this.tasks.addTask(1, new EntityAIWander(this, 0.0D));
		this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(3, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(DW_SWIM_STROKE, (byte) 0);
		dataManager.register(SKIN, rand.nextInt(5));
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
	}

	@Override
	public void onUpdate() {
		this.prevJumpAnimationTicks = this.jumpAnimationTicks;
		super.onUpdate();
		if (this.onGround || (this.strokeTicks == 0 && this.isInWater())) {
			this.ticksOnGround++;
			if (this.jumpAnimationTicks > 0)
				this.jumpAnimationTicks = 0;
		} else {
			this.ticksOnGround = 0;
			this.jumpAnimationTicks++;
		}
		if (this.strokeTicks > 0)
			this.strokeTicks--;
		if (!this.worldObj.isRemote) {
			if (this.strokeTicks > 0) {
				this.strokeTicks--;
				this.dataManager.set(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.dataManager.set(DW_SWIM_STROKE, (byte) 0);
			}
		} else {
			if (this.dataManager.get(DW_SWIM_STROKE) == 1) {
				if (this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}
		}
		if (!this.worldObj.isRemote) {
			this.setAir(20);

			Path path = getNavigator().getPath();
			if (path != null && !path.isFinished() && (onGround || this.isInWater()) && !this.isMovementBlocked()) {
				int index = path.getCurrentPathIndex();
				if (index < path.getCurrentPathLength()) {
					PathPoint nextHopSpot = path.getPathPointFromIndex(index);
					float x = (float) (nextHopSpot.xCoord - posX);
					float z = (float) (nextHopSpot.zCoord - posZ);
					float angle = (float) (Math.atan2(z, x));
					float distance = (float) Math.sqrt(x * x + z * z);
					if (distance > 1) {
						if (!this.isInWater()) {
							if (this.ticksOnGround > 5) {
								this.motionY += 0.5;
								this.motionX += 0.3 * MathHelper.cos(angle);
								this.motionZ += 0.3 * MathHelper.sin(angle);
								this.velocityChanged = true;
							}
						} else {
							if (this.strokeTicks == 0) {
								this.motionX += 0.3 * MathHelper.cos(angle);
								this.motionZ += 0.3 * MathHelper.sin(angle);
								this.velocityChanged = true;
								this.strokeTicks = 40;
								this.worldObj.setEntityState(this, (byte) 8);
							} else if (this.isCollidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						}
					}
				}
			}

			if (!this.worldObj.isRemote) {
				if (this.motionY < 0.0F && this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.4D), MathHelper.floor_double(this.posZ))).getMaterial().isLiquid()) {
					this.motionY *= 0.1F;
					this.velocityChanged = true;
				}
				if (this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ))).getMaterial().isLiquid()) {
					this.motionY += 0.04F;
					this.velocityChanged = true;
				}
			}
		}

		/* TODO fix particles
        if (worldObj.isRemote && getSkin() == 4 && worldObj.getWorldTime() % 10 == 0) {
            BLParticle.DIRT_DECAY.spawn(worldObj, posX, posY + 0.5D, posZ, 0, 0, 0, 0);
        }*/
	}

	@Override
	public void moveEntityWithHeading(float strafing, float forward) {
		super.moveEntityWithHeading(0, 0);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		byte duration = 0;
		if (getSkin() == 4) {
			if (!worldObj.isRemote && !player.capabilities.isCreativeMode && player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && player.getEntityBoundingBox().maxX >= getEntityBoundingBox().minX && player.getEntityBoundingBox().minX <= getEntityBoundingBox().maxX && player.getEntityBoundingBox().maxZ >= getEntityBoundingBox().minZ && player.getEntityBoundingBox().minZ <= getEntityBoundingBox().maxZ) {
				if (worldObj.getDifficulty() == EnumDifficulty.NORMAL)
					duration = 5;
				else if (worldObj.getDifficulty() == EnumDifficulty.HARD)
					duration = 10;
				if (duration > 0)
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), duration * 20, 0));
			}
		}
	}

	public int getSkin() {
		return dataManager.get(SKIN);
	}

	public void setSkin(int skinType) {
		dataManager.set(SKIN, skinType);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("skin", getSkin());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("skin"))
			setSkin(nbt.getInteger("skin"));
		else
			setSkin(rand.nextInt(5));
	}

	//TODO add frogLiving sound
	@Override
	protected SoundEvent getAmbientSound() {
		return super.getAmbientSound();
	}

	//TODO add frogHurt sound
	@Override
	protected SoundEvent getHurtSound() {
		return super.getHurtSound();
	}

	//TODO add frogDeath sound
	@Override
	protected SoundEvent getDeathSound() {
		return super.getDeathSound();
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (isBurning())
			entityDropItem(new ItemStack(ItemRegistry.FROG_LEGS_COOKED, 1, 0), 0.0F);
		else {
			entityDropItem(new ItemStack(ItemRegistry.FROG_LEGS_RAW, 1, 0), 0.0F);
			if (getSkin() == 4)
				entityDropItem(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.POISON_GLAND), 0.0F);
		}
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 8) {
			this.strokeTicks = 0;
		}
	}
}
