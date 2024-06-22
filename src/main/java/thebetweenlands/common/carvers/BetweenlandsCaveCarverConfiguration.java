package thebetweenlands.common.carvers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class BetweenlandsCaveCarverConfiguration extends CaveCarverConfiguration {

	public static final Codec<BetweenlandsCaveCarverConfiguration> CODEC = RecordCodecBuilder.create((p_159184_) -> {
		return p_159184_.group(CarverConfiguration.CODEC.forGetter((p_159192_) -> {
			return p_159192_;
		}), FloatProvider.CODEC.fieldOf("horizontal_radius_multiplier").forGetter((p_159190_) -> {
			return p_159190_.horizontalRadiusMultiplier;
		}), FloatProvider.CODEC.fieldOf("vertical_radius_multiplier").forGetter((p_159188_) -> {
			return p_159188_.verticalRadiusMultiplier;
		}), FloatProvider.codec(-1.0F, 1.0F).fieldOf("floor_level").forGetter((p_159186_) -> {
			return p_159186_.floorLevel;
		})).apply(p_159184_, BetweenlandsCaveCarverConfiguration::new);
	});
	final FloatProvider floorLevel;

	// curently unsused
	public BetweenlandsCaveCarverConfiguration(float p_190653_, HeightProvider p_190654_, FloatProvider p_190655_, VerticalAnchor p_190656_, CarverDebugSettings p_190657_, FloatProvider p_190658_, FloatProvider p_190659_, FloatProvider p_190660_) {
		super(p_190653_, p_190654_, p_190655_, p_190656_, p_190657_, p_190660_, p_190660_, p_190660_);
		this.floorLevel = p_190660_;

	}

	public BetweenlandsCaveCarverConfiguration(float p_159160_, HeightProvider p_159161_, FloatProvider p_159162_, VerticalAnchor p_159163_, boolean p_159164_, FloatProvider p_159165_, FloatProvider p_159166_, FloatProvider p_159167_) {
		this(p_159160_, p_159161_, p_159162_, p_159163_, CarverDebugSettings.DEFAULT, p_159165_, p_159166_, p_159167_);
	}

	public BetweenlandsCaveCarverConfiguration(CarverConfiguration p_159179_, FloatProvider p_159180_, FloatProvider p_159181_, FloatProvider p_159182_) {
		this(p_159179_.probability, p_159179_.y, p_159179_.yScale, p_159179_.lavaLevel, p_159179_.debugSettings, p_159180_, p_159181_, p_159182_);
	}
}
