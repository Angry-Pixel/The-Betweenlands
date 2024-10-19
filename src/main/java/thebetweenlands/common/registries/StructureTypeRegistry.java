package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.structure.DruidCirclePiece;
import thebetweenlands.common.world.gen.structure.DruidCircleStructure;

public class StructureTypeRegistry {
	public static final DeferredRegister<StructureType<?>> TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, TheBetweenlands.ID);
	public static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, TheBetweenlands.ID);

	public static final DeferredHolder<StructureType<?>, StructureType<DruidCircleStructure>> DRUID_CIRCLE = TYPES.register("druid_circle", () -> () -> DruidCircleStructure.CODEC);
	public static final DeferredHolder<StructurePieceType, StructurePieceType> DRUID_CIRCLE_PIECE = registerPiece("druid_circle", DruidCirclePiece::new);

	private static DeferredHolder<StructurePieceType, StructurePieceType> registerPiece(String name, StructurePieceType structurePieceType) {
		return PIECE_TYPES.register(name, () -> structurePieceType);
	}
}
