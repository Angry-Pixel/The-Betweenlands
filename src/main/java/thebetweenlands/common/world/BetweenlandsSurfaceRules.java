package thebetweenlands.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class BetweenlandsSurfaceRules extends SurfaceRules {

	public static ConditionSource simplexGradient(String p_189404_, VerticalAnchor p_189405_, VerticalAnchor p_189406_, double noiseScale) {
		return new SimplexGradientConditionSource(new ResourceLocation(p_189404_), p_189405_, p_189406_, noiseScale);
	}

	public static record SimplexGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove, double noiseScale) implements ConditionSource {
		static final Codec<SimplexGradientConditionSource> CODEC = RecordCodecBuilder.create((p_189839_) -> {
			return p_189839_.group(ResourceLocation.CODEC.fieldOf("random_name").forGetter(SimplexGradientConditionSource::randomName),
				VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(SimplexGradientConditionSource::trueAtAndBelow),
				VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(SimplexGradientConditionSource::falseAtAndAbove),
				Codec.doubleRange(0, 256).fieldOf("scale").forGetter(SimplexGradientConditionSource::noiseScale)).apply(p_189839_, SimplexGradientConditionSource::new);
		});

		public Codec<? extends ConditionSource> codec() {
			return CODEC;
		}

		public Condition apply(final Context p_189841_) {
			final int i = this.trueAtAndBelow().resolveY(p_189841_.context);
			final int j = this.falseAtAndAbove().resolveY(p_189841_.context);
			final double scale = this.noiseScale();
			final PositionalRandomFactory positionalrandomfactory = p_189841_.system.getOrCreateRandomFactory(this.randomName());

			class SimplexGradientCondition extends LazyYCondition {
				SimplexGradientCondition() {
					super(p_189841_);
				}

				protected boolean compute() {
					int k = this.context.blockY;
					if (k <= i) {
						return true;
					} else if (k >= j) {
						return false;
					} else {
						// Make simplex noise
						double d0 = Mth.map((double) k, (double) i, (double) j, 1.0D, 0.0D);
						RandomSource randomsource = new XoroshiroRandomSource(808);
						SimplexNoise noise = new SimplexNoise(randomsource);
						return (noise.getValue(this.context.blockX * scale, this.context.blockZ * scale) + 1) * 0.5 < d0;
					}
				}
			}

			return new SimplexGradientCondition();
		}
	}
}
