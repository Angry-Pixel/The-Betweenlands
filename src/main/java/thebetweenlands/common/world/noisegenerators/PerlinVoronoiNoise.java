package thebetweenlands.common.world.noisegenerators;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

// uses voroni noise then applys a perlin noise derived factor bases on biome inputs
public class PerlinVoronoiNoise {
	public final VoronoiCellNoise voroni;
	public int WeightTotal = 0;
	public final int OctiveCount;
	public List<BiomeEntry> biomeEntryList;
	public final Function<Integer, Double> octavesampler;
	public PerlinNoise distortnoise;        // distords the sampling to make more organic patch shapes

	// Basic stepper
	public static Function<Integer, Double> LOG_STEP = (step) -> {
		double dstep = (double) step;
		if (step == 0) {
			return (double) 1;
		}
		return (dstep) - (1 / dstep);
	}; // each proceeding octive step is a quarter of the power of the last

	// cache for speed boost!
	public Cache<Pair<Integer, Integer>, VoronoiCellNoise.ReturnNoise> samplecache = CacheBuilder.newBuilder().maximumSize(200).build();

	// - octiveMap: suppler to a function that generates a modifyer for each input next octive step
	public PerlinVoronoiNoise(List<BiomeEntry> biomeEntryList, int octivecount, double cellscale, RandomSource seed, @Nullable Function<Integer, Double> octiveMap) {
		if (octivecount <= 0) {
			throw new IndexOutOfBoundsException("Octive count must be more then 0! (" + octivecount + ")");
		}
		if (biomeEntryList.isEmpty()) {
			throw new IndexOutOfBoundsException("Biome Entry List is empty!");
		}

		for (BiomeEntry entry : biomeEntryList) {
			this.WeightTotal += entry.weight;
		}

		if (octiveMap == null) this.octavesampler = LOG_STEP;
		else this.octavesampler = octiveMap;

		this.OctiveCount = octivecount;
		this.biomeEntryList = biomeEntryList;
		this.distortnoise = PerlinNoise.create(seed, List.of(0, 1));
		this.voroni = new VoronoiCellNoise(this.WeightTotal, cellscale, seed);
	}

	// returns index of biome in biomeEntryList as result biome
	// 2d for surface noise
	public int sample(int x, int y) {
		List<VoronoiCellNoise.point> points = this.voroni.GetPoints(x, y);
		points.forEach((point) -> {
			// convert weight to point id for ease of use
			point.weight = IndexFromWeight(point.weight);
		});

		double dist = 1000000000;
		int outIndex = 0;

		for (int point = 0; point < points.size(); point++) {
			// TODO: make some more basic stepper functions
			BiomeEntry entry = biomeEntryList.get(points.get(point).weight);

			double noisex = 0;
			double noisey = 0;

			for (int step = 1; step <= OctiveCount; step++) {
				double octivemul = octavesampler.apply(step);
				noisex += distortnoise.getValue(x, 64, y) * octivemul;
				noisey += distortnoise.getValue(y, -64, y) * octivemul;
			}
			//double xfactor = ((1) * entry.factorMul) + entry.baseFactor;
			//double yfactor = ((1) * entry.factorMul) + entry.baseFactor;

			double compdist = Math.sqrt(Math.pow((((x) - points.get(point).x)), 2) + Math.pow((((y) - points.get(point).z)), 2));

			if ((double) compdist < (double) dist) {
				dist = compdist;
				outIndex = point;
			}
		}

		return points.get(outIndex).weight;
	}

	// returns the index of a biome from weight
	public int IndexFromWeight(int weight) {
		for (int index = 0; index <= this.biomeEntryList.size(); index++) {
			weight -= this.biomeEntryList.get(index).weight;
			if (weight <= 0) {
				return index;
			}
		}
		return 0;
	}

	/**
	 * @param baseFactor base factor or size of the biome
	 * @param factorMul  Multiplier the perlin noise is applied (stepper is then applied on top)
	 */
	public record BiomeEntry(int weight, double baseFactor, double factorMul) {
	}
}
