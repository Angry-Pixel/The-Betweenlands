package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.biome.BiomeCoarseIslands;
import thebetweenlands.common.world.biome.BiomeDeepWaters;
import thebetweenlands.common.world.biome.BiomeMarsh;
import thebetweenlands.common.world.biome.BiomePatchyIslands;
import thebetweenlands.common.world.biome.BiomeSludgePlains;
import thebetweenlands.common.world.biome.BiomeSludgePlainsClearing;
import thebetweenlands.common.world.biome.BiomeSwamplands;
import thebetweenlands.common.world.biome.BiomeSwamplandsClearing;

public class BiomeRegistry {
    public static final BiomeBetweenlands PATCHY_ISLANDS = new BiomePatchyIslands();
    public static final BiomeBetweenlands SWAMPLANDS = new BiomeSwamplands();
    public static final BiomeBetweenlands DEEP_WATERS = new BiomeDeepWaters();
    public static final BiomeBetweenlands COARSE_ISLANDS = new BiomeCoarseIslands();
    public static final BiomeBetweenlands SLUDGE_PLAINS = new BiomeSludgePlains();
    public static final BiomeBetweenlands MARSH_0 = new BiomeMarsh(0);
    public static final BiomeBetweenlands MARSH_1 = new BiomeMarsh(1);
    
    public static final BiomeBetweenlands SWAMPLANDS_CLEARING = new BiomeSwamplandsClearing();
    public static final BiomeBetweenlands SLUDGE_PLAINS_CLEARING = new BiomeSludgePlainsClearing();
    
    public static final List<BiomeBetweenlands> REGISTERED_BIOMES = new ArrayList<BiomeBetweenlands>();

    private BiomeRegistry() {
    }

    @SubscribeEvent
    public static void registerBiomes(final RegistryEvent.Register<Biome> event) {
        final IForgeRegistry<Biome> registry = event.getRegistry();
        try {
            for (Field f : BiomeRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BiomeBetweenlands) {
                    BiomeBetweenlands biome = (BiomeBetweenlands) obj;
                    registry.register(biome);
                    biome.addTypes();
                    REGISTERED_BIOMES.add(biome);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
