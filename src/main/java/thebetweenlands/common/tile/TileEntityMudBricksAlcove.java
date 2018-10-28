package thebetweenlands.common.tile;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityMudBricksAlcove extends TileEntityLootInventory {
	public boolean greebled, top_web, bottom_web, small_candle, big_candle, out_crop;

	public TileEntityMudBricksAlcove() {
		super(1, "container.mud_bricks_alcove");
	}

	public void setUpGreeble() {
		Random rand = getWorld().rand;
		if(!greebled) {
			top_web = rand.nextBoolean();
			bottom_web = rand.nextBoolean();
			small_candle = rand.nextBoolean();
			big_candle = rand.nextBoolean();
			out_crop = rand.nextBoolean();
			greebled = true;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		greebled = nbt.getBoolean("greebled");
		top_web = nbt.getBoolean("top_web");
		bottom_web = nbt.getBoolean("bottom_web");
		small_candle = nbt.getBoolean("small_candle");
		big_candle = nbt.getBoolean("big_candle");
		out_crop = nbt.getBoolean("out_crop");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("greebled", greebled);
		nbt.setBoolean("top_web", top_web);
		nbt.setBoolean("bottom_web", bottom_web);
		nbt.setBoolean("small_candle", small_candle);
		nbt.setBoolean("big_candle", big_candle );
		nbt.setBoolean("out_crop", out_crop);
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
	}
}
