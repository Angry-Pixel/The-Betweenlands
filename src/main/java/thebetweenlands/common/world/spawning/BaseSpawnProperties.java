package thebetweenlands.common.world.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public record BaseSpawnProperties(
    EntityType<?> mobType,
    int weight,
    short baseWeight,
    boolean hostile,
    int minGroupSize,
    int maxGroupSize,
    int minHeight,
    int maxHeight,
    double spawnCheckRadius,
    double spawnCheckRangeY,
    double groupSpawnRadius,
    int spawningInterval,
    boolean canSpawnOnWater,  
    boolean canSpawnInWater,  
    boolean constantWeight    
) {
    public static final Codec<BaseSpawnProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("_mob").forGetter(BaseSpawnProperties::mobType),
            Codec.INT.fieldOf("weight").forGetter(BaseSpawnProperties::weight),
            Codec.SHORT.fieldOf("base_weight").forGetter(BaseSpawnProperties::baseWeight),
            Codec.BOOL.fieldOf("hostile").forGetter(BaseSpawnProperties::hostile),
            Codec.INT.fieldOf("min_group_size").forGetter(BaseSpawnProperties::minGroupSize),
            Codec.INT.fieldOf("max_group_size").forGetter(BaseSpawnProperties::maxGroupSize),
            Codec.INT.fieldOf("min_height").forGetter(BaseSpawnProperties::minHeight),
            Codec.INT.fieldOf("max_height").forGetter(BaseSpawnProperties::maxHeight),
            Codec.DOUBLE.fieldOf("spawn_check_radius").forGetter(BaseSpawnProperties::spawnCheckRadius),
            Codec.DOUBLE.fieldOf("spawn_check_range_y").forGetter(BaseSpawnProperties::spawnCheckRangeY),
            Codec.DOUBLE.fieldOf("group_spawn_radius").forGetter(BaseSpawnProperties::groupSpawnRadius),
            Codec.INT.fieldOf("spawning_interval").forGetter(BaseSpawnProperties::spawningInterval),
            Codec.BOOL.fieldOf("can_spawn_on_water").forGetter(BaseSpawnProperties::canSpawnOnWater),
            Codec.BOOL.fieldOf("can_spawn_in_water").forGetter(BaseSpawnProperties::canSpawnInWater),
            Codec.BOOL.fieldOf("constant_weight").forGetter(BaseSpawnProperties::constantWeight)
        ).apply(instance, BaseSpawnProperties::new)
    );
}
