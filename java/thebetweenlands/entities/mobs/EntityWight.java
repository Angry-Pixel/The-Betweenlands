package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;

public class EntityWight extends EntityMob {
	public int courseChangeCooldown = 0;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	public Entity targetedEntity = null;
	private boolean climbs = false;

	/** Cooldown time between target loss and new target aquirement. */
	private int aggroCooldown = 0;
	public int prevAttackCounter = 0;
	public int attackCounter = 0;

	public EntityWight(World world) {
		super(world);
		setSize(1.5F, 3F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	protected String getLivingSound() {
		int randomSound = worldObj.rand.nextInt(4) + 1;
		return "thebetweenlands:wightMoan" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:wightHurt1";
		else
			return "thebetweenlands:wightHurt2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:wightDeath";
	}

	protected void attackEntity(Entity par1Entity, int par2) {
		if (this.onGround) {
			double var4 = par1Entity.posX - this.posX;
			double var6 = par1Entity.posZ - this.posZ;
			float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
			this.motionX = var4 / (double) var8 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
			this.motionZ = var6 / (double) var8 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
			this.motionY = 0.4000000059604645D;
		}

		this.attackEntityAsMob(par1Entity);

		/*
		 * System.out.println(var4);
		 * 
		 * if (var4) {
		 * par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw *
		 * (float)Math.PI / 180.0F) * (float)1 * 0.5F), 0.1D,
		 * (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) *
		 * (float)1 * 0.5F)); this.motionX *= 0.6D; this.motionZ *= 0.6D; }
		 */
	}

	@Override
	protected void updateEntityActionState() {
		try {
			EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, -1);
			if (closestPlayer.getDistance(posX, posY, posZ) < 10 && !closestPlayer.isSneaking() && !closestPlayer.capabilities.isCreativeMode) {
				targetedEntity = closestPlayer;
			}

			if (this.onGround && targetedEntity != null) {
				if (targetedEntity.getDistance(posX, posY, posZ) < 2) {
					attackEntity(targetedEntity, 1);
				}
			}

			if (targetedEntity != null) {
				this.waypointX = targetedEntity.posX;
				this.waypointY = targetedEntity.posY;
				this.waypointZ = targetedEntity.posZ;
			}

			this.despawnEntity();
			this.prevAttackCounter = this.attackCounter;
			double var1 = this.waypointX - this.posX;
			double var3 = this.waypointY - this.posY;
			double var5 = this.waypointZ - this.posZ;
			double var7 = (double) MathHelper.sqrt_double(var1 * var1 + var3 * var3 + var5 * var5);

			if ((var7 < 1.0D || var7 > 60.0D) && targetedEntity == null) {
				this.waypointX = this.posX + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				this.waypointY = this.posY + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				this.waypointZ = this.posZ + (double) ((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
			}

			if (this.targetedEntity != null) {
				double var11 = this.targetedEntity.posX - this.posX;
				double var13 = this.targetedEntity.boundingBox.minY + (double) (this.targetedEntity.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
				double var15 = this.targetedEntity.posZ - this.posZ;
				this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(var11, var15)) * 180.0F / (float) Math.PI;
			}

			if (this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.rand.nextInt(5) + 2;

				// if (this.isCourseTraversable(this.waypointX, this.waypointY -
				// 0.5, this.waypointZ, var7))
				// {
				if (targetedEntity != null) {
					this.motionX += var1 / var7 * 0.3D;
					this.motionZ += var5 / var7 * 0.3D;
				} else {
					this.motionX += var1 / var7 * 0.18D;
					this.motionZ += var5 / var7 * 0.18D;
				}
				/*
				 * } else if((targetedEntity != null &&
				 * this.worldObj.getBlockId((int)this.posX - 1, (int)this.posY,
				 * (int)this.posZ) != 0 ||
				 * this.worldObj.getBlockId((int)this.posX + 1, (int)this.posY,
				 * (int)this.posZ) != 0 ||
				 * this.worldObj.getBlockId((int)this.posX, (int)this.posY,
				 * (int)this.posZ + 1) != 0 ||
				 * this.worldObj.getBlockId((int)this.posX, (int)this.posY,
				 * (int)this.posZ - 1) != 0) && !this.isJumping &&
				 * this.onGround) { this.jump(); }
				 */
			}

			if (this.targetedEntity != null && this.targetedEntity.isDead) {
				this.targetedEntity = null;
			}

			/*
			 * if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
			 * this.targetedEntity =
			 * this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0D);
			 * 
			 * if (this.targetedEntity != null) { this.aggroCooldown = 20; } }
			 * 
			 * double var9 = 64.0D;
			 * 
			 * if (this.targetedEntity != null) { double var11 =
			 * this.targetedEntity.posX - this.posX; double var13 =
			 * this.targetedEntity.boundingBox.minY +
			 * (double)(this.targetedEntity.height / 2.0F) - (this.posY +
			 * (double)(this.height / 2.0F)); double var15 =
			 * this.targetedEntity.posZ - this.posZ; this.renderYawOffset =
			 * this.rotationYaw = -((float)Math.atan2(var11, var15)) * 180.0F /
			 * (float)Math.PI;
			 * 
			 * if (this.canEntityBeSeen(this.targetedEntity)) {
			 * 
			 * ++this.attackCounter;
			 * 
			 * if (this.attackCounter == 20) { this.attackCounter = -40; } }
			 * else if (this.attackCounter > 0) { --this.attackCounter; } } else
			 * { this.renderYawOffset = this.rotationYaw =
			 * -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F /
			 * (float)Math.PI;
			 * 
			 * if (this.attackCounter > 0) { --this.attackCounter; } }
			 */

			boolean var4 = this.isInWater();
			boolean var31 = this.handleLavaMovement();

			if (var4 || var31 || this.isMovementBlocked()) {
				this.isJumping = this.rand.nextFloat() < 0.8F;
			}

			/*
			 * if(this.targetedEntity == null) { this.rotationYawHead = 180; }
			 * else { this.rotationYawHead = 0; }
			 */

		} catch (Exception ex) {

		}

		super.updateEntityActionState();
	}

	private boolean isCourseTraversable(double par1, double par3, double par5, double par7) {
		double var9 = (this.waypointX - this.posX) / par7;
		double var11 = (this.waypointY - 1 - this.posY - 0.5) / par7;
		double var13 = (this.waypointZ - this.posZ) / par7;
		AxisAlignedBB var15 = this.boundingBox.copy();

		for (int var16 = 1; (double) var16 < par7; ++var16) {
			var15.offset(var9, var11, var13);

			if (this.worldObj.getCollidingBoundingBoxes(this, var15).size() > 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.WIGHT_HEART), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int((float) damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}
}
