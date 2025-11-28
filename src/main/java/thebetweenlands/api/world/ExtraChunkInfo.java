package thebetweenlands.api.world;

import java.util.EnumSet;
import java.util.Optional;

public record ExtraChunkInfo(EnumSet<ExtraChunkInfoTypes> mask, Optional<BiomeWeights> biomeWeights) {

}
