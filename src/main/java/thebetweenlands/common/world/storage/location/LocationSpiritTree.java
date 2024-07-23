package thebetweenlands.common.world.storage.location;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationSpiritTree extends LocationGuarded {
	private final List<BlockPos> notGeneratedWispPositions = new ArrayList<>();
	private final List<BlockPos> generatedWispPositions = new ArrayList<>();

	private final List<BlockPos> largeFacePositions = new ArrayList<>();
	private final List<BlockPos> smallFacePositions = new ArrayList<>();

	public LocationSpiritTree(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "spirit_tree", EnumLocationType.SPIRIT_TREE);
	}

	public void addLargeFacePosition(BlockPos pos) {
		this.largeFacePositions.add(pos);
		this.setDirty(true);
	}

	public void addSmallFacePosition(BlockPos pos) {
		this.smallFacePositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getLargeFacePositions() {
		return Collections.unmodifiableList(this.largeFacePositions);
	}

	public List<BlockPos> getSmallFacePositions() {
		return Collections.unmodifiableList(this.smallFacePositions);
	}

	public void addGeneratedWispPosition(BlockPos pos) {
		this.generatedWispPositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getGeneratedWispPositions() {
		return Collections.unmodifiableList(this.generatedWispPositions);
	}

	public void addNotGeneratedWispPosition(BlockPos pos) {
		this.notGeneratedWispPositions.add(pos);
		this.setDirty(true);
	}

	public List<BlockPos> getNotGeneratedWispPositions() {
		return Collections.unmodifiableList(this.notGeneratedWispPositions);
	}

	public int getActiveWisps(Level level) {
		int i = 0;
		for (BlockPos pos : this.notGeneratedWispPositions) {
			if (level.getBlockState(pos).is(BlockRegistry.WISP.get())) {
				i++;
			}
		}
		for (BlockPos pos : this.generatedWispPositions) {
			if (level.getBlockState(pos).is(BlockRegistry.WISP.get())) {
				i++;
			}
		}
		return i;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		this.saveBlockList(nbt, "generatedWispPositions", this.generatedWispPositions);
		this.saveBlockList(nbt, "notGeneratedWispPositions", this.notGeneratedWispPositions);
		this.saveBlockList(nbt, "largeFacePositions", this.largeFacePositions);
		this.saveBlockList(nbt, "smallFacePositions", this.smallFacePositions);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.readBlockList(nbt, "generatedWispPositions", this.generatedWispPositions);
		this.readBlockList(nbt, "notGeneratedWispPositions", this.notGeneratedWispPositions);
		this.readBlockList(nbt, "largeFacePositions", this.largeFacePositions);
		this.readBlockList(nbt, "smallFacePositions", this.smallFacePositions);
	}

	protected void saveBlockList(CompoundTag nbt, String name, List<BlockPos> blocks) {
		ListTag blockList = new ListTag();
		for (BlockPos pos : blocks) {
			blockList.add(LongTag.valueOf(pos.asLong()));
		}
		nbt.put(name, blockList);
	}

	protected void readBlockList(CompoundTag nbt, String name, List<BlockPos> blocks) {
		blocks.clear();
		ListTag blockList = nbt.getList(name, Tag.TAG_LONG);
		for (Tag tag : blockList) {
			LongTag posNbt = (LongTag) tag;
			blocks.add(BlockPos.of(posNbt.getAsLong()));
		}
	}
}
