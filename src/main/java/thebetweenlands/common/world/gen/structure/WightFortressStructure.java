package thebetweenlands.common.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.StructureTypeRegistry;

import java.util.Optional;
import java.util.function.Consumer;

public class WightFortressStructure extends Structure {

	private static final ResourceLocation PIECE_LOCATION = TheBetweenlands.prefix("wight_fortress");

	public static final MapCodec<WightFortressStructure> CODEC = simpleCodec(WightFortressStructure::new);

	public WightFortressStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
		return placeAndAccountForBasement(context, Heightmap.Types.WORLD_SURFACE_WG, p_227598_ -> this.generatePieces(p_227598_, context));
	}

	private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
		ChunkPos chunkpos = context.chunkPos();
		WorldgenRandom worldgenrandom = context.random();
		BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), context.chunkGenerator().getSeaLevel() - 7, chunkpos.getMinBlockZ());
		Rotation rotation = Rotation.getRandom(worldgenrandom);
		builder.addPiece(new WightFortressPiece(context.structureTemplateManager(), PIECE_LOCATION, blockpos, rotation));
	}

	protected static Optional<Structure.GenerationStub> placeAndAccountForBasement(
		Structure.GenerationContext context, Heightmap.Types heightmapTypes, Consumer<StructurePiecesBuilder> generator
	) {
		ChunkPos chunkpos = context.chunkPos();
		int i = chunkpos.getMiddleBlockX();
		int j = chunkpos.getMiddleBlockZ();
		int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, heightmapTypes, context.heightAccessor(), context.randomState()) - 7;
		return Optional.of(new Structure.GenerationStub(new BlockPos(i, k, j), generator));
	}

	@Override
	public StructureType<?> type() {
		return StructureTypeRegistry.WIGHT_FORTRESS.get();
	}
}
