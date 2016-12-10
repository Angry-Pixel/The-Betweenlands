package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class TileEntityDugSoil extends TileEntity {
	private int compost = 0;
	private int decay = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.compost = nbt.getInteger("compost");
		this.decay = nbt.getInteger("decay");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("compost", this.compost);
		nbt.setInteger("decay", this.decay);
		return super.writeToNBT(nbt);
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
			this.worldObj.setBlockState(this.pos, this.worldObj.getBlockState(this.pos).withProperty(BlockGenericDugSoil.COMPOSTED, this.isComposted()));
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
			this.worldObj.setBlockState(this.pos, this.worldObj.getBlockState(this.pos).withProperty(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()));
		}
	}

	public int getDecay() {
		return this.decay;
	}

	public boolean isFullyDecayed() {
		return this.decay >= 20;
	}
}
