package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.biome.BiomePatchyIslands;
import thebetweenlands.common.world.biome.BiomeSwamplands;

public class BiomeRegistry {
	public static final BiomeBetweenlands PATCHY_ISLANDS = new BiomePatchyIslands();
	public static final BiomeBetweenlands SWAMPLANDS = new BiomeSwamplands();

	public static final List<BiomeBetweenlands> REGISTERED_BIOMES = new ArrayList<BiomeBetweenlands>();

	public void preInit() {
		try {
			for (Field f : BiomeRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof BiomeBetweenlands) {
					BiomeBetweenlands biome = (BiomeBetweenlands) obj;
					GameRegistry.register(biome.setRegistryName(new ResourceLocation(ModInfo.ID, biome.getBiomeName())));
					REGISTERED_BIOMES.add(biome);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
