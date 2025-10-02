package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datamap.entity.AmuletSpawn;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.datamap.item.DecayFood;
import thebetweenlands.common.datamap.item.FluxMultiplier;

public class DataMapRegistry {

	public static final DataMapType<EntityType<?>, AmuletSpawn> AMULET_SPAWNS = DataMapType.builder(TheBetweenlands.prefix("amulet_spawn"), Registries.ENTITY_TYPE, AmuletSpawn.CODEC).synced(AmuletSpawn.CODEC, false).build();

	public static final DataMapType<Item, CompostableItem> COMPOSTABLE = DataMapType.builder(TheBetweenlands.prefix("compost_bin_compostables"), Registries.ITEM, CompostableItem.CODEC).synced(CompostableItem.CODEC, false).build();
	public static final DataMapType<Item, DecayFood> DECAY_FOOD = DataMapType.builder(TheBetweenlands.prefix("decay_foods"), Registries.ITEM, DecayFood.CODEC).synced(DecayFood.CODEC, false).build();
	public static final DataMapType<Item, FluxMultiplier> FLUX_MULTIPLIER = DataMapType.builder(TheBetweenlands.prefix("flux_multiplier"), Registries.ITEM, FluxMultiplier.CODEC).synced(FluxMultiplier.CODEC, false).build();
}
