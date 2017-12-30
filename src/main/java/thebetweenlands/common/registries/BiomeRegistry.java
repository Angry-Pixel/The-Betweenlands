package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.biome.*;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BiomeRegistry {
    public static final BiomeBetweenlands PATCHY_ISLANDS = new BiomePatchyIslands();
    public static final BiomeBetweenlands SWAMPLANDS = new BiomeSwamplands();
    public static final BiomeBetweenlands DEEP_WATERS = new BiomeDeepWaters();
    public static final BiomeBetweenlands COARSE_ISLANDS = new BiomeCoarseIslands();
    public static final BiomeBetweenlands SLUDGE_PLAINS = new BiomeSludgePlains();
    public static final BiomeBetweenlands MARSH_0 = new BiomeMarsh(0);
    public static final BiomeBetweenlands MARSH_1 = new BiomeMarsh(1);
    
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
