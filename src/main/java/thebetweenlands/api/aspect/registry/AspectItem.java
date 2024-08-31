package thebetweenlands.api.aspect.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.Item;
import thebetweenlands.api.BLRegistries;

public record AspectItem(Item item, AspectCalculatorType calculator) {

	public static final Codec<AspectItem> DIRECT_CODEC = RecordCodecBuilder.create(p_345420_ -> p_345420_.group(
		BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(AspectItem::item),
		AspectCalculatorType.CODEC.fieldOf("aspect_calculator").forGetter(AspectItem::calculator)
	).apply(p_345420_, AspectItem::new));

	public static final Codec<Holder<AspectItem>> CODEC = RegistryFileCodec.create(BLRegistries.Keys.ASPECT_ITEMS, DIRECT_CODEC);
}
