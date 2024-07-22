package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.FishTrimmingTableBlockEntity;
import thebetweenlands.common.block.entity.FishingTackleBoxBlockEntity;
import thebetweenlands.common.items.AnadiaMobItem;

import java.util.List;

public class FishTrimmingTableBlock extends HorizontalBaseEntityBlock {
	public FishTrimmingTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.bl.fish_trimming_table"));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FishTrimmingTableBlockEntity table) {
			player.openMenu(table);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) == 0 && level.getBlockEntity(pos) instanceof FishTrimmingTableBlockEntity table) {
			if (table.getItem(0).getItem() instanceof AnadiaMobItem mob && mob.isRotten(level, table.getItem(0))) {
//				BLParticles.FLY.spawn(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FishingTackleBoxBlockEntity(pos, state);
	}
}
