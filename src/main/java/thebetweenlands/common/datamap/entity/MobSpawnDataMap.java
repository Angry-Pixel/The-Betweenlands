package thebetweenlands.common.datamap.entity;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.common.world.spawning.CustomSpawnType;
import thebetweenlands.common.world.spawning.ICustomSpawnEntry;

public class MobSpawnDataMap {

    public static final Codec<ICustomSpawnEntry> DISPATCH_CODEC = CustomSpawnType.CODEC.dispatch("type", ICustomSpawnEntry::getType, type -> (MapCodec<? extends ICustomSpawnEntry>) type.codec());
    public static final Codec<List<ICustomSpawnEntry>> MASTER_LIST_CODEC = DISPATCH_CODEC.listOf();

}


