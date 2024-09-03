package thebetweenlands.common.block;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.block.entity.WeedwoodCraftingTableBlockEntity;

public class WeedwoodCraftingTableBlock extends BaseEntityBlock {

    public static final MapCodec<WeedwoodCraftingTableBlock> CODEC = simpleCodec(WeedwoodCraftingTableBlock::new);

	public WeedwoodCraftingTableBlock(Properties properties) {
		super(properties);
	}

    @Override
    public MapCodec<? extends WeedwoodCraftingTableBlock> codec() {
        return CODEC;
    }

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof WeedwoodCraftingTableBlockEntity table && placer != null) {
			table.rotation = (byte) (((Mth.floor((double) (placer.getYRot() * 4.0F / 360.0F) + 0.5D) & 3) + 1) % 4);
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof WeedwoodCraftingTableBlockEntity table) {
				player.openMenu(table);
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, moving);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WeedwoodCraftingTableBlockEntity(pos, state);
	}
}
