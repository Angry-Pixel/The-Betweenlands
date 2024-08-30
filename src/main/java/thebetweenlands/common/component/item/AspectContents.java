package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.Optional;

public record AspectContents(Optional<Holder<AspectType>> aspect, int amount, Optional<Integer> customColor) {
	public static final AspectContents EMPTY = new AspectContents(Optional.empty(), 0, Optional.empty());

	public static final Codec<AspectContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		AspectType.CODEC.optionalFieldOf("aspect").forGetter(AspectContents::aspect),
		Codec.INT.fieldOf("amount").forGetter(AspectContents::amount),
		Codec.INT.optionalFieldOf("custom_color").forGetter(AspectContents::customColor)
	).apply(instance, AspectContents::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AspectContents> STREAM_CODEC = StreamCodec.composite(
		AspectType.STREAM_CODEC.apply(ByteBufCodecs::optional), AspectContents::aspect,
		ByteBufCodecs.INT, AspectContents::amount,
		ByteBufCodecs.INT.apply(ByteBufCodecs::optional), AspectContents::customColor,
		AspectContents::new
	);

	public AspectContents(Holder<AspectType> aspect, int amount) {
		this(Optional.of(aspect), amount, Optional.empty());
	}

	public static ItemStack createItemStack(Item item, Holder<AspectType> aspect, int amount) {
		ItemStack itemstack = new ItemStack(item);
		itemstack.set(DataComponentRegistry.ASPECT_CONTENTS, new AspectContents(aspect, amount));
		return itemstack;
	}

	public int getAspectColor() {
		if (this.customColor().isPresent()) return this.customColor().get();
		return this.aspect().map(aspect -> aspect.value().color()).orElse(-13083194);
	}
}
