package thebetweenlands.common.registries;

import net.minecraft.client.color.block.BlockColor;
import thebetweenlands.common.colors.BetweenlandsFoligeBlockColors;
import thebetweenlands.common.colors.BetweenlandsGrassBlockColors;
import thebetweenlands.common.colors.BetweenlandsSwampWaterColors;

public class BlockColorRegistry {

	// List of tint indexes
	public static final BlockColor SWAMP_GRASS = new BetweenlandsGrassBlockColors();
	public static final BlockColor SWAMP_FOLIGE = new BetweenlandsFoligeBlockColors();
	public static final BlockColor SWAMP_WATER = new BetweenlandsSwampWaterColors();
	public static final BlockColor SWAMP_MOSS = new BetweenlandsGrassBlockColors();

	public static void register() {
	}
}
