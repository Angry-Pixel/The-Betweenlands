package thebetweenlands.common.world.gen.placement.util;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;

public class BLPlacementModifierHelper {

	public static boolean checkBiomeAt(PlacementContext pContext, BlockPos pos, BiomeCheckContext bContext) {
		// Feature "should not restrict biome" according to BiomeFilter
		if(bContext.topFeature.isEmpty()) {
			return true;
		}
		
		PlacedFeature placedFeature = bContext.topFeature.get();
		
		Holder<Biome> biome = pContext.getLevel().getBiome(pos);
		
		if(biome == null) {
			return false;
		}
		
		if(biome == bContext.prevBiome) {
			return bContext.prevBiomeHadFeature;
		}
		
		// This is how BiomeFilter does it
		@SuppressWarnings("deprecation")
		boolean hasFeature = pContext.generator().getBiomeGenerationSettings(biome).hasFeature(placedFeature);
		
		bContext.prevBiome = biome;
		bContext.prevBiomeHadFeature = hasFeature;
		
		return hasFeature;
	}
	
	public static class BiomeCheckContext {
	
		public final Optional<PlacedFeature> topFeature;
		public Holder<Biome> prevBiome = null;
		public boolean prevBiomeHadFeature = false;

		public BiomeCheckContext(PlacementContext context) {
			this(context.topFeature());
		}
		
		public BiomeCheckContext(Optional<PlacedFeature> topFeature) {
			this.topFeature = topFeature;
		}
		
	}
}
