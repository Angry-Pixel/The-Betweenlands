package thebetweenlands.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import thebetweenlands.items.ItemBLArrow;
import thebetweenlands.utils.PotionHelper;

import java.util.List;

public class EntityBLArrow extends EntityArrow implements IProjectile {
	private int landedX = -1;
	private int landedY = -1;
	private int landedZ = -1;
	private Block blockHit;
	private int inData;
	private boolean inGround;
	public int damageIndex = -1;
	private Item arrows;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 2.0D;
	private int knockbackStrength;
	public boolean isOctineArrow = false;
	public boolean isPoisonedAnglerToothArrow = false;
	public boolean isBasiliskArrow = false;

	public EntityBLArrow(World world) {
		super(world);
		renderDistanceWeight = 10.0D;
		setSize(0.5F, 0.5F);
	}

	public EntityBLArrow(World world, double x, double y, double z) {
		super(world);
		renderDistanceWeight = 10.0D;
		setSize(0.5F, 0.5F);
		setPosition(x, y, z);
		yOffset = 0.0F;

	}

	public EntityBLArrow(World world, EntityLivingBase entityShooter,
			EntityLivingBase entityTarget, float direction, float speed) {
		super(world);
		renderDistanceWeight = 10.0D;
		shootingEntity = entityShooter;

		if (entityShooter instanceof EntityPlayer) {
			canBePickedUp = 1;
		}

		posY = entityShooter.posY + (double) entityShooter.getEyeHeight() - 0.10000000149011612D;
		double d0 = entityTarget.posX - entityShooter.posX;
		double d1 = entityTarget.boundingBox.minY + (double) (entityTarget.height / 3.0F) - posY;
		double d2 = entityTarget.posZ - entityShooter.posZ;
		double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);

