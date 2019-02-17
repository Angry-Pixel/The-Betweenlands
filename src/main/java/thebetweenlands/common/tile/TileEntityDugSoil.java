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
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityDugSoil extends TileEntity {
	private int compost = 0;
	private int decay = 0;
	private int purifiedHarvests = 0;

	public TileEntityDugSoil() {
		super(TileEntityRegistry.DUG_SOIL);
	}
	
	@Override
	public void read(NBTTagCompound nbt) {
		super.read(nbt);
		this.decay = nbt.getInt("decay");
		this.compost = nbt.getInt("compost");
		this.purifiedHarvests = nbt.getInt("purifiedHarvests");
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt) {
		nbt.setInt("compost", this.compost);
		nbt.setInt("decay", this.decay);
		nbt.setInt("purifiedHarvests", this.purifiedHarvests);
		return super.write(nbt);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInt("compost", this.compost);
		nbt.setInt("decay", this.decay);
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		this.decay = nbt.getInt("decay");
		this.compost = nbt.getInt("compost");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.write(new NBTTagCompound());
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
				this.world.setBlockState(this.pos, blockState.with(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).with(BlockGenericDugSoil.COMPOSTED, this.isComposted()), 3);
			} else {
				this.world.setBlockState(this.pos, blockState.with(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).with(BlockGenericDugSoil.COMPOSTED, false), 3);
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
		this.decay = decay;
		if(wasDecayed != this.isFullyDecayed()) {
			IBlockState blockState = this.world.getBlockState(this.pos);
			if(this.isFullyDecayed()) {
				blockState = blockState.with(BlockGenericDugSoil.DECAYED, this.isFullyDecayed()).with(BlockGenericDugSoil.COMPOSTED, false);
			} else {
				blockState = blockState.with(BlockGenericDugSoil.DECAYED, false).with(BlockGenericDugSoil.COMPOSTED, this.isComposted());
			}
			this.world.setBlockState(this.pos, blockState, 3);

			IBlockState blockUp = this.world.getBlockState(this.pos.up());
			if(blockUp.getBlock() instanceof BlockGenericCrop) {
				BlockPos pos = this.pos.up();
				for(int i = 0; i < ((BlockGenericCrop)blockUp.getBlock()).getMaxHeight(); i++) {
					IBlockState cropBlockState = this.world.getBlockState(pos);
					if(cropBlockState.getBlock() instanceof BlockGenericCrop) {
						if(this.isFullyDecayed()) {
							this.world.setBlockState(pos, cropBlockState.with(BlockGenericCrop.DECAYED, true), 3);
						} else {
							this.world.setBlockState(pos, cropBlockState.with(BlockGenericCrop.DECAYED, false), 3);
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
