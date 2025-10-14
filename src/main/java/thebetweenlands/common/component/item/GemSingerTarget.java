package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import thebetweenlands.common.item.tool.GemSingerItem;

public record GemSingerTarget(BlockPos pos, GemSingerItem.Target target) {

	public static final Codec<GemSingerTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(GemSingerTarget::pos),
		GemSingerItem.Target.CODEC.fieldOf("target").forGetter(GemSingerTarget::target)
	).apply(instance, GemSingerTarget::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, GemSingerTarget> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, GemSingerTarget::pos,
		GemSingerItem.Target.STREAM_CODEC, GemSingerTarget::target,
		GemSingerTarget::new
	);
}
