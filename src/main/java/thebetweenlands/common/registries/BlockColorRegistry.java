package thebetweenlands.common.registries;

import net.minecraft.client.color.block.BlockColor;
import thebetweenlands.client.color.BetweenlandsFoligeBlockColors;
import thebetweenlands.client.color.BetweenlandsGrassBlockColors;
import thebetweenlands.client.color.BetweenlandsSwampWaterColors;

public class BlockColorRegistry {

	// List of tint indexes
	public static final BlockColor SWAMP_GRASS = new BetweenlandsGrassBlockColors();
	public static final BlockColor SWAMP_FOLIGE = new BetweenlandsFoligeBlockColors();
	public static final BlockColor SWAMP_WATER = new BetweenlandsSwampWaterColors();
	public static final BlockColor SWAMP_MOSS = new BetweenlandsGrassBlockColors();

	public static void register() {
	}
}
