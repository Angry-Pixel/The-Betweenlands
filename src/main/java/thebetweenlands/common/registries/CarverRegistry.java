package thebetweenlands.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.carvers.*;

public class CarverRegistry {
	public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, TheBetweenlands.ID);
	public static final DeferredRegister<ConfiguredWorldCarver<?>> CONFIGUERED_CARVERS = DeferredRegister.create(Registry.CONFIGURED_CARVER_REGISTRY, TheBetweenlands.ID);
	
	public static final Codec<CaveCarverConfiguration> cavesconfig_codec = CaveCarverConfiguration.CODEC;
	public static final Codec<CanyonCarverConfiguration> ravineconfig_codec = CanyonCarverConfiguration.CODEC;
	
	// Raw Carvers
	public static final RegistryObject<WorldCarver<CaveCarverConfiguration>> BETWEENLANDS_CAVES = CARVERS.register("betweenlands_caves", () -> new BetweenlandsCaveCarver(cavesconfig_codec));
	public static final RegistryObject<WorldCarver<CanyonCarverConfiguration>> BETWEENLANDS_RAVINES = CARVERS.register("betweenlands_ravines", () -> new BetweenlandsRavineCarver(ravineconfig_codec));
	public static final RegistryObject<WorldCarver<CarverConfiguration>> CAVES_BETWEENLANDS = CARVERS.register("caves_betweenlands", () -> new CavesBetweenlands(cavesconfig_codec));
	public static final RegistryObject<WorldCarver<CarverConfiguration>> GIANT_ROOTS_BETWEENLANDS = CARVERS.register("giant_roots_betweenlands", () -> new GiantRoots(cavesconfig_codec));

	// Stock Carvers
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public class ConfigueredCarvers {
		public static final RegistryObject<ConfiguredWorldCarver<CaveCarverConfiguration>> BETWEENLANDS_CAVES = CONFIGUERED_CARVERS.register("betweenlands_caves", () -> new ConfiguredWorldCarver(CarverRegistry.BETWEENLANDS_CAVES.get(),new BetweenlandsCaveCarverConfiguration(new CarverConfiguration(0.5f, UniformHeight.of(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(32)), ConstantFloat.of(1), VerticalAnchor.absolute(-53), CarverDebugSettings.DEFAULT), ConstantFloat.of(1.5f), ConstantFloat.of(0.75f), ConstantFloat.of(-1))));
		public static final RegistryObject<ConfiguredWorldCarver<CanyonCarverConfiguration>> BETWEENLANDS_RAVINES = CONFIGUERED_CARVERS.register("betweenlands_ravines", () -> CarverRegistry.BETWEENLANDS_RAVINES.get().configured(new BetweenlandsRavineCarverConfiguration(new CarverConfiguration(0.025f, UniformHeight.of(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(32)), ConstantFloat.of(1), VerticalAnchor.absolute(-53), CarverDebugSettings.DEFAULT), UniformFloat.of(-0.125f, 0.125f), new CanyonCarverConfiguration.CanyonShapeConfiguration(ConstantFloat.of(3), TrapezoidFloat.of(0,6,2), 3, UniformFloat.of(0.75f, 1), 1, 0))));
		public static final RegistryObject<ConfiguredWorldCarver<CaveCarverConfiguration>> CAVES_BETWEENLANDS = CONFIGUERED_CARVERS.register("caves_betweenlands", () -> new ConfiguredWorldCarver(CarverRegistry.CAVES_BETWEENLANDS.get(),new BetweenlandsCaveCarverConfiguration(new CarverConfiguration(0.5f, UniformHeight.of(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(32)), ConstantFloat.of(1), VerticalAnchor.absolute(-53), CarverDebugSettings.DEFAULT), ConstantFloat.of(1.5f), ConstantFloat.of(0.75f), ConstantFloat.of(-1))));
		public static final RegistryObject<ConfiguredWorldCarver<CaveCarverConfiguration>> GIANT_ROOTS_BETWEENLANDS = CONFIGUERED_CARVERS.register("giant_roots_betweenlands", () -> new ConfiguredWorldCarver(CarverRegistry.GIANT_ROOTS_BETWEENLANDS.get(),new BetweenlandsCaveCarverConfiguration(new CarverConfiguration(0.5f, UniformHeight.of(VerticalAnchor.aboveBottom(-60), VerticalAnchor.belowTop(32)), ConstantFloat.of(1), VerticalAnchor.absolute(-53), CarverDebugSettings.DEFAULT), ConstantFloat.of(1.5f), ConstantFloat.of(0.75f), ConstantFloat.of(-1))));
	}
	
	// Register carver list
	public static void register(IEventBus eventBus) {
		CARVERS.register(eventBus);
		CONFIGUERED_CARVERS.register(eventBus);
	}
}
