package thebetweenlands.common.world.noisegenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.TheBetweenlands;


// TODO: make noise effect distance uniformly
// - currently just stretches distances, current method is only accurate with a convex structure, need one for concave
public class VoronoiCellNoise {
	public int maxvalue;					// the highest value the output interger
	public double cellscale;				// the size in blocks of the cells
	public double noisescale = 10;				// scale of noise sampling for int generation
	public SimplexNoise seednoise;			// sets the seed for eatch point
	public SimplexNoise distortnoise;		// distords the sampling to make more organic patch shapes

	// Simple cache to boost performance
	// arbitrarily set to 200 max values
	public Cache<Pair<Integer,Integer>,ReturnNoise> samplecache = CacheBuilder.newBuilder().maximumSize(200).build();
	// stores points to reduce time regenerating them
	public Cache<Pair<Integer,Integer>,List<point>> pointcache = CacheBuilder.newBuilder().maximumSize(200).build();
	//int keysStored = 0; // checking for multiple instances
	
	public VoronoiCellNoise(int maxvalue, double cellscale, RandomSource seed) {
		this.maxvalue = maxvalue;
		if (cellscale <= 1.0) {
			// dont set cellscale to zero you 4hed
			cellscale = 1;
		}
		this.cellscale = cellscale; // in blocks
		this.seednoise = new SimplexNoise(seed);
		this.distortnoise = new SimplexNoise(seed);
	}
	
	// returns the index of the biome sampled
	public ReturnNoise sample(int x, int y) {

		// Key pair for cache
		Pair<Integer,Integer> pairKey = Pair.of(x,y);

		// Cache optimisation
		ReturnNoise cacheReturn = samplecache.getIfPresent(pairKey);
		if (cacheReturn != null) {
			// debug code remove this if my dome brine forgoto
			//if (keysStored < 100)
			//TheBetweenlands.LOGGER.info(keysStored); // check if cache is being utilised, I dont know if mojang's code create multiple instance of chunks

			// return
			return cacheReturn;
		}

		// generate points
		List<point> list = GetPoints(x, y);
		
		double dist = 1000000000;	// set for safety
		int out = 0;

		// artifact zones to check
		// /tp Dev -307 133 460

		double distortx = distortnoise.getValue(x*0.015625 , y*0.015625, 20) * (cellscale * 0.1);//distortnoise.getValue((float)x*0.01, (float)y*0.01, -20) * (cellscale * 0.1);
		double distorty = distortnoise.getValue(x*0.015625, y*0.015625, -20) * (cellscale * 0.1);//distortnoise.getValue((float)x*0.01, (float)y*0.01, 20) * (cellscale * 0.1);

		distortx -= distortnoise.getValue(x*0.0078125 , y*0.0078125, 20) * (cellscale * 0.05);
		distorty -= distortnoise.getValue(x*0.0078125 , y*0.0078125, -20) * (cellscale * 0.05);

		distortx += distortnoise.getValue(x*0.03125, y*0.03125, 20) * (cellscale * 0.025);
		distorty += distortnoise.getValue(x*0.03125, y*0.03125, -20) * (cellscale * 0.025);

		distortx -= distortnoise.getValue(x*0.0625 , y*0.0625, 20) * (cellscale * 0.0125);
		distorty -= distortnoise.getValue(x*0.0625 , y*0.0625, -20) * (cellscale * 0.0125);


		//distortx += distortnoise.getValue(x*0.055, y*0.055, 5) * (cellscale * 0.011);
		//distorty += distortnoise.getValue(x*0.055, y*0.055, -5) * (cellscale * 0.011);

		//distortx += distortnoise.getValue(x*0.005, y*0.005, 5) * (cellscale * 0.2);
		//distorty += distortnoise.getValue(x*0.005, y*0.005, -5) * (cellscale * 0.2);

		//distortx += distortnoise.getValue(x*0.095, y*0.095, 5) * (cellscale * 0.025);
		//distorty += distortnoise.getValue(x*0.095, y*0.095, -5) * (cellscale * 0.025);

		//distortx += distortnoise.getValue(x*0.075, y*0.075, 20) * (cellscale * 0.02);
		//distorty += distortnoise.getValue(x*0.075, y*0.075, -20) * (cellscale * 0.02);
		
		// extras
		List<Double> exdist = new ArrayList();
		int outIndex = 0;
		
		// get the closest point
		// Output is sampled here
		for (int point = 0; point < list.size(); point++) {
			// TODO: change to not apply warping to distance

			double compdist;

			//if (list.get(point).z == 0) {
				double xfactor = (distortnoise.getValue(list.get(point).x,list.get(point).z,-64)*0.25)+0.35;
				double zfactor = (distortnoise.getValue(list.get(point).x,list.get(point).z,64)*0.25)+0.35;

				compdist = Math.sqrt(Math.pow((((x + distortx) - list.get(point).x) * xfactor), 2) + Math.pow((((y+ distorty) - list.get(point).z) * zfactor), 2)); // the distance with map warped
			//}
			//else {
				//compdist = Math.sqrt(Math.pow(((x + distortx) - list.get(point).x), 2) + Math.pow(((y+ distorty) - list.get(point).y), 2)); // the distance with map warped
			//}
			
			exdist.add(compdist);
			if ((double)compdist < (double)dist) {
				dist = compdist;
				out = (int) list.get(point).weight;
				outIndex = point;
			}
		}
		
		if (out>maxvalue) out = maxvalue;
		
		// fetch biome values from points list
		List<Integer> biomelist = new ArrayList();
		for (point point : list) {
			biomelist.add(point.weight);
		}

		// the return to output
		ReturnNoise outNoise = new ReturnNoise(out, outIndex, biomelist, exdist);

		//keysStored++;

		// add the value to cache
		samplecache.put(pairKey, outNoise);

		// return
		return outNoise;
	}

