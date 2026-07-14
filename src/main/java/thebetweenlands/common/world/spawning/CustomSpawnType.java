package thebetweenlands.common.world.spawning;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.util.StringRepresentable;

public enum CustomSpawnType implements StringRepresentable {
    CAVE("cave", () -> CaveSpawnEntry.CODEC),
    PITSTONE_CAVE("pitstone_cave", () -> PitstoneCaveSpawnEntry.CODEC),
    SURFACE("surface", () -> SurfaceSpawnEntry.CODEC),
    TREE("tree", () -> TreeSpawnEntry.CODEC),
    SKY("sky", () -> SkySpawnEntry.CODEC);

    public static final Codec<CustomSpawnType> CODEC = StringRepresentable.fromEnum(CustomSpawnType::values);

    private final String name;
    private final Supplier<MapCodec<? extends ICustomSpawnEntry>> codec;

    CustomSpawnType(String name, Supplier<MapCodec<? extends ICustomSpawnEntry>> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override public String getSerializedName() { return this.name; }
    
    public MapCodec<? extends ICustomSpawnEntry> codec() {
        return this.codec.get(); 
    }
}

