package thebetweenlands.common.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import thebetweenlands.common.registries.StructureTypeRegistry;

public class DruidCircleStructure extends SinglePieceStructure {

	public static final MapCodec<DruidCircleStructure> CODEC = simpleCodec(DruidCircleStructure::new);

	public DruidCircleStructure(StructureSettings settings) {
		super(DruidCirclePiece::new, 13, 13, settings);
	}

	@Override
	public StructureType<?> type() {
		return StructureTypeRegistry.DRUID_CIRCLE.get();
	}
}
