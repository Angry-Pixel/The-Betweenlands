package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record FlatLandConfiguration(BlockState terrainState, /*BlockState waterState,*/ int waterLevel, int terrainLevel/*, OptionalInt cellWidth*//*, OptionalInt cellHeight*/) implements FeatureConfiguration {

	public static final Codec<FlatLandConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				BlockState.CODEC.fieldOf("terrain_state").forGetter(FlatLandConfiguration::terrainState),
//				BlockState.CODEC.fieldOf("water_state").forGetter(FlatLandConfiguration::waterState),
				Codec.INT.fieldOf("water_level").forGetter(FlatLandConfiguration::waterLevel),
				Codec.INT.fieldOf("terrain_level").forGetter(FlatLandConfiguration::terrainLevel)
//				ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("cell_width", 1).xmap(((Optional<Integer> optionalInt) -> optionalInt.isPresent() ? OptionalInt.of(optionalInt.get()) : OptionalInt.absent()), (OptionalInt optionalInt) -> optionalInt.isPresent() ? Optional.<Integer>of(optionalInt.get()) : Optional.absent())
		).apply(instance, FlatLandConfiguration::new));
	
}
