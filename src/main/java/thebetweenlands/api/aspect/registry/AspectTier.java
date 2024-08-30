package thebetweenlands.api.aspect.registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum AspectTier implements StringRepresentable {
	COMMON,
	UNCOMMON,
	RARE;

	public static final StringRepresentable.EnumCodec<AspectTier> CODEC = StringRepresentable.fromEnum(AspectTier::values);
	public static final IntFunction<AspectTier> BY_ID = ByIdMap.continuous(AspectTier::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
	public static final StreamCodec<ByteBuf, AspectTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, AspectTier::ordinal);


	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
