package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityFishingSpear extends Entity implements IProjectile, IThrowableEntity {
	@SuppressWarnings("unchecked")
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
				@Override
				public boolean apply(@Nullable Entity entity) {
					return entity.canBeCollidedWith();
				}
			});

	private static final DataParameter<Byte> CRITICAL = EntityDataManager.<Byte>createKey(EntityFishingSpear.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityFishingSpear.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> ITEMSTACK_DAMAGE = EntityDataManager.createKey(EntityFishingSpear.class, DataSerializers.VARINT);
	protected int xTile;
	protected int yTile;
	protected int zTile;
	protected Block inTile;
	protected int inData;
	protected boolean inGround;
	protected int timeInGround;
	public int arrowShake;
	public Entity shootingEntity;
	protected int ticksInGround;
	protected int ticksInAir;
	protected double damage = 0;

	private static final byte EVENT_DEAD = 111;

	public EntityFishingSpear(World world) {
		super(world);
		xTile = -1;
		yTile = -1;
		zTile = -1;
		setSize(0.25F, 0.25F);
	}

	public EntityFishingSpear(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y, z);
	}

	public EntityFishingSpear(World world, EntityLivingBase shooter) {
		this(world, shooter.posX, shooter.posY + (double) shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
		shootingEntity = shooter;
	}

	@Override
	public Entity getThrower() {
		return shootingEntity;
	}

	@Override
	public void setThrower(Entity entity) {
		shootingEntity = entity;
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = getEntityBoundingBox().getAverageEdgeLength() * 10.0D;

		if (Double.isNaN(d0))
			d0 = 1.0D;

		d0 = d0 * 64.0D * getRenderDistanceWeight();
		return distance < d0 * d0;
	}

	protected void entityInit() {
		dataManager.register(CRITICAL, (byte) 0);
		dataManager.register(ITEMSTACK_DAMAGE, 0);
		dataManager.register(TYPE, (byte) 0);
	}
	
	public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float f1 = -MathHelper.sin(pitch * 0.017453292F);
		float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		shoot((double) f, (double) f1, (double) f2, velocity, inaccuracy);
		motionX += shooter.motionX;
		motionZ += shooter.motionZ;
		if (!shooter.onGround)
			motionY += shooter.motionY;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x = x / (double) f;
		y = y / (double) f;
		z = z / (double) f;
		x = x + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		y = y + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		z = z + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
		x = x * (double) velocity;
		y = y * (double) velocity;
		z = z * (double) velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f1 = MathHelper.sqrt(x * x + z * z);
		rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
		rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
		ticksInGround = 0;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,int posRotationIncrements, boolean teleport) {
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		motionX = x;
		motionY = y;
		motionZ = z;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			rotationPitch = (float) (MathHelper.atan2(y, (double) f) * (180D / Math.PI));
			rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
			ticksInGround = 0;
		}
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if (!inGround)
			super.setPosition(x, y, z);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(!world.isRemote) {
			if(getItemStackDamage() >= 64) {  // needs to match or exceed Item maxDamage
				getEntityWorld().setEntityState(this, EVENT_DEAD);
				setDead();
			}
		}

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
			rotationPitch = (float) (MathHelper.atan2(motionY, (double) f) * (180D / Math.PI));
			prevRotationYaw = rotationYaw;
			prevRotationPitch = rotationPitch;
		}

		BlockPos blockpos = new BlockPos(xTile, yTile, zTile);
		IBlockState iblockstate = world.getBlockState(blockpos);
		Block block = iblockstate.getBlock();

		if (iblockstate.getMaterial() != Material.AIR) {
			AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(world, blockpos);
			if (axisalignedbb != Block.NULL_AABB&& axisalignedbb.offset(blockpos).contains(new Vec3d(posX, posY, posZ)))
				inGround = true;
		}

		if (arrowShake > 0)
			--arrowShake;

		if (inGround) {
			int j = block.getMetaFromState(iblockstate);

			if ((block != inTile || j != inData) && !world.collidesWithAnyBlock(getEntityBoundingBox().grow(0.05D))) {
				inGround = false;
				motionX *= (double) (rand.nextFloat() * 0.2F);
				motionY *= (double) (rand.nextFloat() * 0.2F);
				motionZ *= (double) (rand.nextFloat() * 0.2F);
				ticksInGround = 0;
				ticksInAir = 0;
			} else {
				++ticksInGround;
				if (ticksInGround >= 1200)
					setDead();
			}
			++timeInGround;
		} else {
			timeInGround = 0;
			++ticksInAir;
			Vec3d vec3d1 = new Vec3d(posX, posY, posZ);
			Vec3d vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
			RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
			vec3d1 = new Vec3d(posX, posY, posZ);
			vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

			if (raytraceresult != null)
				vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

			Entity entity = findEntityOnPath(vec3d1, vec3d);

			if (entity != null)
				raytraceresult = new RayTraceResult(entity);

			if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) raytraceresult.entityHit;

				if (shootingEntity instanceof EntityPlayer && !((EntityPlayer) shootingEntity).canAttackPlayer(entityplayer))
					raytraceresult = null;
			}

			if (raytraceresult != null)
				onHit(raytraceresult);

			if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
				onHit(raytraceresult);

			if (getIsCritical()) {
				for (int k = 0; k < 4; ++k)
					world.spawnParticle(EnumParticleTypes.CRIT, posX + motionX * (double) k / 4.0D, posY + motionY * (double) k / 4.0D, posZ + motionZ * (double) k / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			
			float f4 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));

			for (rotationPitch = (float) (MathHelper.atan2(motionY, (double) f4) * (180D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
				;
			}

			while (rotationPitch - prevRotationPitch >= 180.0F)
				prevRotationPitch += 360.0F;

			while (rotationYaw - prevRotationYaw < -180.0F)
				prevRotationYaw -= 360.0F;

			while (rotationYaw - prevRotationYaw >= 180.0F)
				prevRotationYaw += 360.0F;

			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
			float drag = 0.99F;

			if (isInWater()) {
				for (int i = 0; i < 4; ++i)
					world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ, new int[0]);
				drag = getWaterDrag();
			}

			if (isWet())
				extinguish();

			if (!hasNoGravity())
				motionY -= 0.05000000074505806D;

			motionX *= (double) drag;
			motionY *= (double) drag;
			motionZ *= (double) drag;

			setPosition(posX, posY, posZ);
			doBlockCollisions();
		}

	}

	public float getWaterDrag() {
		switch(getType()) {
		case 0:
			return 0.6F;
		case 1:
		case 2:
			return 0.99F;
		}
		return 0.99F;
	}

	protected void onHit(RayTraceResult raytraceResultIn) {
		Entity entity = raytraceResultIn.entityHit;

		if (entity != null) {

			if (entity instanceof EntityAnadia)
				setDamage(((EntityAnadia) entity).getMaxHealth());
			else
				setDamage(4);

			float f = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			int i = MathHelper.ceil((double) f * damage);

			if (getIsCritical())
				i += rand.nextInt(i / 2 + 2);

			DamageSource damagesource;

			if (shootingEntity == null)
				damagesource = DamageSource.causeThrownDamage(this, this);
			else
				damagesource = DamageSource.causeThrownDamage(this, shootingEntity);

			if (entity.attackEntityFrom(damagesource, (float) i)) {
				if (entity instanceof EntityLivingBase) {
					EntityLivingBase entitylivingbase = (EntityLivingBase) entity;

					if (!world.isRemote)
						entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);

					if (shootingEntity instanceof EntityLivingBase) {
						EnchantmentHelper.applyThornEnchantments(entitylivingbase, shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) shootingEntity, entitylivingbase);
					}

					if (!world.isRemote)
						setItemStackDamage(getItemStackDamage() + 1);

					if (shootingEntity != null && entitylivingbase != shootingEntity && entitylivingbase instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP)
						((EntityPlayerMP) shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
				}

				playSound(SoundEvents.ENTITY_ARROW_HIT, 0.5F, 0.5F);
			} else {
				motionX *= -0.10000000149011612D;
				motionY *= -0.10000000149011612D;
				motionZ *= -0.10000000149011612D;
				rotationYaw += 180.0F;
				prevRotationYaw += 180.0F;
				ticksInAir = 0;
			}
		} else {
			if (!world.isRemote && !inGround && !isInWater())
				setItemStackDamage(getItemStackDamage() + 2);

			BlockPos blockpos = raytraceResultIn.getBlockPos();
			xTile = blockpos.getX();
			yTile = blockpos.getY();
			zTile = blockpos.getZ();
			IBlockState iblockstate = world.getBlockState(blockpos);
			inTile = iblockstate.getBlock();
			inData = inTile.getMetaFromState(iblockstate);
			motionX = (double) ((float) (raytraceResultIn.hitVec.x - posX));
			motionY = (double) ((float) (raytraceResultIn.hitVec.y - posY));
			motionZ = (double) ((float) (raytraceResultIn.hitVec.z - posZ));
			float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			posX -= motionX / (double) f2 * 0.05000000074505806D;
			posY -= motionY / (double) f2 * 0.05000000074505806D;
			posZ -= motionZ / (double) f2 * 0.05000000074505806D;
			playSound(SoundEvents.ENTITY_ARROW_HIT, 0.5F, 0.5F);
			playSound(iblockstate.getBlock().getSoundType(iblockstate, world, blockpos, null).getBreakSound(), 1.5F, 1.5F);
			inGround = true;
			arrowShake = 7;

			if (iblockstate.getMaterial() != Material.AIR)
				inTile.onEntityCollision(world, blockpos, iblockstate, this);
		}
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);
		if (inGround) {
			xTile = MathHelper.floor(posX);
			yTile = MathHelper.floor(posY);
			zTile = MathHelper.floor(posZ);
		}
	}

	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
		List<Entity> list = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D), ARROW_TARGETS);
		Entity hit = null;
		double minDstSq = 0.0D;
		for (Entity entity : list) {
			if (isNotShootingEntity(entity) || ticksInAir >= 5) {
				AxisAlignedBB checkBox = entity.getEntityBoundingBox().grow(0.3D);
				RayTraceResult rayTrace = checkBox.calculateIntercept(start, end);
				if (rayTrace != null) {
					double dstSq = start.squareDistanceTo(rayTrace.hitVec);

					if (dstSq < minDstSq || minDstSq == 0.0D) {
						hit = entity;
						minDstSq = dstSq;
					}
				}
			}
		}
		return hit;
	}

	private boolean isNotShootingEntity(Entity entity) {
		if (entity == shootingEntity)
			return false;
		else if (shootingEntity instanceof EntityPlayer == false && shootingEntity != null && shootingEntity.getRidingEntity() == entity)
			return false;
		else if (shootingEntity instanceof EntityPlayer && shootingEntity != null && entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwner() == shootingEntity && shootingEntity.getRecursivePassengers().contains(entity))
			return false;
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("xTile", xTile);
		compound.setInteger("yTile", yTile);
		compound.setInteger("zTile", zTile);
		compound.setShort("life", (short) ticksInGround);
		ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(inTile);
		compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
		compound.setByte("inData", (byte) inData);
		compound.setByte("shake", (byte) arrowShake);
		compound.setByte("inGround", (byte) (inGround ? 1 : 0));
		compound.setDouble("damage", damage);
		compound.setBoolean("crit", getIsCritical());
		compound.setInteger("itemStackDamage", getItemStackDamage());
		compound.setByte("type", getType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		xTile = compound.getInteger("xTile");
		yTile = compound.getInteger("yTile");
		zTile = compound.getInteger("zTile");
		ticksInGround = compound.getShort("life");

		if (compound.hasKey("inTile", 8))
			inTile = Block.getBlockFromName(compound.getString("inTile"));
		else
			inTile = Block.getBlockById(compound.getByte("inTile") & 255);
	
		inData = compound.getByte("inData") & 255;
		arrowShake = compound.getByte("shake") & 255;
		inGround = compound.getByte("inGround") == 1;

		if (compound.hasKey("damage", 99))
			damage = compound.getDouble("damage");

		setIsCritical(compound.getBoolean("crit"));
		setItemStackDamage(compound.getInteger("itemStackDamage"));
		setType(compound.getByte("type"));
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entityIn) {
		if (!world.isRemote && inGround && arrowShake <= 0 || !world.isRemote && isInWater()) {
			if (entityIn.capabilities.isCreativeMode)
				setDead();
			else if (ticksExisted >= 20) {
				if (entityIn.inventory.addItemStackToInventory(getEntityItem())) {
				world.playSound(entityIn, entityIn.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1F, 1F);
				entityIn.onItemPickup(this, 1);
				setDead();
			}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_DEAD) {
			for (int count = 0; count <= 10; ++count) {
				BLParticles.ITEM_BREAKING.spawn(getEntityWorld(), posX + (getEntityWorld().rand.nextDouble() - 0.5D), posY + (getEntityWorld().rand.nextDouble() - 0.5D), posZ + (getEntityWorld().rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(ItemRegistry.FISHING_SPEAR)));
			}
		}
	}

	protected ItemStack getEntityItem() {
		ItemStack damagedStack = ItemStack.EMPTY;
		switch(getType()) {
		case 0:
			damagedStack = new ItemStack(ItemRegistry.FISHING_SPEAR);
			break;
		case 1:
			damagedStack = new ItemStack(ItemRegistry.FISHING_SPEAR_UNDERWATER);
			break;
		case 2:
			damagedStack = new ItemStack(ItemRegistry.FISHING_SPEAR_UNDERWATER_RETURNS);
			break;
		}
		damagedStack.setItemDamage(getItemStackDamage());
		return damagedStack;
	}

	public void setItemStackDamage(int amount) {
		dataManager.set(ITEMSTACK_DAMAGE, amount);
	}

	public int getItemStackDamage() {
		return dataManager.get(ITEMSTACK_DAMAGE);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	public void setDamage(double damageIn) {
		damage = damageIn;
	}

	public double getDamage() {
		return damage;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	public void setIsCritical(boolean critical) {
		byte b0 = ((Byte) dataManager.get(CRITICAL)).byteValue();
		if (critical)
			dataManager.set(CRITICAL, Byte.valueOf((byte) (b0 | 1)));
		else
			dataManager.set(CRITICAL, Byte.valueOf((byte) (b0 & -2)));
	}

	public boolean getIsCritical() {
		byte b0 = ((Byte) dataManager.get(CRITICAL)).byteValue();
		return (b0 & 1) != 0;
	}

	public void setType(byte type) {
		dataManager.set(TYPE, type);
	}

	public byte getType() {
		return dataManager.get(TYPE).byteValue();
	}

	public void setEnchantmentEffectsFromEntity(EntityLivingBase entity, float amount) {
		setDamage((double) (amount * 2.0F) + rand.nextGaussian() * 0.25D + (double) ((float) world.getDifficulty().getId() * 0.11F));
	}

	public boolean isInBlock() {
		return inGround;
	}

}
