package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.herblore.rune.RuneWater;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.RuneCategory;
import thebetweenlands.common.item.herblore.rune.RuneTier;
import thebetweenlands.common.lib.ModInfo;

public class RuneRegistry {
	private RuneRegistry() {}

	public static void preInit() {
		ItemRune.register(RuneCategory.INITIATE, RuneTier.TIER_1, AspectRegistry.BYRGINAZ, new DefaultRuneContainerFactory(new ResourceLocation(ModInfo.ID, "initiate_water"), new RuneWater.Blueprint()));
	}
}