	public List<point> GetPoints(int x, int y) {
		// Key pair for cache
		Pair<Integer,Integer> pairKey = Pair.of(x,y);

		// Cache optimisation
		List<point> cacheReturn = pointcache.getIfPresent(pairKey);
		if (cacheReturn != null) {
			// return
			return cacheReturn;
		}

		// generate points
		List<point> list = new ArrayList();

		int xchunk = 0;
		int ychunk = 0;

		// some div by 0 protection
		if (x != 0.0) {
			xchunk = (int) (x / cellscale);
		}
		if (y != 0.0) {
			ychunk = (int) (y / cellscale);
		}

		// Generate biome points
		// TODO: add world seed
		// translate sample cords + 2 in all directions to safely resolve potential perlin noise sampling out of bounds and causing artifacts
		for (int tslx = -2; tslx<= 2; tslx++) {
			for (int tsly = -2; tsly<= 2; tsly++) {
				// slow solution, needs replacement
				Random randtest = new Random(((xchunk-tslx)*374761393) + ((ychunk-tsly)*668265263));

				// generates the cell point
				// used for surrounded biomes such as swamplands clearing and slugeplains clearing
				// and some structures and features
				int value = randtest.nextInt(maxvalue)-1;

				list.add(new point(((xchunk-tslx)*cellscale) + ((cellscale-(cellscale*0.5))*(randtest.nextDouble(-1, 1))),
						0,((ychunk-tsly)*cellscale) + ((cellscale-(cellscale*0.5))*randtest.nextDouble(-1, 1)), value));
			}
		}


		// add the list to cache
		pointcache.put(pairKey, list);

		return list;
	}

	// returns distance to all other points and their respective biomes
	// TODO: look at changing this to a record
	public static class ReturnNoise {
		public List<Integer> pointVal = new ArrayList();		// the point's biome value
		public List<Double> dist = new ArrayList();			// the point's distance from the sample location
		public int result;
		public int resultIndex;
		
		// Constructor for cell noise sample return value (distance list is used for biome border trentches)
		public ReturnNoise(int result, int resultIndex, List<Integer> pointVal, List<Double> dist) {
			this.result = result;
			this.resultIndex = resultIndex;
			this.pointVal = pointVal;
			this.dist = dist;
		}
	}

	public static class point {
		public double x, y, z;
		public int weight;

		public point(double x, double y, double z, int weight){
			this.x = x;
			this.y = y;
			this.z = z;
			this.weight = weight;
		}
	}
}
