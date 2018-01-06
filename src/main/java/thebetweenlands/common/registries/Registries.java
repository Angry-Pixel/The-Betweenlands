package thebetweenlands.common.registries;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.serializer.Serializers;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		BlockRegistry.preInit();
		ItemRegistry.preInit();
		EntityRegistry.preInit();
//		BiomeRegistry.preInit();
		SoundRegistry.preInit();
		CapabilityRegistry.preInit();
		StorageRegistry.preInit();
		Serializers.register();
		CustomRecipeRegistry.preInit();
		AdvancementCriterionRegistry.preInit();

		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ModelRegistry.preInit();
			AmbienceRegistry.preInit();
		}
	}

	public void init() {
		//this.blockRegistry.init();
		ItemRegistry.init();
		RecipeRegistry.init();
		TileEntityRegistry.init();
		AspectRegistry.init();
		FluidRegistry.init();
	}
}
