package thebetweenlands.common.registries;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

//TODO 1.13 Remove this
//Registries should just directly use the registry events where possible
public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		CapabilityRegistry.preInit();
		StorageRegistry.preInit();
		CustomRecipeRegistry.preInit();
		AdvancementCriterionRegistry.preInit();
		LootTableRegistry.preInit();

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			ModelRegistry.preInit();
			AmbienceRegistry.preInit();
		});
	}

	public void init() {
		AspectRegistry.init();
		FluidRegistry.init();
	}
}
