package thebetweenlands.api.world.biome;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;

public record CarvingMasks(Map<GenerationStep.Carving, CarvingMask> carvingMasks, CarvingMask airCarvingMask, CarvingMask liquidCarvingMask) {

	// TODO only memoize carving masks, not get them immediately
	public static CarvingMasks from(ProtoChunk protoChunk) {
		Map<GenerationStep.Carving, CarvingMask> carvingMasks = new EnumMap<>(GenerationStep.Carving.class);
		CarvingMask airCarvingMask = null, liquidCarvingMask = null;
		for(GenerationStep.Carving step : GenerationStep.Carving.values()) {
			CarvingMask carvingMask = protoChunk.getCarvingMask(step);
			if(step == Carving.AIR) {
				airCarvingMask = carvingMask;
			} else if(step == Carving.LIQUID) {
				liquidCarvingMask = carvingMask;
			}
			
			carvingMasks.put(step, liquidCarvingMask);
		}
		
		return new CarvingMasks(carvingMasks, airCarvingMask, liquidCarvingMask);
	}
	
}
