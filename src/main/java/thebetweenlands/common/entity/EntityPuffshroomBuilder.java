package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom;
import thebetweenlands.common.block.plant.BlockMouldHornMushroom.EnumMouldHorn;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityPuffshroomBuilder extends EntityCreature implements IEntityBL {
	private static final DataParameter<Boolean> IS_MIDDLE = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BOOLEAN);

	public EntityPuffshroomBuilder (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_MIDDLE, false);
	}

	public void setIsMiddle(boolean state) {
		dataManager.set(IS_MIDDLE, state);
	}

	public boolean getIsMiddle() {
		return dataManager.get(IS_MIDDLE);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.getTotalWorldTime() % 20 == 0) {
			if (!world.isRemote) {
				checkForMiddle();
				if (getIsMiddle())
					if (checkForMouldhorns(world, getPosition())) {
						createSoilPatches(world, getPosition().down());
						breakMouldhorns(world, getPosition());
						killTendrills(world, getPosition());
						//DO the thing for the thing
					}
			}
			if (world.isRemote) {
				if (getIsMiddle())
					if (checkForMouldhorns(world, getPosition())) {

					}
			}
		}
	}

	private void killTendrills(World world, BlockPos pos) {
		List<Entity> list = getEntityWorld().getEntitiesWithinAABB(EntityPuffshroomBuilder.class, getEntityBoundingBox().grow(0.6D, 0D, 0.6D));
		for(Entity found : list)
			found.setDead();
	}

	private void breakMouldhorns(World world, BlockPos pos) {
		breakShroomBlocks(world, pos.add(6, 0, 0));
		breakShroomBlocks(world, pos.add(0, 0, 6));
		breakShroomBlocks(world, pos.add(-6, 0, 0));
		breakShroomBlocks(world, pos.add(0, 0, -6));
		breakShroomBlocks(world, pos.add(5, 0, 5));
		breakShroomBlocks(world, pos.add(5, 0, -5));
		breakShroomBlocks(world, pos.add(-5, 0, 5));
		breakShroomBlocks(world, pos.add(-5, 0, -5));
	}
	
	private void breakShroomBlocks(World world, BlockPos pos) {
		for(int y = 0; y <= 6; y++)
			if (world.getBlockState(pos.add(0, y, 0)).getBlock() instanceof BlockMouldHornMushroom) {
				world.playEvent(null, 2001, pos.add(0, y, 0), Block.getIdFromBlock(BlockRegistry.MOULD_HORN));
				world.setBlockToAir(pos.add(0, y, 0));
			}
	}

	private void createSoilPatches(World world, BlockPos pos) {
		setSoilPatches(world, pos);
		setSoilPatches(world, pos.add(6, 0, 0));
		setSoilPatches(world, pos.add(0, 0, 6));
		setSoilPatches(world, pos.add(-6, 0, 0));
		setSoilPatches(world, pos.add(0, 0, -6));
		setSoilPatches(world, pos.add(5, 0, 5));
		setSoilPatches(world, pos.add(5, 0, -5));
		setSoilPatches(world, pos.add(-5, 0, 5));
		setSoilPatches(world, pos.add(-5, 0, -5));
	}

	private void setSoilPatches(World world, BlockPos pos) {
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				world.setBlockState(pos.add(x, 0, z), BlockRegistry.MOULDY_SOIL.getDefaultState());
	}

	private boolean checkForMouldhorns(World world, BlockPos pos) {
		int count = 0;
		if(checkForCap(world, pos.add(6, 0, 0)))
			count++;
		if(checkForCap(world, pos.add(0, 0, 6)))
			count++;
		if(checkForCap(world, pos.add(-6, 0, 0)))
			count++;
		if(checkForCap(world, pos.add(0, 0, -6)))
			count++;
		if(checkForCap(world, pos.add(5, 0, 5)))
			count++;
		if(checkForCap(world, pos.add(5, 0, -5)))
			count++;
		if(checkForCap(world, pos.add(-5, 0, 5)))
			count++;
		if(checkForCap(world, pos.add(-5, 0, -5)))
			count++;
		return count >= 8;
	}

	private boolean checkForCap(World world, BlockPos pos) {
		for(int y = 0; y <= 6; y++)
			if (world.getBlockState(pos.add(0, y, 0)).getBlock() instanceof BlockMouldHornMushroom && world.getBlockState(pos.add(0, y, 0)).getValue(BlockMouldHornMushroom.MOULD_HORN_TYPE) == EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS) {
				if (world.isRemote) {
					spawnSporeBeamParticles(new Vec3d(pos.getX() - getPosition().getX(), y + 0.75D, pos.getZ() - getPosition().getZ()));
					}
				return true;
				}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnSporeBeamParticles(Vec3d target) {
		for(int i = 0; i < 20; i++) {
			float offsetLen = this.world.rand.nextFloat();
			Vec3d offset = new Vec3d(target.x * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f);
			float vx = (world.rand.nextFloat() * 0.5f - 0.25f) * 0.00125f;
			float vy = (world.rand.nextFloat() * 0.5f - 0.25f) * 0.00125f;
			float vz = (world.rand.nextFloat() * 0.5f - 0.25f) * 0.00125f;
			float scale = 1f + world.rand.nextFloat();
			BLParticles.SMOKE.spawn(world, this.posX + offset.x, this.posY + offset.y, this.posZ + offset.z, 
			ParticleArgs.get().withMotion(vx, vy, vz).withColor(0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 1.0F).withScale(scale).withData(100));
		}
	}

	private void checkForMiddle() {
		List<Entity> list = getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(0.6D, 0D, 0.6D));
		if(list.stream().filter(e -> e instanceof EntityPuffshroomBuilder).count() == 8)
			setIsMiddle(true);
		else
			setIsMiddle(false);
	}

	@Override
	public void onLivingUpdate() {
		if(getIsMiddle() && world.isRemote) {
			spawnSporeDustParticles(world, getPosition(), 0, 0, 0);
			spawnSporeDustParticles(world, getPosition(), 6, 0, 0);
			spawnSporeDustParticles(world, getPosition(), 0, 0, 6);
			spawnSporeDustParticles(world, getPosition(), -6, 0, 0);
			spawnSporeDustParticles(world, getPosition(),0, 0, -6);
			spawnSporeDustParticles(world, getPosition(),5, 0, 5);
			spawnSporeDustParticles(world, getPosition(),5, 0, -5);
			spawnSporeDustParticles(world, getPosition(),-5, 0, 5);
			spawnSporeDustParticles(world, getPosition(),-5, 0, -5);
			}
		super.onLivingUpdate();
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnSporeDustParticles(World world, BlockPos pos, int offX, int offY, int offZ) {
		BLParticles.REDSTONE_DUST.spawn(world, pos.getX() + offX + 0.5D + (rand.nextDouble() - 0.5D) * width, pos.getY() + offY  + 0.5D + rand.nextDouble() * height, pos.getZ() + offZ + 0.5D + (rand.nextDouble() - 0.5D) * width, 
		ParticleArgs.get().withColor(0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 0.5F + this.rand.nextFloat() * 0.5F, 1.0F));
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
    public boolean getCanSpawnHere() {
        return true;
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote)
			checkForMiddle();
		return livingdata;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setIsMiddle(nbt.getBoolean("isMiddle"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("isMiddle", getIsMiddle());
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
}
