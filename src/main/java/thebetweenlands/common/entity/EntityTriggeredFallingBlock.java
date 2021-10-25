package thebetweenlands.common.entity;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTriggeredFallingBlock extends EntityProximitySpawner {

	private static final DataParameter<Boolean> IS_WALK_WAY = EntityDataManager.createKey(EntityTriggeredFallingBlock.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.createKey(EntityTriggeredFallingBlock.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_TEMP = EntityDataManager.createKey(EntityTriggeredFallingBlock.class, DataSerializers.BOOLEAN);
	public EntityTriggeredFallingBlock(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_WALK_WAY, false);
		dataManager.register(IS_HANGING, false);
		dataManager.register(IS_TEMP, false);
	}

	@Override
	public void onUpdate() {
		if (!getEntityWorld().isRemote) {
			if (isTemporary() && ticksExisted > 20)
				setDead();
			if (getEntityWorld().getTotalWorldTime() % 5 == 0) {
				if (!isHanging())
					checkArea();
				else
					checkBlockAbove();
			}
		}

		if (getEntityWorld().isRemote)
			dustParticles();
	}

	private void checkBlockAbove() {
		if (getEntityWorld().isAirBlock(getPosition().up())) {
			Entity spawn = getEntitySpawned();
			if (spawn != null) {
				performPreSpawnaction(this, spawn);
				if (!spawn.isDead) // just in case of pre-emptive removal
					getEntityWorld().spawnEntity(spawn);
			}
			if (!isDead && isSingleUse())
				setDead();
		}
	}

	public void dustParticles() {
		if (rand.nextInt(16) == 0) {
			BlockPos blockpos = getPosition().down();
			if (canFallThrough(getEntityWorld().getBlockState(blockpos))) {
				double d0 = (double) ((float) getPosition().getX() + rand.nextFloat());
				double d1 = !isWalkway() ? (double) getPosition().getY() - 0.05D : (double) getPosition().getY() + 1D;
				double d2 = (double) ((float) getPosition().getZ() + rand.nextFloat());
				if(!isWalkway())
					getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(getBlockType(getEntityWorld(), getPosition())));
				else {
					double motionX = getEntityWorld().rand.nextDouble() * 0.1F - 0.05F;
					double motionY = getEntityWorld().rand.nextDouble() * 0.025F + 0.025F;
					double motionZ = getEntityWorld().rand.nextDouble() * 0.1F - 0.05F;
					getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, d0, d1, d2, motionX, motionY, motionZ, Block.getStateId(getBlockType(getEntityWorld(), getPosition())));
				}
			}
		}
	}

    public static boolean canFallThrough(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		((EntityFallingBlock)entitySpawned).setHurtEntities(true);
		 if (!getEntityWorld().isRemote) {
			 targetEntity.getEntityWorld().playSound(null, targetEntity.getPosition(), SoundRegistry.ROOF_COLLAPSE, SoundCategory.BLOCKS, 0.5F, 1.0F);
		 }
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return isWalkway() ? 0.0625F : 1F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, isWalkway() ? 1D : - getProximityVertical () * 2 , 0D);
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return false;
	}

	@Override
	protected Entity getEntitySpawned() {
		if(getBlockType(getEntityWorld(), getPosition()).getBlock() != null) {
			EntityFallingBlock entity = new EntityFallingBlock(getEntityWorld(), posX, posY, posZ, getBlockType(getEntityWorld(), getPosition()));
			entity.shouldDropItem = false;
			return entity;
		}
		return null;
	}

	private IBlockState getBlockType(World world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}

	public void setWalkway(boolean walkway) {
		dataManager.set(IS_WALK_WAY, walkway);
	}

	public boolean isWalkway() {
		return dataManager.get(IS_WALK_WAY);
	}

	public boolean isHanging() {
		return dataManager.get(IS_HANGING);
	}

	public void setHanging(boolean walkway) {
		dataManager.set(IS_HANGING, walkway);
	}
	
	public boolean isTemporary() {
		return dataManager.get(IS_TEMP);
	}

	public void setTemporary(boolean temp) {
		dataManager.set(IS_TEMP, temp);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setWalkway(nbt.getBoolean("walk_way"));
		setHanging(nbt.getBoolean("hanging"));
		setTemporary(nbt.getBoolean("temp"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("walk_way", isWalkway());
		nbt.setBoolean("hanging", isHanging());
		nbt.setBoolean("temp", isTemporary());
	}
}