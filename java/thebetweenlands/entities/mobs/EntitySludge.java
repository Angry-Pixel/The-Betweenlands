package thebetweenlands.entities.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.ControlledAnimation;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class EntitySludge extends EntityMob implements IEntityBL {
	private int sludgeJumpDelay;
	public float squishAmount;
	public float squishFactor;
	public float prevSquishFactor;

	public ControlledAnimation scale = new ControlledAnimation(5);

	public EntitySludge(World world) {
		super(world);

		this.tasks.addTask(0, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1D, false));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

		isImmuneToFire = true;
		setSize(1.0F, 1.5F);
		sludgeJumpDelay = rand.nextInt(20) + 10;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	/*
	 * @Override protected String getLivingSound() { return ""; }
	 *
	 * @Override protected String getHurtSound() { return ""; }
	 *
	 * @Override protected String getDeathSound() { return ""; }
	 */

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (rand.nextInt(5) == 0)
			entityDropItem(ItemGeneric.createStack(EnumItemGeneric.SLUDGE_BALL, 3 + looting), 0.0F);
	}

	@Override
	public void onUpdate() {
		squishFactor += (squishAmount - squishFactor) * 0.5F;
		prevSquishFactor = squishFactor;
		boolean flag = onGround;
		super.onUpdate();
		if (!this.worldObj.isRemote) {
			if (getIsPlayerNearby(7, 3, 7, 7) || getAttackTarget() != null || this.worldObj.rand.nextInt(1900) == 0) {
				if (!getActive()) {
					setActive(true);
					motionY += 0.6;
				}
			}

			if (getActive()) {
				if (onGround && !flag) {
					squishAmount = -0.5F;
					playSound("mob.slime.big", 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
				} else if (!onGround && flag) {
					squishAmount = 1.0F;
				}

				alterSquishAmount();

				int bx = MathHelper.floor_double(this.posX);
				int by = MathHelper.floor_double(this.posY);
				int bz = MathHelper.floor_double(this.posZ);
				if (this.worldObj.getBlock(bx, by, bz) == Blocks.air &&
						BLBlockRegistry.sludge.canPlaceBlockAt(this.worldObj, bx, by, bz)) {
					BLBlockRegistry.sludge.generateBlockTemporary(this.worldObj, bx, by, bz);
				}

				if(getAttackTarget() == null && this.worldObj.rand.nextInt(350) == 0) {
					this.setActive(false);
				}
			} else {
				motionX = 0;
				motionZ = 0;
				motionY = 0;
			}
		}

		//Update animation
		if(this.worldObj.isRemote) {
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
	protected void updateEntityActionState() {
		super.updateEntityActionState();

		if(this.worldObj.isRemote) return;

		if(getAttackTarget() == null) {
			EntityPlayer newTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this, 8.0D);
			if(newTarget != null) {
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
			moveForward = 1;
			playSound("mob.slime.big", 1F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
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
			if (player.boundingBox.maxY >= boundingBox.minY && player.boundingBox.minY <= boundingBox.maxY) {
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
		dataWatcher.addObject(20, (byte) (this.worldObj.rand.nextInt(5) == 0 ? 1 : 0));
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	public void setActive(boolean active) {
		dataWatcher.updateObject(20, (byte) (active ? 1 : 0));
	}

	public boolean getActive() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	public boolean getIsPlayerNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distanceX, distanceY, distanceZ));
		ArrayList<EntityPlayer> listEntityPlayers = new ArrayList<EntityPlayer>();
		for (Entity entityNeighbor : list)
		{
			if (entityNeighbor instanceof EntityPlayer && getDistanceToEntity(entityNeighbor) <= radius && (!((EntityPlayer)entityNeighbor).capabilities.disableDamage && !worldObj.isRemote && getEntitySenses().canSee(entityNeighbor)))
				return true;
		}
		return false;
	}
}
