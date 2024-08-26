package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlessingData {

	public static final Codec<BlessingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GlobalPos.CODEC.optionalFieldOf("location").forGetter(o -> o.location)
	).apply(instance, BlessingData::new));

	private Optional<GlobalPos> location;

	public BlessingData() {
		this(Optional.empty());
	}

	public BlessingData(Optional<GlobalPos> location) {
		this.location = location;
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


	public void setBlessed(ResourceKey<Level> dimension, BlockPos location) {
		this.location = Optional.of(GlobalPos.of(dimension, location));
//		this.setChanged();
	}

	public void clearBlessed() {
		this.location = Optional.empty();
//		this.setChanged();
	}

	private void setChanged(LivingEntity entity) {
//		PacketDistributor.sendToPlayersTrackingEntity(entity, new UpdateBlessedPacket(this.location));
	}
}
