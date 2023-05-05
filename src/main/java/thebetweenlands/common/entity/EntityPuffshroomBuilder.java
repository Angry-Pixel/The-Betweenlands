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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	private static final DataParameter<BlockPos> PATCH_1 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_2 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_3 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_4 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_5 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_6 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_7 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> PATCH_8 = EntityDataManager.createKey(EntityPuffshroomBuilder.class, DataSerializers.BLOCK_POS);
	public int renderTicks = 0;
	public int prev_renderTicks = 0;
	public int index = 0;
	public boolean hasPlacedPatchMiddle;
	public boolean hasPlacedPatch1;
	public boolean hasPlacedPatch2;
	public boolean hasPlacedPatch3;
	public boolean hasPlacedPatch4;
	public boolean hasPlacedPatch5;
	public boolean hasPlacedPatch6;
	public boolean hasPlacedPatch7;
	public boolean hasPlacedPatch8;

	public EntityPuffshroomBuilder (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_MIDDLE, false);
		dataManager.register(PATCH_1, new BlockPos(0,0,0));
		dataManager.register(PATCH_2, new BlockPos(0,0,0));
		dataManager.register(PATCH_3, new BlockPos(0,0,0));
		dataManager.register(PATCH_4, new BlockPos(0,0,0));
		dataManager.register(PATCH_5, new BlockPos(0,0,0));
		dataManager.register(PATCH_6, new BlockPos(0,0,0));
		dataManager.register(PATCH_7, new BlockPos(0,0,0));
		dataManager.register(PATCH_8, new BlockPos(0,0,0));
	}

	public void setIsMiddle(boolean state) {
		dataManager.set(IS_MIDDLE, state);
	}

	public boolean getIsMiddle() {
		return dataManager.get(IS_MIDDLE);
	}

	public void setPatch1(BlockPos pos) {
		dataManager.set(PATCH_1, pos);
	}

	public BlockPos getPatch1() {
		return dataManager.get(PATCH_1);
	}

	public void setPatch2(BlockPos pos) {
		dataManager.set(PATCH_2, pos);
	}

	public BlockPos getPatch2() {
		return dataManager.get(PATCH_2);
	}

	public void setPatch3(BlockPos pos) {
		dataManager.set(PATCH_3, pos);
	}

	public BlockPos getPatch3() {
		return dataManager.get(PATCH_3);
	}

	public void setPatch4(BlockPos pos) {
		dataManager.set(PATCH_4, pos);
	}

	public BlockPos getPatch4() {
		return dataManager.get(PATCH_4);
	}

	public void setPatch5(BlockPos pos) {
		dataManager.set(PATCH_5, pos);
	}

	public BlockPos getPatch5() {
		return dataManager.get(PATCH_5);
	}

	public void setPatch6(BlockPos pos) {
		dataManager.set(PATCH_6, pos);
	}

	public BlockPos getPatch6() {
		return dataManager.get(PATCH_6);
	}

	public void setPatch7(BlockPos pos) {
		dataManager.set(PATCH_7, pos);
	}

	public BlockPos getPatch7() {
		return dataManager.get(PATCH_7);
	}

	public void setPatch8(BlockPos pos) {
		dataManager.set(PATCH_8, pos);
	}

	public BlockPos getPatch8() {
		return dataManager.get(PATCH_8);
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
		if (getEntityWorld().isRemote)
			prev_renderTicks = renderTicks;

		if (world.getTotalWorldTime() % 20 == 0) {
			if (!world.isRemote) {
				checkForMiddle();
				if (getIsMiddle()) {
						if (index == 2)
							if(!hasPlacedPatchMiddle) {
								setSoilPatches(getPosition().down());
								hasPlacedPatchMiddle = true;
							}
						if(!hasPlacedPatch1)
							plotMouldyPath(getPosition().down(), getPatch1().down(), index, 1);
						if(!hasPlacedPatch2)
							plotMouldyPath(getPosition().down(), getPatch2().down(), index, 2);
						if(!hasPlacedPatch3)
							plotMouldyPath(getPosition().down(), getPatch3().down(), index, 3);
						if(!hasPlacedPatch4)
							plotMouldyPath(getPosition().down(), getPatch4().down(), index, 4);
						if(!hasPlacedPatch5)
							plotMouldyPath(getPosition().down(), getPatch5().down(), index, 5);
						if(!hasPlacedPatch6)
							plotMouldyPath(getPosition().down(), getPatch6().down(), index, 6);
						if(!hasPlacedPatch7)
							plotMouldyPath(getPosition().down(), getPatch7().down(), index, 7);
						if(!hasPlacedPatch8)
							plotMouldyPath(getPosition().down(), getPatch8().down(), index, 8);
						if (index <= 16) // just to stop unneeded increments
							index++;

					if (checkForMouldhorns()) {
						//breakMouldhorns();
						killTendrills();
						//DO the thing for the thing

					}
				}
			}
			if (world.isRemote) {
				if (getIsMiddle())
					if (checkForMouldhorns()) {

					}
			}
		}
		if (getEntityWorld().isRemote)
			renderTicks++;
	}

	private void plotMouldyPath(BlockPos posStart, BlockPos posEnd, int index, int patchNumber) {
		Vec3d startPos = new Vec3d(posStart.getX() + 0.5D, posStart.getY(), posStart.getZ() + 0.5D);
		Vec3d endPos = new Vec3d(posEnd.getX() + 0.5D, posEnd.getY(), posEnd.getZ() + 0.5D);
		Vec3d targetVector = new Vec3d(posEnd.getX() + 0.5D - posStart.getX() - 0.5D, posEnd.getY() - posStart.getY(), posEnd.getZ() + 0.5D - posStart.getZ() - 0.5D).normalize();
		int range = MathHelper.floor(startPos.distanceTo(endPos) + 0.5D);
		if (index <= range) {
			getEntityWorld().setBlockState(posStart.add(targetVector.x * index + 0.5D, targetVector.y * index, targetVector.z * index + 0.5D), BlockRegistry.MOULDY_SOIL.getDefaultState());
			if (index == range) {
				getEntityWorld().setBlockState(posStart.add(targetVector.x * index + 0.5D, targetVector.y * index + 1.0D, targetVector.z * index + 0.5D), BlockRegistry.MOULD_HORN.getDefaultState().withProperty(BlockMouldHornMushroom.MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM));
				createSoilPatches(patchNumber);
			}
		}
	}

	private void killTendrills() {
		List<Entity> list = getEntityWorld().getEntitiesWithinAABB(EntityPuffshroomBuilder.class, getEntityBoundingBox().grow(0.6D, 0D, 0.6D));
		for(Entity found : list)
			found.setDead();
	}

	private void breakMouldhorns() {
		breakShroomBlocks(getPatch1());
		breakShroomBlocks(getPatch2());
		breakShroomBlocks(getPatch3());
		breakShroomBlocks(getPatch4());
		breakShroomBlocks(getPatch5());
		breakShroomBlocks(getPatch6());
		breakShroomBlocks(getPatch7());
		breakShroomBlocks(getPatch8());
	}
	
	private void breakShroomBlocks(BlockPos pos) {
		for(int y = 0; y <= 6; y++)
			if (getEntityWorld().getBlockState(pos.add(0, y, 0)).getBlock() instanceof BlockMouldHornMushroom) {
				getEntityWorld().playEvent(null, 2001, pos.add(0, y, 0), Block.getIdFromBlock(BlockRegistry.MOULD_HORN));
				getEntityWorld().setBlockToAir(pos.add(0, y, 0));
			}
	}

	private void createSoilPatches(int patchNumber) {
		switch (patchNumber) {
		case 1:
			setSoilPatches(getPatch1().down());
			hasPlacedPatch1 = true;
			break;
		case 2:
			setSoilPatches(getPatch2().down());
			hasPlacedPatch2 = true;
			break;
		case 3:
			setSoilPatches(getPatch3().down());
			hasPlacedPatch3 = true;
			break;
		case 4:
			setSoilPatches(getPatch4().down());
			hasPlacedPatch4 = true;
			break;
		case 5:
			setSoilPatches(getPatch5().down());
			hasPlacedPatch5 = true;
			break;
		case 6:
			setSoilPatches(getPatch6().down());
			hasPlacedPatch6 = true;
			break;
		case 7:
			setSoilPatches(getPatch7().down());
			hasPlacedPatch7 = true;
			break;
		case 8:
			setSoilPatches(getPatch8().down());
			hasPlacedPatch8 = true;
			break;
		}
	}

	private void setSoilPatches(BlockPos pos) {
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				getEntityWorld().setBlockState(pos.add(x, 0, z), BlockRegistry.MOULDY_SOIL.getDefaultState());
	}

	private boolean checkForMouldhorns() {
		int count = 0;
		if(checkForCap(getPatch1()))
			count++;
		if(checkForCap(getPatch2()))
			count++;
		if(checkForCap(getPatch3()))
			count++;
		if(checkForCap(getPatch4()))
			count++;
		if(checkForCap(getPatch5()))
			count++;
		if(checkForCap(getPatch6()))
			count++;
		if(checkForCap(getPatch7()))
			count++;
		if(checkForCap(getPatch8()))
			count++;
		return count >= 8;
	}

	private boolean checkForCap(BlockPos pos) {
		for(int y = 0; y <= 6; y++)
			if (getEntityWorld().getBlockState(pos.add(0, y, 0)).getBlock() instanceof BlockMouldHornMushroom && getEntityWorld().getBlockState(pos.add(0, y, 0)).getValue(BlockMouldHornMushroom.MOULD_HORN_TYPE) == EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS) {
				if (getEntityWorld().isRemote) {
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
		if(list.stream().filter(e -> e instanceof EntityPuffshroomBuilder).count() == 8) {
			if (!getIsMiddle()) {
				setIsMiddle(true);
				setPatch1(getPosition().add(5 + rand.nextInt(5), 0, 0));
				setPatch2(getPosition().add(0, 0, 5 + rand.nextInt(5)));
				setPatch3(getPosition().add(-5 - rand.nextInt(5), 0, 0));
				setPatch4(getPosition().add(0, 0, -5 - rand.nextInt(5)));
				setPatch5(getPosition().add(4 + rand.nextInt(5), 0, 4 + rand.nextInt(5)));
				setPatch6(getPosition().add(4 + rand.nextInt(5), 0, -4 - rand.nextInt(5)));
				setPatch7(getPosition().add(-4 - rand.nextInt(5), 0, 4 + rand.nextInt(5)));
				setPatch8(getPosition().add(-4 - rand.nextInt(5), 0, -4 - rand.nextInt(5)));
				index = 0;
			}
		}
		else
			setIsMiddle(false);
	}

	@Override
	public void onLivingUpdate() {
		if(getIsMiddle() && world.isRemote) {
			spawnSporeDustParticles(world, getPosition());
			spawnSporeDustParticles(world, getPatch1());
			spawnSporeDustParticles(world, getPatch2());
			spawnSporeDustParticles(world, getPatch3());
			spawnSporeDustParticles(world, getPatch4());
			spawnSporeDustParticles(world, getPatch5());
			spawnSporeDustParticles(world, getPatch6());
			spawnSporeDustParticles(world, getPatch7());
			spawnSporeDustParticles(world, getPatch8());
			}
		super.onLivingUpdate();
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnSporeDustParticles(World world, BlockPos posPatch) {
		if(!(world.getBlockState(posPatch).getBlock() instanceof BlockMouldHornMushroom))
			BLParticles.REDSTONE_DUST.spawn(world, posPatch.getX()+ 0.5D + (rand.nextDouble() - 0.5D) * width, posPatch.getY()  + 0.5D + rand.nextDouble() * height, posPatch.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * width, 
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
	public boolean canDespawn() {
		return false;
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

		if(nbt.hasKey("patch_1")) {
			setPatch1(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_1")));
			setPatch2(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_2")));
			setPatch3(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_3")));
			setPatch4(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_4")));
			setPatch5(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_5")));
			setPatch6(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_6")));
			setPatch7(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_7")));
			setPatch8(NBTUtil.getPosFromTag(nbt.getCompoundTag("patch_8")));
			hasPlacedPatchMiddle = nbt.getBoolean("hasPlacedPatchMiddle");
			hasPlacedPatch1 = nbt.getBoolean("hasPlacedPatch1");
			hasPlacedPatch2 = nbt.getBoolean("hasPlacedPatch2");
			hasPlacedPatch3 = nbt.getBoolean("hasPlacedPatch3");
			hasPlacedPatch4 = nbt.getBoolean("hasPlacedPatch4");
			hasPlacedPatch5 = nbt.getBoolean("hasPlacedPatch5");
			hasPlacedPatch6 = nbt.getBoolean("hasPlacedPatch6");
			hasPlacedPatch7 = nbt.getBoolean("hasPlacedPatch7");
			hasPlacedPatch8 = nbt.getBoolean("hasPlacedPatch8");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if (getIsMiddle()) {
			nbt.setTag("patch_1", NBTUtil.createPosTag(getPatch1()));
			nbt.setTag("patch_2", NBTUtil.createPosTag(getPatch2()));
			nbt.setTag("patch_3", NBTUtil.createPosTag(getPatch3()));
			nbt.setTag("patch_4", NBTUtil.createPosTag(getPatch4()));
			nbt.setTag("patch_5", NBTUtil.createPosTag(getPatch5()));
			nbt.setTag("patch_6", NBTUtil.createPosTag(getPatch6()));
			nbt.setTag("patch_7", NBTUtil.createPosTag(getPatch7()));
			nbt.setTag("patch_8", NBTUtil.createPosTag(getPatch8()));
			nbt.setBoolean("isMiddle", getIsMiddle());
			nbt.setBoolean("hasPlacedPatchMiddle", hasPlacedPatchMiddle);
			nbt.setBoolean("hasPlacedPatch1", hasPlacedPatch1);
			nbt.setBoolean("hasPlacedPatch2", hasPlacedPatch2);
			nbt.setBoolean("hasPlacedPatch3", hasPlacedPatch3);
			nbt.setBoolean("hasPlacedPatch4", hasPlacedPatch4);
			nbt.setBoolean("hasPlacedPatch5", hasPlacedPatch5);
			nbt.setBoolean("hasPlacedPatch6", hasPlacedPatch6);
			nbt.setBoolean("hasPlacedPatch7", hasPlacedPatch7);
			nbt.setBoolean("hasPlacedPatch8", hasPlacedPatch8);
		}
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
