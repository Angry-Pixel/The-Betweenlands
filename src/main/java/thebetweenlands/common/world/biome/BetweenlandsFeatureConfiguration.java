package thebetweenlands.common.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.carvers.BetweenlandsCaveCarverConfiguration;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.FeatureRegistries;

public class BetweenlandsFeatureConfiguration implements FeatureConfiguration {
    public int biome;
    public ResourceLocation name; // Can be null
    public boolean useBetweenlandsGenerator;

    // TODO: tie biome ids and fetcher to dimension biome provider biome registry
    public static final Codec<BetweenlandsFeatureConfiguration> CODEC = RecordCodecBuilder.create((codec) -> {
        return codec.group(ResourceLocation.CODEC.fieldOf("biome").forGetter(BetweenlandsFeatureConfiguration::getName)).
                and(Codec.BOOL.fieldOf("blgen").forGetter(BetweenlandsFeatureConfiguration::useBetweenlandsGenerator)).
                apply(codec, BetweenlandsFeatureConfiguration::new);
    });

    public BetweenlandsFeatureConfiguration(int biomeIndex, boolean useBetweenlandsGeneratorNoise) {
        super();
        this.biome = biomeIndex;
        this.useBetweenlandsGenerator = useBetweenlandsGeneratorNoise;
        this.name = new ResourceLocation(TheBetweenlands.ID,"deep_waters_crag_spiers");
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public boolean useBetweenlandsGenerator() {
        return this.useBetweenlandsGenerator;
    }

    public int getBiome() {
        return this.biome;
    }

    public BetweenlandsFeatureConfiguration(ResourceLocation biomename, boolean generatorintegration) {
        super();
        this.biome = -1;
        this.name = biomename;
        this.useBetweenlandsGenerator = generatorintegration;
    }
}
