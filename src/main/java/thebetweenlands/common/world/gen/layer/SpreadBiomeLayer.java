package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public class SpreadBiomeLayer extends ZoomBiomeLayer {

	public static final MapCodec<SpreadBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					SpreadBiomeLayer.Quadrant.CODEC.fieldOf("quadrant").forGetter(o -> o.quadrant)
				).apply(instance, SpreadBiomeLayer::new)
		);

	protected final Quadrant quadrant;
	
	public SpreadBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, Quadrant quadrant) {
		super(registry, parent);
		this.quadrant = quadrant;
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
		// could probably be cleaner
		if(
				(pX == 0 && this.quadrant.getXAxisDirection() == AxisDirection.POSITIVE) ||
				(pX == 1 && this.quadrant.getXAxisDirection() == AxisDirection.NEGATIVE) ||
				(pZ == 0 && this.quadrant.getZAxisDirection() == AxisDirection.POSITIVE) ||
				(pZ == 1 && this.quadrant.getZAxisDirection() == AxisDirection.NEGATIVE)
			) {
			return -1;
		}

		return super.apply(context, parentArea, x, z);
	}

	public static enum Quadrant implements StringRepresentable {
		XNZN("XNZN", AxisDirection.NEGATIVE, AxisDirection.NEGATIVE),
		XNZP("XNZP", AxisDirection.NEGATIVE, AxisDirection.POSITIVE),
		XPZN("XPZN", AxisDirection.POSITIVE, AxisDirection.NEGATIVE),
		XPZP("XPZP", AxisDirection.POSITIVE, AxisDirection.POSITIVE);
		
        public static final Codec<SpreadBiomeLayer.Quadrant> CODEC = StringRepresentable.fromEnum(SpreadBiomeLayer.Quadrant::values);

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
	}
}
