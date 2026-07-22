package thebetweenlands.common.world.spawning;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BiomeSpawnerZone( ICustomSpawnEntry locationType, List<BaseSpawnProperties> weightedMobPool) {
    private static final Codec<ICustomSpawnEntry> DISPATCH_CODEC = CustomSpawnType.CODEC.dispatch(ICustomSpawnEntry::getType, CustomSpawnType::codec);

    public static final Codec<BiomeSpawnerZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        DISPATCH_CODEC.fieldOf("location_type").forGetter(BiomeSpawnerZone::locationType),
        BaseSpawnProperties.CODEC.listOf().fieldOf("weighted_mobs").forGetter(BiomeSpawnerZone::weightedMobPool)
    ).apply(instance, BiomeSpawnerZone::new));
}