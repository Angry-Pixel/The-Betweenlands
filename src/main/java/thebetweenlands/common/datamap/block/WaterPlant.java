package thebetweenlands.common.datamap.block;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

public record WaterPlant(
		float movementGunk,
		@Nonnull WaterPlantVolumeType movementGunkType,
		float passiveGunk,
		@Nonnull WaterPlantVolumeType passiveGunkType
) {
	// movementGunk: how much gunk is added for moving all the way through the block's hitbox
	// passiveGunk:  how much gunk is added each tick for passively being inside the plant's hitbox
	// Also, we allow negative gunk values
	
	public static final Codec<WaterPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.POSITIVE_FLOAT.fieldOf("movement_gunk").forGetter(WaterPlant::movementGunk),
			NeoForgeExtraCodecs.optionalFieldAlwaysWrite(WaterPlantVolumeType.CODEC, "movement_gunk_type", WaterPlantVolumeType.PERCENT).forGetter(WaterPlant::movementGunkType),
			ExtraCodecs.POSITIVE_FLOAT.fieldOf("passive_gunk").forGetter(WaterPlant::passiveGunk),
			NeoForgeExtraCodecs.optionalFieldAlwaysWrite(WaterPlantVolumeType.CODEC, "passive_gunk_type", WaterPlantVolumeType.VOLUME).forGetter(WaterPlant::passiveGunkType)
	).apply(instance, WaterPlant::new));
	
	public WaterPlant {
		Objects.requireNonNull(movementGunkType);
		Objects.requireNonNull(passiveGunkType);
	}
	
	public static enum WaterPlantVolumeType implements StringRepresentable {
		VOLUME("volume"),
		PERCENT("percent"),
		FLAT("flat");
		
		public static final Codec<WaterPlantVolumeType> CODEC = StringRepresentable.fromValues(WaterPlantVolumeType::values);
		
		private final String name;
		
		private WaterPlantVolumeType(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
