package thebetweenlands.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.placement.SimplexMaskPlacement;

public class PlacementRegistry {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT = DeferredRegister.create(Registry.PLACEMENT_MODIFIER_REGISTRY, TheBetweenlands.ID);

    public static final RegistryObject<PlacementModifierType<SimplexMaskPlacement>> SimplexMaskPlacementType = PLACEMENT.register("simplex_mask_placement", SimplexMaskPlacement::new);

    public static void register(IEventBus eventBus) {
        PLACEMENT.register(eventBus);
    }
}
