package thebetweenlands.common.registries;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datamap.block.WaterPlant;
import thebetweenlands.common.datamap.entity.AmuletSpawn;
import thebetweenlands.common.datamap.entity.MobSpawnDataMap;
import thebetweenlands.common.datamap.item.AnimatorFuel;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.datamap.item.DecayFood;
import thebetweenlands.common.datamap.item.FluxMultiplier;
import thebetweenlands.common.datamap.item.LightningConversion;
import thebetweenlands.common.world.spawning.ICustomSpawnEntry;

public class DataMapRegistry {
	
    public static final DataMapType<EntityType<?>, List<ICustomSpawnEntry>> CUSTOM_SPAWNS = DataMapType.builder(TheBetweenlands.prefix("custom_spawns"), Registries.ENTITY_TYPE, MobSpawnDataMap.MASTER_LIST_CODEC).synced(MobSpawnDataMap.MASTER_LIST_CODEC, false).build();

	public static final DataMapType<EntityType<?>, AmuletSpawn> AMULET_SPAWNS = DataMapType.builder(TheBetweenlands.prefix("amulet_spawn"), Registries.ENTITY_TYPE, AmuletSpawn.CODEC).synced(AmuletSpawn.CODEC, false).build();

	public static final DataMapType<Item, AnimatorFuel> ANIMATOR_FUEL = DataMapType.builder(TheBetweenlands.prefix("animator_fuel"), Registries.ITEM, AnimatorFuel.CODEC).synced(AnimatorFuel.CODEC, false).build();
	public static final DataMapType<Item, CompostableItem> COMPOSTABLE = DataMapType.builder(TheBetweenlands.prefix("compost_bin_compostables"), Registries.ITEM, CompostableItem.CODEC).synced(CompostableItem.CODEC, false).build();
	public static final DataMapType<Item, DecayFood> DECAY_FOOD = DataMapType.builder(TheBetweenlands.prefix("decay_foods"), Registries.ITEM, DecayFood.CODEC).synced(DecayFood.CODEC, false).build();
	public static final DataMapType<Item, FluxMultiplier> FLUX_MULTIPLIER = DataMapType.builder(TheBetweenlands.prefix("flux_multiplier"), Registries.ITEM, FluxMultiplier.CODEC).synced(FluxMultiplier.CODEC, false).build();
	public static final DataMapType<Item, LightningConversion> LIGHTNING_CONVERSION = DataMapType.builder(TheBetweenlands.prefix("lightning_conversion"), Registries.ITEM, LightningConversion.CODEC).synced(LightningConversion.CODEC, false).build();

	public static final DataMapType<Block, WaterPlant> WATER_PLANT = DataMapType.builder(TheBetweenlands.prefix("water_plant"), Registries.BLOCK, WaterPlant.CODEC).synced(WaterPlant.CODEC, false).build();
}
