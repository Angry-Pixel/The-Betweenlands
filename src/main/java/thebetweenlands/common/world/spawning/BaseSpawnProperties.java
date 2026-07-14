package thebetweenlands.common.world.spawning;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

public record BaseSpawnProperties(
	HolderSet<Biome> allowedBiomes,
    short baseWeight,
    short weight,
    boolean hostile,
    int minGroupSize,
    int maxGroupSize,
    int minHeight,
    int maxHeight,
    double spawnCheckRadius,
    double spawnCheckRangeY,
    double groupSpawnRadius,
    int spawningInterval
) {

	public static final Codec<BaseSpawnProperties> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
        	RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("allowed_biomes").forGetter(BaseSpawnProperties::allowedBiomes),
            Codec.SHORT.fieldOf("base_weight").forGetter(BaseSpawnProperties::baseWeight),
            Codec.SHORT.fieldOf("weight").forGetter(BaseSpawnProperties::weight),
            Codec.BOOL.fieldOf("hostile").forGetter(BaseSpawnProperties::hostile),
            Codec.INT.fieldOf("min_group_size").forGetter(BaseSpawnProperties::minGroupSize),
            Codec.INT.fieldOf("max_group_size").forGetter(BaseSpawnProperties::maxGroupSize),
            Codec.INT.fieldOf("min_height").forGetter(BaseSpawnProperties::minHeight),
            Codec.INT.fieldOf("max_height").forGetter(BaseSpawnProperties::maxHeight),
            Codec.DOUBLE.fieldOf("spawn_check_radius").forGetter(BaseSpawnProperties::spawnCheckRadius),
            Codec.DOUBLE.fieldOf("spawn_check_range_y").forGetter(BaseSpawnProperties::spawnCheckRangeY),
            Codec.DOUBLE.fieldOf("group_spawn_radius").forGetter(BaseSpawnProperties::groupSpawnRadius),
            Codec.INT.fieldOf("spawning_interval").forGetter(BaseSpawnProperties::spawningInterval)
        ).apply(instance, BaseSpawnProperties::new)
    );
}
