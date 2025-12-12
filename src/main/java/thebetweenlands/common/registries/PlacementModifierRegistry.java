package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.placement.CragSpiresPlacement;
import thebetweenlands.common.world.gen.placement.SimplexColumnsPlacement;

public class PlacementModifierRegistry {

	public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<SimplexColumnsPlacement>> SIMPLEX_COLUMNS_PLACEMENT = PLACEMENT_MODIFIER_TYPES.register("simplex_columns", () -> () -> SimplexColumnsPlacement.CODEC);
	public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<CragSpiresPlacement>> CRAG_SPIRES_PLACEMENT = PLACEMENT_MODIFIER_TYPES.register("crag_spires", () -> () -> CragSpiresPlacement.CODEC);
}
