package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.block.misc.BlockSludge;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntitySpikeTrap extends TileEntity implements ITickable {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	@Override
	public void update() {
		if (!getWorld().isRemote) {
			IBlockState state = getWorld().getBlockState(getPos());
			EnumFacing facing = state.getValue(BlockSpikeTrap.FACING);

			IBlockState stateFacing = getWorld().getBlockState(getPos().offset(facing, 1));
			if (stateFacing.getBlock() != Blocks.AIR && stateFacing.getBlockHardness(getWorld(), getPos().offset(facing, 1)) >= 0.0F && !(stateFacing.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing.getBlock();
				getWorld().playEvent(null, 2001, getPos().offset(facing, 1), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getWorld(), getPos().offset(facing, 1), getWorld().getBlockState(getPos().offset(facing, 1)), 0);
				getWorld().setBlockToAir(getPos().offset(facing, 1));
			}
			IBlockState stateFacing2 = getWorld().getBlockState(getPos().offset(facing, 2));
			if (stateFacing2.getBlock() != Blocks.AIR && stateFacing2.getBlockHardness(getWorld(), getPos().offset(facing, 2)) >= 0.0F && !(stateFacing2.getBlock() instanceof BlockSludge)) {
				setType((byte) 1);
				setActive(true);
				Block block = stateFacing2.getBlock();
				getWorld().playEvent(null, 2001, getPos().offset(facing, 2), Block.getIdFromBlock(block));
				block.dropBlockAsItem(getWorld(), getPos().offset(facing, 2), getWorld().getBlockState(getPos().offset(facing, 2)), 0);
				getWorld().setBlockToAir(getPos().offset(facing, 2));
			}
			if (getWorld().rand.nextInt(500) == 0) {
				if (type != 0 && !active && animationTicks == 0)
					setType((byte) 0);
				else if (isBlockOccupied() == null)
					setType((byte) 1);
			}

			if (isBlockOccupied() != null && type != 0)
				if(!active && animationTicks == 0)
					setActive(true);

		}
		this.prevAnimationTicks = this.animationTicks;
		if (active) {
			activateBlock();
			if (animationTicks == 0)
				getWorld().playSound(null, (double) getPos().getX(), (double)getPos().getY(), (double)getPos().getZ(), SoundRegistry.SPIKE, SoundCategory.BLOCKS, 1.25F, 1.0F);
			if (animationTicks <= 20)
				animationTicks += 4;
			if (animationTicks == 20 && !this.getWorld().isRemote)
				setActive(false);
		}
		if (!active)
			if (animationTicks >= 1)
				animationTicks--;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 2);
	}

	public void setType(byte blockType) {
		type = blockType;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 2);
	}

	protected Entity activateBlock() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getPos().offset(facing, 1);
		List<EntityLivingBase> list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(hitArea));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (!(entity instanceof IEntityBL))
						((EntityLivingBase) entity).attackEntityFrom(DamageSource.GENERIC, 2);
			}
		return null;
	}

	protected Entity isBlockOccupied() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockSpikeTrap.FACING);
		BlockPos hitArea = getPos().offset(facing , 1);
		List<EntityLivingBase> list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(hitArea).shrink(0.25D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (!(entity instanceof IEntityBL))
					return entity;
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicks", animationTicks);
		nbt.setBoolean("active", active);
		nbt.setByte("type", type);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getInteger("animationTicks");
		active = nbt.getBoolean("active");
		type = nbt.getByte("type");
	}

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
        return writeToNBT(nbt);
    }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}