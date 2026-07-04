package thebetweenlands.common.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import thebetweenlands.common.registries.StructureTypeRegistry;

public class SpiritTreeStructure extends SinglePieceStructure {

	public static final MapCodec<SpiritTreeStructure> CODEC = simpleCodec(SpiritTreeStructure::new);

	public SpiritTreeStructure(StructureSettings settings) {
		super(SpiritTreePiece::new, 64, 64, settings);
	}

	@Override
	public StructureType<?> type() {
		return StructureTypeRegistry.SPIRIT_TREE.get();
	}
}
