package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.List;

public class EntitySludge extends EntityMob implements IEntityBL {
	public static final DataParameter<Boolean> IS_ACTIVE = EntityDataManager.createKey(EntitySludge.class, DataSerializers.BOOLEAN);

	private int sludgeJumpDelay;
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;

	public ControlledAnimation scale = new ControlledAnimation(5);

	public EntitySludge(World world) {
		super(world);

		this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false, true));

		isImmuneToFire = true;
		setSize(1.1F, 1.2F);
		sludgeJumpDelay = rand.nextInt(20) + 10;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (rand.nextInt(5) == 0) {
			entityDropItem(new ItemStack(ItemMisc.EnumItemMisc.SLUDGE_BALL.getItem(), 3 + looting), 0.0F);
		}
	}

	@Override
	public void onUpdate() {
		squishFactor += (squishAmount - squishFactor) * 0.5F;
		prevSquishFactor = squishFactor;
		boolean prevOnGround = onGround;
		super.onUpdate();
		if (!this.worldObj.isRemote) {
			if (getIsPlayerNearby(7, 3, 7, 7) || getAttackTarget() != null || this.worldObj.rand.nextInt(1900) == 0) {
				if (!getActive()) {
					setActive(true);
					motionY += 0.6;
				}
			}

			if (getActive()) {
				if (onGround && !prevOnGround) {
					squishAmount = -0.5F;
					playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
				} else if (!onGround && prevOnGround) {
					squishAmount = 1.0F;
				}

				alterSquishAmount();

				BlockPos position = this.getPosition();
				if (this.worldObj.getBlockState(position).getBlock() == Blocks.AIR &&
						BlockRegistry.SLUDGE.canPlaceBlockAt(this.worldObj, position)) {
					BlockRegistry.SLUDGE.generateBlockTemporary(this.worldObj, position);
				}

				if (getAttackTarget() == null && this.worldObj.rand.nextInt(350) == 0) {
					this.setActive(false);
				}
			} else {
				motionX = 0;
				motionZ = 0;
				motionY = 0;
			}
		}

		//Update animation
		if (this.worldObj.isRemote) {
			if (getActive()) {
				scale.increaseTimer();
			} else {
				scale.decreaseTimer();
			}
		}
	}

	protected void alterSquishAmount() {
		squishAmount *= 0.8F;
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || !this.getActive();
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (this.worldObj.isRemote) return;

		if (getAttackTarget() == null) {
			EntityPlayer newTarget = this.worldObj.getNearestAttackablePlayer(this, 8.0, 8.0);
			if (newTarget != null) {
				this.setAttackTarget(newTarget);
			}
		}

		if ((onGround || this.isInWater()) && sludgeJumpDelay-- <= 0) {
			sludgeJumpDelay = getJumpDelay();
			if (getAttackTarget() != null) {
				sludgeJumpDelay /= 3.0F;
			}
			isJumping = true;
			moveStrafing = getAttackTarget() == null ? (1.0F - rand.nextFloat() * 2.0F) : ((1.0F - rand.nextFloat() * 2.0F) / 10.0F);
			if (this.getAttackTarget() != null) {
				this.moveRelative(0, 0.65F, 1);
			} else {
				moveForward = 1;
			}
			playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		} else {
			isJumping = false;
			if (onGround) {
				moveStrafing = moveForward = 0.0F;
			}
		}

		if (this.rand.nextFloat() < 0.8F && this.isInWater()) {
			this.isJumping = true;
		}
	}

	protected int getJumpDelay() {
		return rand.nextInt(20) + 10;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!player.capabilities.isCreativeMode && !worldObj.isRemote && getEntitySenses().canSee(player) && getActive()) {
			AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
			if (playerBoundingBox.maxY >= this.getEntityBoundingBox().minY && playerBoundingBox.minY <= this.getEntityBoundingBox().maxY) {
				player.attackEntityFrom(DamageSource.causeMobDamage(this), 1F);
				player.motionX = 0.0D;
				player.motionY = 0.0D;
				player.motionZ = 0.0D;
			}
		}
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_ACTIVE, this.worldObj.rand.nextInt(5) == 0);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	public void setActive(boolean active) {
		this.dataManager.set(IS_ACTIVE, active);
	}

	public boolean getActive() {
		return dataManager.get(IS_ACTIVE);
	}

	public boolean getIsPlayerNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(distanceX, distanceY, distanceZ));
		for (Entity entityNeighbor : entities) {
			if (entityNeighbor instanceof EntityPlayer && getDistanceToEntity(entityNeighbor) <= radius && (!((EntityPlayer) entityNeighbor).capabilities.disableDamage && !worldObj.isRemote && getEntitySenses().canSee(entityNeighbor)))
				return true;
		}
		return false;
	}
}
