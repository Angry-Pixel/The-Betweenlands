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
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntitySpikeTrap extends TileEntity implements ITickable {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	@Override
	public void update() {
		if (!world.isRemote) {
			IBlockState stateUp = world.getBlockState(pos.up());
			if (stateUp.getBlock() != Blocks.AIR && stateUp.getBlockHardness(world, pos.up()) >= 0.0F) {
				setType((byte) 1);
				setActive(true);
				Block block = stateUp.getBlock();
				world.playEvent(null, 2001, pos.up(), Block.getIdFromBlock(block));
				block.dropBlockAsItem(world, pos.up(), world.getBlockState(pos.up()), 0);
				world.setBlockToAir(pos.up());
			}
			IBlockState stateUp2 = world.getBlockState(pos.up(2));
			if (stateUp2.getBlock() != Blocks.AIR && stateUp2.getBlockHardness(world, pos.up(2)) >= 0.0F) {
				setType((byte) 1);
				setActive(true);
				Block block = stateUp2.getBlock();
				world.playEvent(null, 2001, pos.up(2), Block.getIdFromBlock(block));
				block.dropBlockAsItem(world, pos.up(2), world.getBlockState(pos.up(2)), 0);
				world.setBlockToAir(pos.up(2));
			}
			if (world.rand.nextInt(500) == 0) {
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
				world.playSound(null, (double) pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundRegistry.SPIKE, SoundCategory.BLOCKS, 1.25F, 1.0F);
			if (animationTicks <= 20)
				animationTicks += 4;
			if (animationTicks == 20 && !this.world.isRemote)
				setActive(false);
		}
		if (!active)
			if (animationTicks >= 1)
				animationTicks--;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	public void setType(byte blockType) {
		type = blockType;
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	protected Entity activateBlock() {
		float y = 1F / 16 * animationTicks;
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1D + y, pos.getZ() + 1D));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					//if (entity instanceof EntityPlayer)
						((EntityLivingBase) entity).attackEntityFrom(DamageSource.GENERIC, 2);
			}
		return null;
	}

	protected Entity isBlockOccupied() {
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + 0.25D, pos.getY(), pos.getZ() + 0.25D, pos.getX() + 0.75D, pos.getY() + 2D, pos.getZ() + 0.75D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				//if (entity instanceof EntityPlayer)
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
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

}