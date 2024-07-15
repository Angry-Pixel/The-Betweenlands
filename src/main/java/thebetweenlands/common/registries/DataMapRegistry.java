package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.datamaps.FluxMultiplier;

public class DataMapRegistry {

	public static final DataMapType<Item, FluxMultiplier> FLUX_MULTIPLIER = DataMapType.builder(TheBetweenlands.prefix("flux_multiplier"), Registries.ITEM, FluxMultiplier.CODEC).synced(FluxMultiplier.CODEC, false).build();
}
