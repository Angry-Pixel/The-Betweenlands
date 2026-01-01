package thebetweenlands.common.datamap.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public record LightningConversion(Holder<Item> convertTo, float convertChance, int maxConvert, boolean randomCount) {

	public static final Codec<LightningConversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("convert_to").forGetter(LightningConversion::convertTo),
		Codec.floatRange(0.0F, 1.0F).fieldOf("conversion_chance").forGetter(LightningConversion::convertChance),
		Codec.intRange(1, Item.ABSOLUTE_MAX_STACK_SIZE).fieldOf("max_conversions").forGetter(LightningConversion::maxConvert),
		Codec.BOOL.fieldOf("random_conversion_count").forGetter(LightningConversion::randomCount)
	).apply(instance, LightningConversion::new));
}
