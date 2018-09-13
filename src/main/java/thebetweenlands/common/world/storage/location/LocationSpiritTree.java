package thebetweenlands.common.world.storage.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.registries.BlockRegistry;

public class LocationSpiritTree extends LocationGuarded {
	private List<BlockPos> wisps = new ArrayList<BlockPos>();

	public LocationSpiritTree(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "spirit_tree", EnumLocationType.SPIRIT_TREE);
	}

	public void addWisp(BlockPos pos) {
		this.wisps.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getWisps() {
		return Collections.unmodifiableList(this.wisps);
	}

	public int getActiveWisps() {
		int i = 0;
		for(BlockPos pos : this.wisps) {
			if(this.getWorldStorage().getWorld().getBlockState(pos).getBlock() == BlockRegistry.WISP) {
				i++;
			}
		}
		return i;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		this.saveBlockList(nbt, "wisps", this.wisps);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readBlockList(nbt, "wisps", this.wisps);
	}

	protected void saveBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		NBTTagList blockList = new NBTTagList();
		for(BlockPos pos : blocks) {
			blockList.appendTag(new NBTTagLong(pos.toLong()));
		}
		nbt.setTag(name, blockList);
	}

	protected void readBlockList(NBTTagCompound nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		NBTTagList blockList = nbt.getTagList(name, Constants.NBT.TAG_LONG);
		for(int i = 0; i < blockList.tagCount(); i++) {
			NBTTagLong posNbt = (NBTTagLong) blockList.get(i);
			blocks.add(BlockPos.fromLong(posNbt.getLong()));
		}
	}
}
