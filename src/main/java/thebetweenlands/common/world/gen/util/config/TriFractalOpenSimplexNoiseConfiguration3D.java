package thebetweenlands.common.world.gen.util.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexCache;
import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexData;

// Now if that isn't a class name, I don't know what is
public class TriFractalOpenSimplexNoiseConfiguration3D {

	public static final MapCodec<TriFractalOpenSimplexNoiseConfiguration3D> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf("first").forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings1),
					FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf("second").forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings2),
					FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf("third").forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings2)
			).apply(instance, TriFractalOpenSimplexNoiseConfiguration3D::new));
	
	public static final MapCodec<TriFractalOpenSimplexNoiseConfiguration3D> namedCodec(String name1, String name2, String name3) {
		return RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf(name1).forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings1),
						FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf(name2).forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings2),
						FractalOpenSimplexNoiseSettings3D.MAP_CODEC.fieldOf(name3).forGetter(TriFractalOpenSimplexNoiseConfiguration3D::noiseSettings3)
				).apply(instance, TriFractalOpenSimplexNoiseConfiguration3D::new));
	}

	private final FractalOpenSimplexNoiseSettings3D noiseSettings1;
	private final FractalOpenSimplexNoiseSettings3D noiseSettings2;
	private final FractalOpenSimplexNoiseSettings3D noiseSettings3;
	private final TriFractalOpenSimplexCache noiseCache;
	
	public TriFractalOpenSimplexNoiseConfiguration3D(FractalOpenSimplexNoiseSettings3D noiseSettings1, FractalOpenSimplexNoiseSettings3D noiseSettings2, FractalOpenSimplexNoiseSettings3D noiseSettings3) {
		this.noiseSettings1 = noiseSettings1;
		this.noiseSettings2 = noiseSettings2;
		this.noiseSettings3 = noiseSettings3;
		this.noiseCache = new TriFractalOpenSimplexCache(noiseSettings1.octaves(), noiseSettings1.additiveSeed(), noiseSettings2.octaves(), noiseSettings2.additiveSeed(), noiseSettings3.octaves(), noiseSettings3.additiveSeed());
	}
	
	public FractalOpenSimplexNoiseSettings3D noiseSettings1() {
		return this.noiseSettings1;
	}
	
	public FractalOpenSimplexNoiseSettings3D noiseSettings2() {
		return this.noiseSettings2;
	}
	
	public FractalOpenSimplexNoiseSettings3D noiseSettings3() {
		return this.noiseSettings3;
	}
	
	public TriFractalOpenSimplexData getNoise(long seed) {
		return this.noiseCache.getNoise(seed);
	}
}
