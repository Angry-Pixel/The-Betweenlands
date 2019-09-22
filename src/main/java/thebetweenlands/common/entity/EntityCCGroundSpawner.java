package thebetweenlands.common.entity;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.BlockCompactedMudSlope;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EntityCCGroundSpawner extends EntityProximitySpawner {
	private static final DataParameter<Boolean> IS_WORLD_SPANWED = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SPAWN_COUNT = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> CAN_BE_REMOVED_SAFELY = EntityDataManager.createKey(EntityCCGroundSpawner.class, DataSerializers.BOOLEAN);
	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();

	public EntityCCGroundSpawner(World world) {
		super(world);
		setSize(3F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_WORLD_SPANWED, true);
		dataManager.register(SPAWN_COUNT, 0);
		dataManager.register(CAN_BE_REMOVED_SAFELY, false);
	}

	@Override
	public boolean getCanSpawnHere() {
		int solidCount = 0;
		BlockPos pos = new BlockPos(this);

		if(pos.getY() < WorldProviderBetweenlands.CAVE_START) {
			return false;
		}

		for(int xo = -1; xo <= 1; xo++) {
			for(int zo = -1; zo <= 1; zo++) {
				BlockPos offsetPos = pos.add(xo, 0, zo);
				IBlockState state = this.world.getBlockState(offsetPos);

				if(state.getMaterial().isLiquid()) {
					return false;
				}

				if(SurfaceType.MIXED_GROUND.apply(state)) {
					solidCount++;
				} else if(xo == 0 && zo == 0) {
					return false;
				}

				if(!this.world.isAirBlock(offsetPos.up())) {
					return false;
				}
			}
		}

		return solidCount >= 6;
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }
/*
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}
*/
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!getEntityWorld().isRemote) {
			if(isWorldSpawned() && !isBloodSky(getEntityWorld()))
				setDead();

			if (getEntityWorld().getTotalWorldTime() % 60 == 0)
				checkArea();
			List<EntityFallingBlock> listPlug = getEntityWorld().getEntitiesWithinAABB(EntityFallingBlock.class, getEntityBoundingBox());
			if (!listPlug.isEmpty()) {
				getEntityWorld().setBlockToAir(getPosition());
				setDead();
			}
		}

		this.setPosition(MathHelper.floor(this.posX) + 0.5D, MathHelper.floor(this.posY), MathHelper.floor(this.posZ) + 0.5D);
		this.prevPosX = this.lastTickPosX = this.posX;
		this.prevPosY = this.lastTickPosY = this.posY;
		this.prevPosZ = this.lastTickPosZ = this.posZ;
	}

	public boolean isBloodSky(World world) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
        if(worldStorage.getEnvironmentEventRegistry().bloodSky.isActive())
            return true;
        return false;
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		if (!getEntityWorld().isRemote) {
			if(getCanBeRemovedSafely() && canBeRemovedNow())
				setDead();
			if (getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(isWorldSpawned() && !isBloodSky(getEntityWorld()))
					return null;
				List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
				if(list.stream().filter(e -> e instanceof EntityCryptCrawler).count() >= 4)
					return null;
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null)
						if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
							if (canSneakPast() && entity.isSneaking())
								return null;
							else if (checkSight() && !canEntityBeSeen(entity) || getCanBeRemovedSafely())
								return null;
							else {
								for (int count = 0; count < getEntitySpawnCount(); count++) {
									Entity spawn = getEntitySpawned();
									if (spawn != null) {
										performPreSpawnaction(entity, spawn);
										if (!spawn.isDead) // just in case of pre-emptive removal
											getEntityWorld().spawnEntity(spawn);
										performPostSpawnaction(entity, spawn);
									}
								}
							}
						}
				}
			}
		}
		return null;
	}

    public boolean canBeRemovedNow() {
    	AxisAlignedBB dead_zone = getEntityBoundingBox().grow(0D, 1D, 0D).offset(0D, -0.5D, 0D);
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, dead_zone);
		if(list.stream().filter(e -> e instanceof EntityCryptCrawler).count() >= 1)
			return false;
        return true;
    }

	@Override
    public float getEyeHeight() {
        return height + 0.5F; // sort of needed so it can see a bit further
    }
