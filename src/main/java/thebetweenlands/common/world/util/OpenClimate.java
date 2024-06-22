package thebetweenlands.common.world.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

public class OpenClimate extends Climate {

	// Climate extentsion alowing for open generator conversions
	public static BlockPos findSpawnPosition(List<ParameterPoint> p_186807_, Sampler p_186808_) {
		return Climate.findSpawnPosition(p_186807_, p_186808_);
	}
}
