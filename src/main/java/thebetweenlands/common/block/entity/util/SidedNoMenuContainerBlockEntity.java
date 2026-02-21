package thebetweenlands.common.block.entity.util;

import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public abstract class SidedNoMenuContainerBlockEntity extends NoMenuContainerBlockEntity implements WorldlyContainer {
	public static final int[] NO_SLOTS = IntArrays.EMPTY_ARRAY;
	
	public SidedNoMenuContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// Force implementors to think about automation
	@Override
	public abstract int[] getSlotsForFace(Direction side);
	
	@Override
	public abstract boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction);

	@Override
	public abstract boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction);
	
	public static int[] allSlots(NoMenuContainerBlockEntity blockEntity) {
		return allSlots(Math.max(blockEntity.getContainerSize(), blockEntity.getItems().size()));
	}

	public static int[] allSlots(int containerSize) {
		int[] slots = new int[containerSize];
		for(int i = 0; i < containerSize; ++i) {
			slots[i] = i;
		}
		return slots;
	}

	public static int[] slotsBetween(int minSlotInclusive, int maxSlotExclusive) {
		return IntStream.range(minSlotInclusive, minSlotInclusive).toArray();
	}
	
	public static int[] slotsBetweenInclusive(int minSlotInclusive, int maxSlotInclusive) {
		return IntStream.rangeClosed(minSlotInclusive, minSlotInclusive).toArray();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return true;
	}
	
	@Override
	public IItemHandler getItemHandlerCapability(Direction context) {
		return new SidedInvWrapper(this, context);
	}
}
