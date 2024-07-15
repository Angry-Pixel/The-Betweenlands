package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.WinterEvent;

import javax.annotation.Nullable;

public class BlackIceBlock extends HalfTransparentBlock {
	public BlackIceBlock(Properties properties) {
		super(properties);
	}

	public static BlockState meltsInto() {
		return BlockRegistry.SWAMP_WATER.get().defaultBlockState();
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		super.playerDestroy(level, player, pos, state, te, stack);
		if (!EnchantmentHelper.hasTag(stack, EnchantmentTags.PREVENTS_ICE_MELTING)) {
			if (level.dimensionType().ultraWarm()) {
				level.removeBlock(pos, false);
				return;
			}

			BlockState blockstate = level.getBlockState(pos.below());
			if (blockstate.blocksMotion() || blockstate.liquid()) {
				level.setBlockAndUpdate(pos, meltsInto());
			}
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!WinterEvent.isFroooosty(level) || level.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightBlock(level, pos)) {
			this.melt(state, level, pos);
		}
	}

	protected void melt(BlockState state, Level level, BlockPos pos) {
		if (level.dimensionType().ultraWarm()) {
			level.removeBlock(pos, false);
		} else {
			level.setBlockAndUpdate(pos, meltsInto());
			level.neighborChanged(pos, meltsInto().getBlock(), pos);
		}
	}

}
