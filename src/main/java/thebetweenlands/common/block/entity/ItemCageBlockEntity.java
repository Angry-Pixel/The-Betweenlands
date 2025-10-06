package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.SwordEnergy;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCageBlockEntity extends SyncedBlockEntity {

	public byte type; // type will be used for each sword part rendering
	public boolean canBreak;

	public ItemCageBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.ITEM_CAGE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, ItemCageBlockEntity entity) {
		if (!level.isClientSide()) {
			if (entity.isBlockOccupied(level, pos, state)) {
				if (!entity.canBreak)
					entity.setCanBeBroken(level, pos, state, true);
			} else {
				if (entity.canBreak)
					entity.setCanBeBroken(level, pos, state, false);
			}
		}
	}

	public void setCanBeBroken(Level level, BlockPos pos, BlockState state, boolean isBreakable) {
		this.canBreak = isBreakable;
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public static void setBlockWithType(Level level, BlockPos pos, BlockState state, int blockType) {
		level.setBlockAndUpdate(pos, state);
		((ItemCageBlockEntity)level.getBlockEntity(pos)).type = (byte) blockType;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	protected boolean isBlockOccupied(Level level, BlockPos pos, BlockState state) {
		List<Player> list = level.getEntitiesOfClass(Player.class, new AABB(pos.getX() + 0.25D, pos.getY() - 3D, pos.getZ() + 0.25D, pos.getX() + 0.75D, pos.getY(), pos.getZ() + 0.75D));
		return !list.isEmpty();
	}

	@Nullable
	public SwordEnergy isSwordEnergyBelow(LevelAccessor level, BlockPos pos, BlockState state) {
		List<SwordEnergy> list = level.getEntitiesOfClass(SwordEnergy.class, new AABB(pos.getX() - 9D, pos.getY() - 2D, pos.getZ() - 9D, pos.getX() + 10D, pos.getY() + 3D, pos.getZ() + 10D));
		if (!list.isEmpty()) {
			return list.getFirst();
		}
		return null;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putByte("type", this.type);
		tag.putBoolean("can_break", this.canBreak);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.type = tag.getByte("type");
		this.canBreak = tag.getBoolean("can_break");
	}
}