		if (d3 >= 1.0E-7D) {
			float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
			double d4 = d0 / d3;
			double d5 = d2 / d3;
			setLocationAndAngles(entityShooter.posX + d4, posY, entityShooter.posZ + d5, f2, f3);
			yOffset = 0.0F;
			float f4 = (float) d3 * 0.2F;
			setThrowableHeading(d0, d1 + (double) f4, d2, direction, speed);
		}
	}

	public EntityBLArrow(World world, EntityLivingBase entityShooter, float speed) {
		super(world);
		renderDistanceWeight = 10.0D;
		shootingEntity = entityShooter;

		if (entityShooter instanceof EntityPlayer) {
			canBePickedUp = 1;
		}

		setSize(0.5F, 0.5F);
		setLocationAndAngles(entityShooter.posX, entityShooter.posY + (double) entityShooter.getEyeHeight(), entityShooter.posZ, entityShooter.rotationYaw, entityShooter.rotationPitch);
		posX -= (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		posY -= 0.10000000149011612D;
		posZ -= (double) (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		setPosition(posX, posY, posZ);
		yOffset = 0.0F;
		motionX = (double) (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
		motionZ = (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
		motionY = (double) (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));
		setThrowableHeading(motionX, motionY, motionZ, speed * 1.5F, 1.0F);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(16, Byte.valueOf((byte) 0));
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float par7,
			float par8) {
		float f2 = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= (double) f2;
		y /= (double) f2;
		z /= (double) f2;
		x += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) par8;
		y += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) par8;
		z += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) par8;
		x *= (double) par7;
		y *= (double) par7;
		z *= (double) par7;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f3 = MathHelper.sqrt_double(x * x + z * z);
		prevRotationYaw = rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float) (Math.atan2(y, (double) f3) * 180.0D / Math.PI);
		ticksInGround = 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float par7, float par8, int par9) {
		setPosition(x, y, z);
		setRotation(par7, par8);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z) {
		motionX = x;
		motionY = y;
		motionZ = z;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(x * x + z * z);
			prevRotationYaw = rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float) (Math.atan2(y, (double) f) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
			ticksInGround = 0;
		}
	}

	@Override
	public void onUpdate() {
		super.onEntityUpdate();

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float) (Math.atan2(motionY, (double) f) * 180.0D / Math.PI);
		}

		Block block = worldObj.getBlock(landedX, landedY, landedZ);

		if (block.getMaterial() != Material.air) {
			block.setBlockBoundsBasedOnState(worldObj, landedX, landedY, landedZ);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(worldObj, landedX, landedY, landedZ);

			if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ))) {
				inGround = true;
			}
		}

		if (arrowShake > 0) {
			--arrowShake;
		}

		if (inGround) {
			int j = worldObj.getBlockMetadata(landedX, landedY, landedZ);

			if (block == blockHit && j == inData) {
				++ticksInGround;

				if (ticksInGround == 1200) {
					setDead();
				}
				else if (isOctineArrow && ticksInGround == 1) {
					worldObj.setBlock(this.landedX, this.landedY+1, this.landedZ, Blocks.fire);
				}
			} else {
				inGround = false;
				motionX *= (double) (rand.nextFloat() * 0.2F);
				motionY *= (double) (rand.nextFloat() * 0.2F);
				motionZ *= (double) (rand.nextFloat() * 0.2F);
				ticksInGround = 0;
				ticksInAir = 0;
			}
		} else {
			++ticksInAir;
			Vec3 vec31 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition movingobjectposition = worldObj.func_147447_a(vec31, vec3, false, true, false);
			vec31 = Vec3.createVectorHelper(posX, posY, posZ);
			vec3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (movingobjectposition != null) {
				vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
			}

			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;

			for (i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5)) {
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

					if (movingobjectposition1 != null) {
						double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

				if (entityplayer.capabilities.disableDamage || shootingEntity instanceof EntityPlayer && !((EntityPlayer) shootingEntity).canAttackPlayer(entityplayer)) {
					movingobjectposition = null;
				}
			}

			float f2;
			float f4;

			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					int k = MathHelper.ceiling_double_int((double) f2 * damage);

					if (getIsCritical()) {
						k += rand.nextInt(k / 2 + 2);
					}

					DamageSource damagesource = null;

					if (shootingEntity == null) {
						damagesource = ItemBLArrow.causeArrowDamage(this, this);
					} else {
						damagesource = ItemBLArrow.causeArrowDamage(this, shootingEntity);
					}

					boolean enderman = !(movingobjectposition.entityHit instanceof EntityEnderman);
					if (isBurning() && enderman) {
						movingobjectposition.entityHit.setFire(5);
					}

					if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float) k)) {
						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;

							if (!worldObj.isRemote) {
								entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
							}

							if (knockbackStrength > 0) {
								f4 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

								if (f4 > 0.0F) {
									movingobjectposition.entityHit.addVelocity(motionX * (double) knockbackStrength * 0.6000000238418579D / (double) f4, 0.1D, motionZ * (double) knockbackStrength * 0.6000000238418579D / (double) f4);
								}
							}

							if (shootingEntity != null && shootingEntity instanceof EntityLivingBase) {
								EnchantmentHelper.func_151384_a(entitylivingbase, shootingEntity);
								EnchantmentHelper.func_151385_b((EntityLivingBase) shootingEntity, entitylivingbase);
							}

							if (shootingEntity != null && movingobjectposition.entityHit != shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
							//special arrows

							//TODO set better poison value
							if(isPoisonedAnglerToothArrow && enderman)
								entitylivingbase.addPotionEffect(new PotionEffect(Potion.poison.getId(), 200, 2));

							//TODO set better burning value
							if(isOctineArrow && enderman){
								if(isBurning())
									movingobjectposition.entityHit.setFire(9);
								else
									movingobjectposition.entityHit.setFire(5);
							}

							if(isBasiliskArrow && enderman){
								entitylivingbase.addPotionEffect(new PotionEffect(PotionHelper.petrify.getId(), 100));
							}
						}

						playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));

						if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
							setDead();
						}
					} else {
						motionX *= -0.10000000149011612D;
						motionY *= -0.10000000149011612D;
						motionZ *= -0.10000000149011612D;
						rotationYaw += 180.0F;
						prevRotationYaw += 180.0F;
						ticksInAir = 0;
					}
				} else {
					landedX = movingobjectposition.blockX;
					landedY = movingobjectposition.blockY;
					landedZ = movingobjectposition.blockZ;
					blockHit = block;
					inData = worldObj.getBlockMetadata(landedX, landedY, landedZ);
					motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - posX));
					motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - posY));
					motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - posZ));
					f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					posX -= motionX / (double) f2 * 0.05000000074505806D;
					posY -= motionY / (double) f2 * 0.05000000074505806D;
					posZ -= motionZ / (double) f2 * 0.05000000074505806D;
					playSound("random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
					inGround = true;
					arrowShake = 7;
					setIsCritical(false);

					if (blockHit.getMaterial() != Material.air) {
						blockHit.onEntityCollidedWithBlock(worldObj, landedX, landedY, landedZ, this);
					}
				}
			}

			if (getIsCritical()) {
				for (i = 0; i < 4; ++i) {
					worldObj.spawnParticle("crit", posX + motionX * (double) i / 4.0D, posY + motionY * (double) i / 4.0D, posZ + motionZ * (double) i / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

			for (rotationPitch = (float) (Math.atan2(motionY, (double) f2) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
				;
			}

			while (rotationPitch - prevRotationPitch >= 180.0F) {
				prevRotationPitch += 360.0F;
			}

			while (rotationYaw - prevRotationYaw < -180.0F) {
				prevRotationYaw -= 360.0F;
			}

			while (rotationYaw - prevRotationYaw >= 180.0F) {
				prevRotationYaw += 360.0F;
			}

			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f1 = 0.05F;

			if (isInWater()) {
				for (int l = 0; l < 4; ++l) {
					f4 = 0.25F;
					worldObj.spawnParticle("bubble", posX - motionX * (double) f4, posY - motionY * (double) f4, posZ - motionZ * (double) f4, motionX, motionY, motionZ);
				}

				f3 = 0.8F;
			}

			if (isWet()) {
				extinguish();
			}

			motionX *= (double) f3;
			motionY *= (double) f3;
			motionZ *= (double) f3;
			motionY -= (double) f1;
			setPosition(posX, posY, posZ);
			func_145775_I();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setShort("xTile", (short) landedX);
		par1NBTTagCompound.setShort("yTile", (short) landedY);
		par1NBTTagCompound.setShort("zTile", (short) landedZ);
		par1NBTTagCompound.setShort("life", (short) ticksInGround);
		par1NBTTagCompound.setByte("inTile", (byte) Block.getIdFromBlock(blockHit));
		par1NBTTagCompound.setByte("inData", (byte) inData);
		par1NBTTagCompound.setByte("shake", (byte) arrowShake);
		par1NBTTagCompound.setByte("inGround", (byte) (inGround ? 1 : 0));
		par1NBTTagCompound.setByte("pickup", (byte) canBePickedUp);
		par1NBTTagCompound.setDouble("damage", damage);
		par1NBTTagCompound.setBoolean("isOctineArrow", isOctineArrow);
		par1NBTTagCompound.setBoolean("isPoisonedAnglerToothArrow", isPoisonedAnglerToothArrow);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		landedX = par1NBTTagCompound.getShort("xTile");
		landedY = par1NBTTagCompound.getShort("yTile");
		landedZ = par1NBTTagCompound.getShort("zTile");
		ticksInGround = par1NBTTagCompound.getShort("life");
		blockHit = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
		inData = par1NBTTagCompound.getByte("inData") & 255;
		arrowShake = par1NBTTagCompound.getByte("shake") & 255;
		inGround = par1NBTTagCompound.getByte("inGround") == 1;
		isOctineArrow = par1NBTTagCompound.getBoolean("isOctineArrow");
		isPoisonedAnglerToothArrow = par1NBTTagCompound.getBoolean("isPoisonedAnglerToothArrow");

		if (par1NBTTagCompound.hasKey("damage", 99)) {
			damage = par1NBTTagCompound.getDouble("damage");
		}

		if (par1NBTTagCompound.hasKey("pickup", 99)) {
			canBePickedUp = par1NBTTagCompound.getByte("pickup");
		} else if (par1NBTTagCompound.hasKey("player", 99)) {
			canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		if (!worldObj.isRemote && inGround && arrowShake <= 0) {
			boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;

			if (canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(arrows, 1))) {
				flag = false;
			}

			if (flag) {
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				par1EntityPlayer.onItemPickup(this, 1);
				setDead();
			}
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public void setDamage(double damageAmount) {
		damage = damageAmount;
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public void setKnockbackStrength(int knockBack) {
		knockbackStrength = knockBack;
	}

	@Override
	public boolean canAttackWithItem() {
		return false;
	}

	@Override
	public void setIsCritical(boolean isCritical) {
		byte b0 = dataWatcher.getWatchableObjectByte(16);

		if (isCritical) {
			dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 | 1)));
		} else {
			dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	@Override
	public boolean getIsCritical() {
		byte b0 = dataWatcher.getWatchableObjectByte(16);
		return (b0 & 1) != 0;
	}

	public int getDamageIndex() {
		return damageIndex;
	}

	public String getType(){
		if(isPoisonedAnglerToothArrow)
			return "poisonedAnglerToothArrow";
		else if(isOctineArrow)
			return "octineArrow";
		else
			return "anglerToothArrow";
	}
}
