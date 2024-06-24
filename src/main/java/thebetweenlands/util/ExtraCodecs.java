package thebetweenlands.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;

public class ExtraCodecs {
	public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS_CODEC = StreamCodec.composite(ByteBufCodecs.INT, o -> o.x, ByteBufCodecs.INT, o -> o.z, ChunkPos::new);

}
