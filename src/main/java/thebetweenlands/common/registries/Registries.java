package thebetweenlands.common.registries;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		BlockRegistry.preInit();
		ItemRegistry.preInit();
		EntityRegistry.preInit();
		SoundRegistry.preInit();
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
		//this.blockRegistry.init();
		TileEntityRegistry.init();
		AspectRegistry.init();
		FluidRegistry.init();
	}
}
