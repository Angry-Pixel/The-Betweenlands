package thebetweenlands.common.registries;

import java.util.function.Consumer;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.biome.BiomeCoarseIslands;
import thebetweenlands.common.world.biome.BiomeMarsh;
import thebetweenlands.common.world.biome.BiomePatchyIslands;
import thebetweenlands.common.world.biome.BiomeSludgePlains;
import thebetweenlands.common.world.biome.BiomeSwamplands;

@ObjectHolder(ModInfo.ID)
public class BiomeRegistry {
	@ObjectHolder("patchy_islands")
	public static final BiomeBetweenlands PATCHY_ISLANDS = null;

	@ObjectHolder("swamplands")
	public static final BiomeBetweenlands SWAMPLANDS = null;

	@ObjectHolder("deep_waters")
	public static final BiomeBetweenlands DEEP_WATERS = null;

	@ObjectHolder("coarse_islands")
	public static final BiomeBetweenlands COARSE_ISLANDS = null;

	@ObjectHolder("sludge_plains")
	public static final BiomeBetweenlands SLUDGE_PLAINS = null;

	@ObjectHolder("marsh_0")
	public static final BiomeBetweenlands MARSH_0 = null;

	@ObjectHolder("marsh_1")
	public static final BiomeBetweenlands MARSH_1 = null;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Biome> event) {
		final IForgeRegistry<Biome> registry = event.getRegistry();

		register(new RegistryHelper<Biome>() {
			@Override
			public <F extends Biome> F reg(String regName, F obj, Consumer<F> callback) {
				obj.setRegistryName(ModInfo.ID, regName);
				registry.register(obj);
				callback.accept(obj);
				return obj;
			}
		});
	}

	private static void register(RegistryHelper<Biome> reg) {
		reg.reg("patchy_islands", new BiomePatchyIslands());
		reg.reg("swamplands", new BiomeSwamplands());
		reg.reg("coarse_islands", new BiomeCoarseIslands());
		reg.reg("sludge_plains", new BiomeSludgePlains());
		reg.reg("marsh_0", new BiomeMarsh(0));
		reg.reg("marsh_1", new BiomeMarsh(1));
	}
}
