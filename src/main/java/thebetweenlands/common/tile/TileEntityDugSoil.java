package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class TileEntityDugSoil extends TileEntity {
	private int compost = 0;
	private int decay = 0;
	private int purifiedHarvests = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.decay = nbt.getInteger("decay");
		this.compost = nbt.getInteger("compost");
		this.purifiedHarvests = nbt.getInteger("purifiedHarvests");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("compost", this.compost);
		nbt.setInteger("decay", this.decay);
		nbt.setInteger("purifiedHarvests", this.purifiedHarvests);
		return super.writeToNBT(nbt);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("compost", this.compost);
		nbt.setInteger("decay", this.decay);
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		this.decay = nbt.getInteger("decay");
		this.compost = nbt.getInteger("compost");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		//Only recreate/remove if block has changed
		return oldState.getBlock() != newSate.getBlock();
	}

	public void copy(TileEntityDugSoil other) {
		this.setDecay(other.decay);
		this.setCompost(other.compost);
	}

	public void setPurifiedHarvests(int harvests) {
		IBlockState blockState = this.world.getBlockState(this.pos);
		BlockGenericDugSoil soil = ((BlockGenericDugSoil)blockState.getBlock());
		if(soil.isPurified(this.world, this.pos, blockState)) {
			if(harvests < 0) {
				harvests = 0;
			}
			int maxHarvests = soil.getPurifiedHarvests(this.world, this.pos, blockState);
			this.purifiedHarvests = harvests;
			if(this.purifiedHarvests >= maxHarvests) {
				this.world.setBlockState(this.pos, soil.getUnpurifiedDugSoil(this.world, this.pos, blockState), 3);
				BlockGenericDugSoil.copy(this.world, this.pos, this);
			}
		} else {
			this.purifiedHarvests = 0;
		}
		this.markDirty();
	}

	public int getPurifiedHarvests() {
		return this.purifiedHarvests;
	}

	public void setCompost(int compost) {
		if(compost < 0) {
			compost = 0;
		}
		boolean wasComposted = this.isComposted();
		this.compost = compost;
		if(wasComposted != this.isComposted()) {
			IBlockState blockState = this.world.getBlockState(this.pos);
			if(!this.isFullyDecayed()) {
				this.world.setBlockState(this.pos, blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, this.isComposted()), 3);
			} else {
				this.world.setBlockState(this.pos, blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, false), 3);
			}
		} else {
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 3);
		}
		this.markDirty();
	}

	public int getCompost() {
		return this.compost;
	}

	public boolean isComposted() {
		return this.compost > 0;
	}

	public void setDecay(int decay) {
		if(decay < 0) {
			decay = 0;
		}
		boolean wasDecayed = this.isFullyDecayed();
		this.decay = Math.min(20, decay);
		if(wasDecayed != this.isFullyDecayed()) {
			IBlockState blockState = this.world.getBlockState(this.pos);
			if(this.isFullyDecayed()) {
				blockState = blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, false);
			} else {
				blockState = blockState.withProperty(BlockGenericDugSoil.DECAYED, false).withProperty(BlockGenericDugSoil.COMPOSTED, this.isComposted());
			}
			this.world.setBlockState(this.pos, blockState, 3);

			IBlockState blockUp = this.world.getBlockState(this.pos.up());
			if(blockUp.getBlock() instanceof BlockGenericCrop) {
				BlockPos pos = this.pos.up();
				for(int i = 0; i < ((BlockGenericCrop)blockUp.getBlock()).getMaxHeight(); i++) {
					IBlockState cropBlockState = this.world.getBlockState(pos);
					if(cropBlockState.getBlock() instanceof BlockGenericCrop) {
						if(this.isFullyDecayed()) {
							this.world.setBlockState(pos, cropBlockState.withProperty(BlockGenericCrop.DECAYED, true), 3);
						} else {
							this.world.setBlockState(pos, cropBlockState.withProperty(BlockGenericCrop.DECAYED, false), 3);
						}
					} else {
						break;
					}
					pos = pos.up();
				}
			}
		} else {
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 3);
		}
		this.markDirty();
	}

	public int getDecay() {
		return this.decay;
	}

	public boolean isFullyDecayed() {
		return this.decay >= 20;
	}
}
