package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.feature.WeedwoodBushFeature;

public class FeatureRegistry {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TheBetweenlands.ID);

	public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEEDWOOD_BUSH = FEATURES.register("weedwood_bush",
		() -> new WeedwoodBushFeature(NoneFeatureConfiguration.CODEC));
}
