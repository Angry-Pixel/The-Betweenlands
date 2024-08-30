package thebetweenlands.api.aspect.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import thebetweenlands.api.BLRegistries;

public record AspectType(int color, AspectTier tier, boolean rollAsCommonAspect, int amount) {

	public static final Codec<AspectType> DIRECT_CODEC = RecordCodecBuilder.create(p_345420_ -> p_345420_.group(
			Codec.INT.fieldOf("color").forGetter(AspectType::color),
		AspectTier.CODEC.fieldOf("tier").forGetter(AspectType::tier),
			Codec.BOOL.fieldOf("common_aspect").forGetter(AspectType::rollAsCommonAspect),
			Codec.INT.fieldOf("amount").forGetter(AspectType::amount))
		.apply(p_345420_, AspectType::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AspectType> DIRECT_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, AspectType::color,
		AspectTier.STREAM_CODEC, AspectType::tier,
		ByteBufCodecs.BOOL, AspectType::rollAsCommonAspect,
		ByteBufCodecs.INT, AspectType::amount,
		AspectType::new
	);

	public static final Codec<Holder<AspectType>> CODEC = RegistryFileCodec.create(BLRegistries.Keys.ASPECT_TYPES, DIRECT_CODEC);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AspectType>> STREAM_CODEC = ByteBufCodecs.holder(BLRegistries.Keys.ASPECT_TYPES, DIRECT_STREAM_CODEC);
}
