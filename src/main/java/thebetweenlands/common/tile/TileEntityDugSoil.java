package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class TileEntityDugSoil extends TileEntity {
	private int compost = 0;
	private int decay = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.decay = nbt.getInteger("decay");
		this.compost = nbt.getInteger("compost");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("compost", this.compost);
		nbt.setInteger("decay", this.decay);
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

	public void setCompost(int compost) {
		boolean wasComposted = this.isComposted();
		this.compost = compost;
		if(wasComposted != this.isComposted()) {
			IBlockState blockState = this.worldObj.getBlockState(this.pos);
			if(!this.isFullyDecayed()) {
				this.worldObj.setBlockState(this.pos, blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, this.isComposted()));
			} else {
				this.worldObj.setBlockState(this.pos, blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, false));
			}
		}
	}

	public int getCompost() {
		return this.compost;
	}

	public boolean isComposted() {
		return this.compost > 0;
	}

	public void setDecay(int decay) {
		boolean wasDecayed = this.isFullyDecayed();
		this.decay = decay;
		if(wasDecayed != this.isFullyDecayed()) {
			IBlockState blockState = this.worldObj.getBlockState(this.pos);
			if(this.isFullyDecayed()) {
				blockState = blockState.withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).withProperty(BlockGenericDugSoil.COMPOSTED, false);
			} else {
				blockState = blockState.withProperty(BlockGenericDugSoil.DECAYED, false).withProperty(BlockGenericDugSoil.COMPOSTED, this.isComposted());
			}
			this.worldObj.setBlockState(this.pos, blockState);

			/*IBlockState blockUp = this.worldObj.getBlockState(this.pos.up());
			if(blockUp.getBlock() instanceof BlockGenericCrop) {
				BlockPos pos = this.pos.up();
				for(int i = 0; i < ((BlockGenericCrop)blockUp.getBlock()).getMaxHeight(); i++) {
					IBlockState cropBlockState = this.worldObj.getBlockState(pos);
					if(cropBlockState.getBlock() instanceof BlockGenericCrop) {
						if(wasDecayed) {
							this.worldObj.setBlockState(pos, cropBlockState.withProperty(BlockGenericCrop.DECAYED, false));
						} else {
							this.worldObj.setBlockState(pos, cropBlockState.withProperty(BlockGenericCrop.DECAYED, true));
						}
					} else {
						break;
					}
					pos = pos.up();
				}
			}*/
		}
	}

	public int getDecay() {
		return this.decay;
	}

	public boolean isFullyDecayed() {
		return this.decay >= 20;
	}
}
