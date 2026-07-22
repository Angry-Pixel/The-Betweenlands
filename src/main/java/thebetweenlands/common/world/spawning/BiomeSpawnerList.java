package thebetweenlands.common.world.spawning;

import java.util.List;

import com.mojang.serialization.Codec;

public record BiomeSpawnerList(List<BiomeSpawnerZone> zones) {
    public static final Codec<BiomeSpawnerList> CODEC =  BiomeSpawnerZone.CODEC.listOf().xmap(BiomeSpawnerList::new, BiomeSpawnerList::zones);
}
