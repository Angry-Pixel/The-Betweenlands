package thebetweenlands.common.component.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record OriginalItemData(@Nonnull ItemStack originalStack) {

	public static final Codec<OriginalItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ItemStack.CODEC.fieldOf("original_item").forGetter(OriginalItemData::originalStack)
	).apply(instance, OriginalItemData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, OriginalItemData> STREAM_CODEC = StreamCodec.composite(
		ItemStack.STREAM_CODEC, OriginalItemData::originalStack,
		OriginalItemData::new
	);
	
}
