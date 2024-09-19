package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record UpgradeDamage(int damage, int maxDamage) {

	public static final UpgradeDamage EMPTY = new UpgradeDamage(0, 0);

	public static final Codec<UpgradeDamage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("damage").forGetter(UpgradeDamage::damage),
		Codec.INT.fieldOf("max_damage").forGetter(UpgradeDamage::maxDamage)
	).apply(instance, UpgradeDamage::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, UpgradeDamage> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, UpgradeDamage::damage,
		ByteBufCodecs.INT, UpgradeDamage::maxDamage,
		UpgradeDamage::new
	);
}
