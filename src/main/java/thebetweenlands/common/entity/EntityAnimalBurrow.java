package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.api.block.ICritterBurrowEnabled;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.terrain.BlockAnimalBurrow;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityAnimalBurrow extends EntityCreature implements IEntityBL {

	private static final DataParameter<ItemStack> BURROW_ITEM = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Integer> RESTING_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> RECALL_COOLDOWN = EntityDataManager.createKey(EntityAnimalBurrow.class, DataSerializers.VARINT);

	public EntityAnimalBurrow (World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(BURROW_ITEM, ItemStack.EMPTY);
		dataManager.register(RESTING_COOLDOWN, 100);
		dataManager.register(RECALL_COOLDOWN, 0);
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

		if(!world.isRemote) {
			if (world.isAirBlock(getPosition()))
				setDead();

			if(getBurrowItem().isEmpty() && world.getTotalWorldTime()%20 == 0 && getRecallTimer() <= 0) {
				lureCloseEntity();
				if(checkCatch() != null) {
					ItemStack itemMob = ItemRegistry.CRITTER.capture(checkCatch());
					checkCatch().setDead();
					setBurrowItem(itemMob);
					setRestingTimer(120);
				}
			}

			if(!getBurrowItem().isEmpty() && getRestingTimer() <= 0) {
				if(getEntity() != null) {
					world.spawnEntity(getEntity());
					setBurrowItem(ItemStack.EMPTY);
					setRecallTimer(360);
				}
			}

			if(getBurrowItem().isEmpty() && getRecallTimer() > 0)
				setRecallTimer(getRecallTimer() - 1);

			if(!getBurrowItem().isEmpty() && getRestingTimer() > 0)
				setRestingTimer(getRestingTimer() - 1);
		}
	}

	private EntityLiving checkCatch() {
		IBlockState state = NBTUtil.readBlockState((NBTTagCompound) getEntityData().getTag("tempBlockTypes"));
		Class entity = ICritterBurrowEnabled.getEntityForBlockType(world, state.getBlock());
		EntityLiving mob = null;
		List<EntityLiving> list = getEntityWorld().getEntitiesWithinAABB(entity, new AxisAlignedBB(getBurrowEntrancePos()));
		if (!list.isEmpty())
			mob = list.get(0);
		return mob;
	}

	@SuppressWarnings("unchecked")
	public void lureCloseEntity() {
		IBlockState state = NBTUtil.readBlockState((NBTTagCompound) getEntityData().getTag("tempBlockTypes"));
		Class entity = ICritterBurrowEnabled.getEntityForBlockType(world, state.getBlock());
		List<EntityLiving> list = getEntityWorld().getEntitiesWithinAABB(entity, extendRangeBox());
		if (list.isEmpty())
			return;
		if (!list.isEmpty()) {
			Collections.shuffle(list);
			EntityLiving mob = list.get(0);
			mob.getNavigator().tryMoveToXYZ(getBurrowEntrancePos().getX(), getBurrowEntrancePos().getY() + 0.5D, getBurrowEntrancePos().getZ(), 1D);
		}
	}

	public AxisAlignedBB extendRangeBox() {
		return new AxisAlignedBB(getPosition()).grow(8D, 1D, 8D);
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

	public void setBurrowItem(ItemStack itemStack) {
		dataManager.set(BURROW_ITEM, itemStack);
	}

	public ItemStack getBurrowItem() {
		return dataManager.get(BURROW_ITEM);
	}

	public void setRestingTimer(int resting) {
		dataManager.set(RESTING_COOLDOWN, resting);
	}

	public int getRestingTimer() {
		return dataManager.get(RESTING_COOLDOWN);
	}

	public void setRecallTimer(int recall) {
		dataManager.set(RECALL_COOLDOWN, recall);
	}

	public int getRecallTimer() {
		return dataManager.get(RECALL_COOLDOWN);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (BURROW_ITEM.equals(key))
			setBurrowItem(getBurrowItem());
		super.notifyDataManagerChange(key);
	}

	public BlockPos getBurrowEntrancePos() {
		BlockPos entrancePos = getPosition();
		if (world.getBlockState(getPosition()).getBlock() instanceof BlockAnimalBurrow) {
			EnumFacing burrowFacing = world.getBlockState(getPosition()).getValue(BlockAnimalBurrow.FACING);
			entrancePos = getPosition().offset(burrowFacing);
		}
		return entrancePos;
	}

	public Entity getEntity() {
		ItemStack stack = this.getBurrowItem();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemMob && ((ItemMob) stack.getItem()).hasEntityData(stack))
			return ((ItemMob) stack.getItem()).createCapturedEntity(this.world, getBurrowEntrancePos().getX() + 0.5D, getBurrowEntrancePos().getY(), getBurrowEntrancePos().getZ() + 0.5D, stack);
		return null;
	}

    @Override
    public boolean getCanSpawnHere() {
    	BlockPos pos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ));
    	IBlockState state = world.getBlockState(pos);
        return ICritterBurrowEnabled.isSuitableBurrowBlock(state.getBlock()) && hasAccess(world, pos);
    }

	private boolean hasAccess(World world, BlockPos pos) {
		List<BlockPos> list = new ArrayList();
		for (EnumFacing facing : EnumFacing.values())
			if (world.isAirBlock(pos.offset(facing)))
				if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
					list.add(pos.offset(facing));
				}
		if (!list.isEmpty())
			return true;
		return false;
	}

	private EnumFacing accessFacing(World world, BlockPos pos) {
		// TODO make alt ones for underwater stuffs :) if(isUnderwaterType);
		List<EnumFacing> list = new ArrayList();
		for (EnumFacing facing : EnumFacing.values())
			if (world.isAirBlock(pos.offset(facing)))
				if (facing != EnumFacing.UP && facing != EnumFacing.DOWN)
					list.add(facing);
		if (!list.isEmpty()) {
			Collections.shuffle(list);
			return list.get(0);
		}
		return EnumFacing.UP;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			getOriginBlocks(getEntityWorld(), getPosition());
			IBlockState state = world.getBlockState(getPosition());
			if(ICritterBurrowEnabled.isSuitableBurrowBlock(state.getBlock())) {
				IBlockState burrow = ICritterBurrowEnabled.getBurrowBlock(state.getBlock());
				Class entity = ICritterBurrowEnabled.getEntityForBlockType(world, state.getBlock());
				setBurrowItem(ItemRegistry.CRITTER.capture(entity));
				world.setBlockState(getPosition(), burrow.withProperty(BlockAnimalBurrow.FACING, accessFacing(world, getPosition())), 3);
			}
		}
		return livingdata;
	}

	@Override
    public void setDead() {
		if(!getEntityWorld().isRemote) {
			if(getEntityData().hasKey("tempBlockTypes"))
				loadOriginBlocks(getEntityWorld(), getEntityData());
		}
        super.setDead();
    }

	private void getOriginBlocks(World world, BlockPos pos) {
		NBTTagCompound entityNbt = getEntityData();
		IBlockState state = world.getBlockState(pos);
		NBTTagCompound tag = new NBTTagCompound();
		NBTUtil.writeBlockState(tag, state);
		entityNbt.setTag("tempBlockTypes", tag);
		entityNbt.setTag("originPos", NBTUtil.createPosTag(pos));
		writeEntityToNBT(entityNbt);
	}

	public void loadOriginBlocks(World world, NBTTagCompound tag) {
		NBTTagCompound entityNbt = getEntityData();
		BlockPos origin = NBTUtil.getPosFromTag(entityNbt.getCompoundTag("originPos"));
		IBlockState state = NBTUtil.readBlockState((NBTTagCompound) entityNbt.getTag("tempBlockTypes"));
		world.setBlockState(origin, state, 3);
		getEntityWorld().playSound((EntityPlayer)null, origin, SoundRegistry.ROOF_COLLAPSE, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		NBTTagCompound burrowItem = (NBTTagCompound) nbt.getTag("burrowItem");
		ItemStack stackBurrow = ItemStack.EMPTY;
		if(!burrowItem.isEmpty())
			stackBurrow = new ItemStack(burrowItem);
		setBurrowItem(stackBurrow);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		NBTTagCompound burrowItem = new NBTTagCompound();
		getBurrowItem().writeToNBT(burrowItem);
		nbt.setTag("burrowItem", burrowItem);
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
