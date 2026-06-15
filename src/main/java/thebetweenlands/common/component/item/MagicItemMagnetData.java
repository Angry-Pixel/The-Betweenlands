package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MagicItemMagnetData(int item_magnet_last_gravity_update, boolean magnetActive) {

	public static final MagicItemMagnetData DEFAULT = new MagicItemMagnetData(0, false);

	public static final Codec<MagicItemMagnetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("item_magnet_last_gravity_update").forGetter(MagicItemMagnetData::item_magnet_last_gravity_update),
		Codec.BOOL.fieldOf("magnetActive").forGetter(MagicItemMagnetData::magnetActive)
	).apply(instance, MagicItemMagnetData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, MagicItemMagnetData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, MagicItemMagnetData::item_magnet_last_gravity_update,
		ByteBufCodecs.BOOL, MagicItemMagnetData::magnetActive,
		MagicItemMagnetData::new
	);
}
