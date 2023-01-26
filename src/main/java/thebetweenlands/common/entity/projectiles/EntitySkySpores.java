package thebetweenlands.common.entity.projectiles;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom.EnumMouldHorn;
import thebetweenlands.common.registries.BlockRegistry;

public class EntitySkySpores extends EntityLiving {

	private static final DataParameter<BlockPos> TARGET = EntityDataManager.<BlockPos>createKey(EntitySkySpores.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntitySkySpores.class, DataSerializers.ITEM_STACK);
	public double impactTargetX;
	public double impactTargetY;
	public double impactTargetZ;
	public double accelerationX;
	public double accelerationY;
	public double accelerationZ;
	public EntityPlayer playerTarget;

	public EntitySkySpores(World world) {
		super(world);
		setSize(1F, 1F);
	}

	public EntitySkySpores(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y, z);
	}

	public EntitySkySpores(World world, double x, double y, double z, EntityPlayer playerIn) {
		this(world);
		setPosition(x, y, z);
		setNoGravity(true);
		playerTarget = playerIn;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TARGET, new BlockPos(0, 0, 0));
		dataManager.register(STACK, new ItemStack(Items.FIRE_CHARGE));
	}

    @Override
    protected void applyEntityAttributes() {
    	super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		int x = compound.getInteger("targetX");
		int y = compound.getInteger("targetY");
		int z = compound.getInteger("targetZ");
		setTarget(new BlockPos(x, y, z));
		ItemStack itemstack = new ItemStack(compound.getCompoundTag("Item"));
		setStack(itemstack);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("targetX", getTarget().getX());
		compound.setInteger("targetY", getTarget().getY());
		compound.setInteger("targetZ", getTarget().getZ());
	      ItemStack itemstack = this.getStack();
		if (!itemstack.isEmpty())
			compound.setTag("Item", itemstack.writeToNBT(new NBTTagCompound()));
	}
	
	public void setTarget(BlockPos pos) {
		dataManager.set(TARGET, pos);
	}

	public BlockPos getTarget() {
		return dataManager.get(TARGET);
	}

	public void setStack(ItemStack stack) {
		dataManager.set(STACK, stack);
	}

	public ItemStack getStack() {
		return dataManager.get(STACK);
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getRenderItem() {
		ItemStack itemstack = this.getStack();
		return itemstack.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : itemstack;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.IN_FIRE) || source.equals(DamageSource.ON_FIRE) || source.equals(DamageSource.LAVA) || source.equals(DamageSource.FALL))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote)
			if (ticksExisted == 1)
				setPlayerAsTarget();

		if (world.isRemote && ticksExisted > 1 || !isDead && world.isBlockLoaded(getPosition()) && ticksExisted > 1) {
			if(onGround)
				setNoGravity(false);
			RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, true, this);
			if (raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS)
				onImpact(raytraceresult);
			doBlockCollisions();

			Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
			float f = getMotionFactor();
			impactTargetX = getTarget().getX() - posX;
			impactTargetY = getTarget().getY() - posY + height;
			impactTargetZ = getTarget().getZ() - posZ;

			double distanceSqRt = (double) MathHelper.sqrt(impactTargetX * impactTargetX + impactTargetY * impactTargetY + impactTargetZ * impactTargetZ);

			if (distanceSqRt != 0.0D) {
				accelerationX = impactTargetX / distanceSqRt * 0.1D;
				accelerationY = impactTargetY / distanceSqRt * 0.05D;
				accelerationZ = impactTargetZ / distanceSqRt * 0.1D;
				vector3d.add(accelerationX, accelerationY, accelerationZ).scale((double) f);
				addVelocity(accelerationX, accelerationY, accelerationZ);
				float f2 = (float) (MathHelper.atan2(vector3d.z, vector3d.x) * (double) (180F / (float) Math.PI)) - 90.0F;
				rotationYaw = limitAngle(rotationYaw, f2, 90.0F);
				renderYawOffset = rotationYaw;

				if (isInWater()) {
			//	for (int i = 0; i < 4; ++i)
				//	world.addParticle(ParticleTypes.BUBBLE, d0 - vector3d.x * 0.25D, d1 - vector3d.y * 0.25D, d2 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
					f = 0.8F;
				}
			//	world.addParticle(getParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}
	
	public float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
		float deg = MathHelper.wrapDegrees(targetAngle - sourceAngle);
		if (deg > maximumChange)
			deg = maximumChange;

		if (deg < -maximumChange)
			deg = -maximumChange;

		float deg2 = sourceAngle + deg;
		if (deg2 < 0.0F)
			deg2 += 360.0F;
		else if (deg2 > 360.0F)
			deg2 -= 360.0F;

		return deg2;
	}

	public void onImpact(RayTraceResult result) {
		RayTraceResult.Type raytraceresult$type = result.typeOfHit;
		if (raytraceresult$type == RayTraceResult.Type.BLOCK)
			onBlockHit((RayTraceResult) result);
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		entity.applyEntityCollision(this);
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(!getEntityWorld().isRemote) {
				player.attackEntityFrom(DamageSource.FALLING_BLOCK, 2F);
				player.setFire(5);
			}
		}
		setDead();
	}

	public void onBlockHit(RayTraceResult result) {
		if (!getEntityWorld().isRemote) {
			getEntityWorld().setBlockState(result.getBlockPos().up(), getMainBlockStateForPlacement());
			//getEntityWorld().setBlockState(result.getBlockPos().up(2), Blocks.FIRE.getDefaultState());
	        getEntityWorld().playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2F, 1F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.8F);
	        //impactParticles(this, (float)posX, (float)posY, (float)posZ);
			createDebris(result.getBlockPos().up());
			setDead();
		}
	}

	private void createDebris(BlockPos pos) {
		for (int x = 0; x < 12; x++) {
			double angle = Math.toRadians(x * 30D);
			double offSetX = Math.floor(-Math.sin(angle) * 3D);
			double offSetZ = Math.floor(Math.cos(angle) * 3D);

			if(getEntityWorld().isAirBlock(pos.add(offSetX, 1, offSetZ))) {
				IBlockState placedBlock = getDebrisBlockStateForPlacement();
				getEntityWorld().setBlockState(pos.add(offSetX, 1, offSetZ), placedBlock);
				EntityFallingBlock debris = new EntityFallingBlock(getEntityWorld(), Math.floor(pos.getX() + 0.5D + offSetX), pos.getY() + 1D, Math.floor(pos.getZ() + 0.5D + offSetZ), placedBlock);
				Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
				double velX = pos.getX() + offSetX * 2D - debris.posX;
				double velY = pos.getY() + 6D - debris.posY;
				double velZ = pos.getZ() + offSetZ * 2D - debris.posZ;
				double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
				double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
				double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				vector3d.add(accelerationX, accelerationY, accelerationZ).scale((double) getMotionFactor());
				debris.addVelocity(accelerationX, accelerationY, accelerationZ);
				getEntityWorld().spawnEntity(debris);
			}
		}
	}
	
	private IBlockState getMainBlockStateForPlacement() {
		return BlockRegistry.MOULDY_SOIL.getDefaultState();
	}
	
	private IBlockState getDebrisBlockStateForPlacement() {
		return BlockRegistry.MOULD_HORN.getDefaultState().withProperty(BlockMouldHornMushroom.MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM);
	}

	public float getMotionFactor() {
		return 0.95F;
	}

	public void setPlayerAsTarget() {
		if (playerTarget == null) {
			List<? extends EntityPlayer> players = getEntityWorld().getPlayers(EntityPlayer.class, EntitySelectors.IS_ALIVE);
			if (!players.isEmpty()) {
				Collections.shuffle(players);
				setTarget(players.get(0).getPosition().down());
			}
		} else {
			setTarget(playerTarget.getPosition().down());
		}
			
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isBurning() {
		boolean flameOn = world != null && world.isRemote;
		return false; //flameOn;
	}

	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 128.0D;
		return distance < d0 * d0;
	}
}