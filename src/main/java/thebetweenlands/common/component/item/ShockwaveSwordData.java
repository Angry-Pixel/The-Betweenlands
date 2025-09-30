package thebetweenlands.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public record ShockwaveSwordData(int uses, long cooldownTimestamp) {

	public static final ShockwaveSwordData DEFAULT = new ShockwaveSwordData(0, -1);

	public static final Codec<ShockwaveSwordData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("uses").forGetter(ShockwaveSwordData::uses),
		Codec.LONG.fieldOf("cooldown").forGetter(ShockwaveSwordData::cooldownTimestamp)
	).apply(instance, ShockwaveSwordData::new));

	public static final StreamCodec<? super RegistryFriendlyByteBuf, ShockwaveSwordData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, ShockwaveSwordData::uses,
		ByteBufCodecs.VAR_LONG, ShockwaveSwordData::cooldownTimestamp,
		ShockwaveSwordData::new
	);

	public ShockwaveSwordData incrementUses(Level level) {
		int newUses = this.uses() + 1;
		if (newUses >= 3) {
			return new ShockwaveSwordData(3, level.getGameTime());
		}
		return new ShockwaveSwordData(newUses, this.cooldownTimestamp());
	}
}
