package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.util.BLDyeColor;

import javax.annotation.Nullable;

public class StainedFilteredSiltGlassBlock extends FilteredSiltGlassBlock {

	private final BLDyeColor color;

	public StainedFilteredSiltGlassBlock(BLDyeColor color, Properties properties) {
		super(properties);
		this.color = color;
	}

	@Override
	public @Nullable Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		return this.color.getColorValue();
	}
}
