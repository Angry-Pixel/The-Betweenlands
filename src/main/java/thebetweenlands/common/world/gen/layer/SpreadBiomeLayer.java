package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SingleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public class SpreadBiomeLayer implements SingleParentBiomeLayer {

	public static final MapCodec<SpreadBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					SpreadBiomeLayer.Quadrant.CODEC.fieldOf("quadrant").forGetter(o -> o.quadrant)
				).apply(instance, SpreadBiomeLayer::new)
		);

	private final BiomeLayerConfigured parent;
	private final Quadrant quadrant;
	
	public SpreadBiomeLayer(BiomeLayerConfigured parent, Quadrant quadrant) {
		this.parent = parent;
		this.quadrant = quadrant;
	}

	@Override
	public BiomeLayerConfigured getParentLayer() {
		return this.parent;
	}
	
	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

	public static int getParentX(int x) {
		return x >> 1;
	}

	public  static int getParentY(int y) {
		return y >> 1;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z) {
		int pX = x & 1;
		int pZ = z & 1;
		
		final Quadrant quadrant;
		if(this.quadrant == Quadrant.RANDOM) {
			// Pick a random quadrant out of {XNZN, XNZP, XPZN, XPZP}
			RandomSource random = context.createRandom(getParentX(x), getParentY(z));
			quadrant = Quadrant.fromRandomIndex(random.nextInt(4));
		} else {
			// Otherwise, just use the specified quadrant
			quadrant = this.quadrant;
		}
		
		// could probably be cleaner
		if(
				(pX == 0 && quadrant.getXAxisDirection() == AxisDirection.POSITIVE) ||
				(pX == 1 && quadrant.getXAxisDirection() == AxisDirection.NEGATIVE) ||
				(pZ == 0 && quadrant.getZAxisDirection() == AxisDirection.POSITIVE) ||
				(pZ == 1 && quadrant.getZAxisDirection() == AxisDirection.NEGATIVE)
			) {
			return -1;
		}

		return parentArea.get(getParentX(x), getParentY(z));
	}

	public static enum Quadrant implements StringRepresentable {
		XNZN("XNZN", AxisDirection.NEGATIVE, AxisDirection.NEGATIVE),
		XNZP("XNZP", AxisDirection.NEGATIVE, AxisDirection.POSITIVE),
		XPZN("XPZN", AxisDirection.POSITIVE, AxisDirection.NEGATIVE),
		XPZP("XPZP", AxisDirection.POSITIVE, AxisDirection.POSITIVE),
		RANDOM("RANDOM", null, null);
		
        public static final Codec<SpreadBiomeLayer.Quadrant> CODEC = StringRepresentable.fromEnum(SpreadBiomeLayer.Quadrant::values);

        private static final Quadrant[] RANDOM_QUADRANTS = new Quadrant[] {Quadrant.XNZN, Quadrant.XNZP, Quadrant.XPZN, Quadrant.XPZP};
        
        private final String serializationKey;
        private final AxisDirection xAxisDirection;
        private final AxisDirection zAxisDirection;
        
        private Quadrant(String serializationKey, AxisDirection xAxisDirection, AxisDirection zAxisDirection) {
        	this.serializationKey = serializationKey;
        	this.xAxisDirection = xAxisDirection;
        	this.zAxisDirection = zAxisDirection;
		}

		@Override
		public String getSerializedName() {
			return this.serializationKey;
		}

		public AxisDirection getXAxisDirection() {
			return this.xAxisDirection;
		}

		public AxisDirection getZAxisDirection() {
			return this.zAxisDirection;
		}
		
		public static Quadrant fromRandomIndex(int index) {
			return RANDOM_QUADRANTS[index % RANDOM_QUADRANTS.length];
		}
	}
}
