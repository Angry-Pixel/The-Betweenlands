package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public record BlessingData(Optional<GlobalPos> location) {

	public static final Codec<BlessingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GlobalPos.CODEC.optionalFieldOf("location").forGetter(o -> o.location)
	).apply(instance, BlessingData::new));

	public static final StreamCodec<FriendlyByteBuf, BlessingData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(GlobalPos.STREAM_CODEC), o -> o.location,
		BlessingData::new
	);

	public static BlessingData setBlessed(ResourceKey<Level> level, BlockPos pos) {
		return new BlessingData(Optional.of(GlobalPos.of(level, pos)));
	}

	public static BlessingData noBlessing() {
		return new BlessingData(Optional.empty());
	}

	public boolean isBlessed() {
		return this.location.isPresent();
	}

	@Nullable
	public BlockPos getBlessingLocation() {
		return this.location.map(GlobalPos::pos).orElse(null);
	}

	@Nullable
	public ResourceKey<Level> getBlessingDimension() {
		return this.location.map(GlobalPos::dimension).orElse(null);
	}
}
