package thebetweenlands.common.tile;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityMudBrickAlcove extends TileEntityLootInventory {
	public boolean hasUrn, topWeb, bottomWeb, smallCandle, bigCandle, outcrop;
	public int urnType = 0, rotationOffset = 0, dungeonLevel = 0;
	public int facing = 0;

	public TileEntityMudBrickAlcove() {
		super(1, "container.bl.mud_bricks_alcove");
	}

	public void setUpGreeble() {
		Random rand = getWorld().rand;
		if(rand.nextInt(3) == 0)
			hasUrn = true;
		if(hasUrn) {
			urnType = rand.nextInt(3);
			rotationOffset = rand.nextInt(41) - 20;
		}
		topWeb = rand.nextBoolean();
		bottomWeb = rand.nextBoolean();
		smallCandle = rand.nextBoolean();
		bigCandle = rand.nextBoolean();
		outcrop = rand.nextBoolean();
	}

	public void setDungeonLevel(int level) {
		dungeonLevel = level;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		hasUrn = nbt.getBoolean("has_urn");
		urnType = nbt.getInteger("urn_type");
		rotationOffset = nbt.getInteger("rotationOffset");
		topWeb = nbt.getBoolean("top_web");
		bottomWeb = nbt.getBoolean("bottom_web");
		smallCandle = nbt.getBoolean("small_candle");
		bigCandle = nbt.getBoolean("big_candle");
		outcrop = nbt.getBoolean("out_crop");
		dungeonLevel = nbt.getInteger("dungeon_level");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("has_urn", hasUrn);
		nbt.setInteger("urn_type", urnType);
		nbt.setInteger("rotationOffset", this.rotationOffset);
		nbt.setBoolean("top_web", topWeb);
		nbt.setBoolean("bottom_web", bottomWeb);
		nbt.setBoolean("small_candle", smallCandle);
		nbt.setBoolean("big_candle", bigCandle );
		nbt.setBoolean("out_crop", outcrop);
		nbt.setInteger("dungeon_level", dungeonLevel);
		return nbt;
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
		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(pos, state, state, 1);
	}
}
