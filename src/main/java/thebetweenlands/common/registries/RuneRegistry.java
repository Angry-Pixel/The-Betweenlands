package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.RuneCategory;
import thebetweenlands.api.rune.RuneTier;
import thebetweenlands.common.herblore.rune.RuneFire;
import thebetweenlands.common.herblore.rune.RuneWater;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.properties.DefaultRuneItemProperties;
import thebetweenlands.common.item.herblore.rune.properties.PatternRuneItemProperties;
import thebetweenlands.common.lib.ModInfo;

public class RuneRegistry {
	private RuneRegistry() {}

	public static void preInit() {
		ItemRune.register(RuneCategory.INITIATE, RuneTier.TIER_1, AspectRegistry.BYRGINAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "initiate_water"), new RuneWater.Blueprint()));
		ItemRune.register(RuneCategory.TOKEN, RuneTier.TIER_1, AspectRegistry.CELAWYNN, new PatternRuneItemProperties(new ResourceLocation(ModInfo.ID, "pattern")));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.FIRNALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "fire"), new RuneFire.Blueprint()));
	}
}
