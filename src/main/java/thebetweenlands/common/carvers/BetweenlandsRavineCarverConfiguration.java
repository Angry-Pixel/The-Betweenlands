package thebetweenlands.common.carvers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class BetweenlandsRavineCarverConfiguration extends CanyonCarverConfiguration {

	// curently unsused
	public BetweenlandsRavineCarverConfiguration(float p_190586_, HeightProvider p_190587_, FloatProvider p_190588_, VerticalAnchor p_190589_, CarverDebugSettings p_190590_, FloatProvider p_190591_, CanyonShapeConfiguration p_190592_) {
		super(p_190586_, p_190587_, p_190588_, p_190589_, p_190590_, p_190591_, p_190592_);
	}

	public BetweenlandsRavineCarverConfiguration(CarverConfiguration p_158980_, FloatProvider p_158981_, CanyonShapeConfiguration p_158982_) {
		this(p_158980_.probability, p_158980_.y, p_158980_.yScale, p_158980_.lavaLevel, p_158980_.debugSettings, p_158981_, p_158982_);
	}
}
