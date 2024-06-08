package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.OptionalLong;

import io.netty.bootstrap.BootstrapConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.dimension.BetweenlandsDimension;
import thebetweenlands.common.dimension.BetweenlandsDimensionType;
import thebetweenlands.common.dimension.BetweenlandsLevelData;
import thebetweenlands.common.dimension.BetweenlandsSpecialEffects;
import thebetweenlands.common.world.BetweenlandsLevel;
import thebetweenlands.common.world.BetweenlandsServerLevel;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;

// NOTE: Dimensions will be set here in newer versions (1.19+) as in 1.18 there is no method to do this without a data pack
// TODO: Do some major cleaning up of my old debug and scrap code
public class DimensionRegistries {
	// Register the dimension
	public static final ResourceKey<Level> BETWEENLANDS_DIMENSION_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"));
	public static final ResourceKey<DimensionType> BETWEENLANDS_DIMENSION_TYPE_KEY = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"));
	//public static final DeferredRegister<Level> DIMS = DeferredRegister.create(Registry.DIMENSION_REGISTRY, TheBetweenlands.ID);

	//public static final ResourceKey<Level> dtest = ResourceKey.create(ForgeRegistries.WORLD_TYPES, new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"));
	public static final DeferredRegister<Level> DIMS = DeferredRegister.create(Registry.DIMENSION_REGISTRY, TheBetweenlands.ID);
	public static final DeferredRegister<DimensionType> DIM_TYPES = DeferredRegister.create(Registry.DIMENSION_TYPE_REGISTRY, TheBetweenlands.ID);
	public static final DeferredRegister<LevelStem> DIM_STEM = DeferredRegister.create(Registry.LEVEL_STEM_REGISTRY, TheBetweenlands.ID);

	// having a look at some more hardcoded methods for dimensions
	//public static final ResourceKey<Registry<Level>> BETWEENLANDS_DIMENSION =
	//public static final ResourceKey<LevelStem> WORLDGEN_KEY = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, BETWEENLANDS_DIMENSION_KEY.getRegistryName());

	//public static final RegistryObject<LevelStem> BETWEENLANDS_STEM = DIM_STEM.register("the_betweenlands", () -> new LevelStem(BETWEENLANDS_TYPE.get(), BetweenlandsGeneratorFactory, true));

	//public static final RegistryObject<Level> TESTLANDS = DIMS.register("the_testlands", () -> new BetweenlandsDimension(
	//		new BetweenlandsLevelData(),
	//		TESTLANDS_DIMENSION_KEY,
	//		TESTLANDS_TYPE.getHolder().get(),
	//		() -> InactiveProfiler.INSTANCE,
	//		Minecraft.getInstance().isLocalServer(), false, 0));

	//public static void BetweenlandsType(BootstrapConfig<DimensionType> context) {

	//}

	
	@SuppressWarnings("unchecked")
	public static void register(IEventBus event) {

		// Add betweenlands sky effects
		DimensionSpecialEffects.EFFECTS.put(new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"), BetweenlandsSpecialEffects.BetweenlandsEffects());

		/*
		Field reflection;
		try {
			// reflect dim special effects list and append betweenlands special effects to it
			reflection = DimensionSpecialEffects.class.getDeclaredFields()[0];
			reflection.setAccessible(true);
			
			try {
				((Object2ObjectMap<ResourceLocation, DimensionSpecialEffects>) reflection.get(reflection)).put(new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"), BetweenlandsSpecialEffects.BetweenlandsEffects());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}

		 */

		//DIM_TYPES.register(event);
		//DIMS.register(event);
	}
}