/*
	@Override
	protected boolean isMovementBlocked() {
		return true;
	}
*/
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
				setCanBeRemovedSafely(true);
			}
		}
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity instanceof EntityFallingBlock)
			if (!getEntityWorld().isRemote)
				setCanBeRemovedSafely(true);
	}

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		getEntityWorld().playSound((EntityPlayer)null, getPosition(), getDigSound(), SoundCategory.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPosition(getPosition().getX() + 0.5F, getPosition().getY() - 1.5F, getPosition().getZ() + 0.5F);
	}

	protected SoundEvent getDigSound() {
		return SoundRegistry.CRYPT_CRAWLER_DIG;
	}

	@Override
	protected void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned) {
		if(!getEntityWorld().isRemote) {
			TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.GOOP_SPLAT, (float) posX, (float)posY + 0.25F, (float)posZ, 0F));
			entitySpawned.motionY += 0.5D;
			if(isWorldSpawned() && getSpawnCount() >= maxUseCount())
				setCanBeRemovedSafely(true);
		}
	}

	@Override
	protected float getProximityHorizontal() {
		return 8F;
	}

	@Override
	protected float getProximityVertical() {
		return 2F;
	}

	@Override
	protected boolean canSneakPast() {
		return false;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		EntityCryptCrawler crawler = new EntityCryptCrawler(getEntityWorld());
		crawler.onInitialSpawn(getEntityWorld().getDifficultyForLocation(getPosition()), null);
		return crawler;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 5;
	}

	public void setIsWorldSpawned(boolean world_spawned) {
		dataManager.set(IS_WORLD_SPANWED, world_spawned);
	}

	public boolean isWorldSpawned() {
		return dataManager.get(IS_WORLD_SPANWED);
	}

	public void setSpawnCount(int spawn_count) {
		dataManager.set(SPAWN_COUNT, spawn_count);
	}

	public int getSpawnCount() {
		return dataManager.get(SPAWN_COUNT);
	}

	public void setCanBeRemovedSafely(boolean safe) {
		dataManager.set(CAN_BE_REMOVED_SAFELY, safe);
	}

	public boolean getCanBeRemovedSafely() {
		return dataManager.get(CAN_BE_REMOVED_SAFELY);
	}

	@Override
    public void setDead() {
		if(!getEntityWorld().isRemote) {
			if(isWorldSpawned())
				if(getEntityData().hasKey("tempBlockTypes"))
					loadOriginBlocks(getEntityWorld(), getEntityData());
		}
        super.setDead();
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			getOriginBlocks(getEntityWorld(), getPosition());
			getEntityWorld().setBlockState(getPosition(), blockHelper.AIR);
			getEntityWorld().setBlockState(getPosition().add(0, -1, 0), blockHelper.COMPACTED_MUD);
			getEntityWorld().setBlockState(getPosition().add(-1, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.NORTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(0, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.NORTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(1, 0, -1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.NORTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(-1, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.SOUTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(0, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.SOUTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(1, 0, 1), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.SOUTH).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(-1, 0, 0), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.WEST).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
			getEntityWorld().setBlockState(getPosition().add(1, 0, 0), blockHelper.COMPACTED_MUD_SLOPE.withProperty(BlockCompactedMudSlope.FACING, EnumFacing.EAST).withProperty(BlockCompactedMudSlope.HALF, EnumHalf.BOTTOM));
		}
		return livingdata;
	}

	private void getOriginBlocks(World world, BlockPos pos) {
		NBTTagList tagList = new NBTTagList();
		NBTTagCompound entityNbt = getEntityData();
		for (int x = -1; x <= 1; x ++)
			for (int z = -1; z <= 1; z++) 
				for(int y = 0; y <= 1; y++) {
				IBlockState state = world.getBlockState(pos.add(x, -y, z));
				NBTTagCompound tag = new NBTTagCompound();
				NBTUtil.writeBlockState(tag, state);
				tagList.appendTag(tag);
			}

		if (!tagList.isEmpty()) {
			entityNbt.setTag("tempBlockTypes", tagList);
			entityNbt.setTag("originPos", NBTUtil.createPosTag(pos));
		}
		writeEntityToNBT(entityNbt);
	}

	public void loadOriginBlocks(World world, NBTTagCompound tag) {
		NBTTagCompound entityNbt = getEntityData();
		NBTTagList tagListPos = entityNbt.getTagList("originPos", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound nbttagcompoundPos = tagListPos.getCompoundTagAt(0);
		BlockPos origin = NBTUtil.getPosFromTag(nbttagcompoundPos);
		List<IBlockState> list = new ArrayList<IBlockState>();
		NBTTagList tagList = entityNbt.getTagList("tempBlockTypes", Constants.NBT.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < tagList.tagCount(); ++indexCount) {
			NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(indexCount);
			IBlockState state = NBTUtil.readBlockState(nbttagcompound);
			list.add(indexCount, state);
		}
		int a = 0;
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				for(int y = 0; y <= 1; y++) {
				world.setBlockState(origin.add(x, -y, z), list.get(a++), 3);
			}
		getEntityWorld().playSound((EntityPlayer)null, origin, SoundRegistry.ROOF_COLLAPSE, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("world_spawned", Constants.NBT.TAG_BYTE))
			setIsWorldSpawned(nbt.getBoolean("world_spawned"));
		if(nbt.hasKey("remove_safely", Constants.NBT.TAG_BYTE))
			setCanBeRemovedSafely(nbt.getBoolean("remove_safely"));
		setSpawnCount(nbt.getInteger("spawn_count"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("world_spawned", isWorldSpawned());
		nbt.setBoolean("remove_safely", getCanBeRemovedSafely());
		nbt.setInteger("spawn_count", getSpawnCount());
	}
}