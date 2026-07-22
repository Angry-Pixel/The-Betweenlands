package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datamap.block.WaterPlant;
import thebetweenlands.common.datamap.entity.AmuletSpawn;
import thebetweenlands.common.datamap.item.AnimatorFuel;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.datamap.item.DecayFood;
import thebetweenlands.common.datamap.item.FluxMultiplier;
import thebetweenlands.common.datamap.item.LightningConversion;
import thebetweenlands.common.world.spawning.BiomeSpawnerList;

public class DataMapRegistry {
	
	public static final DataMapType<Biome, BiomeSpawnerList> BIOME_SPAWNS = DataMapType.builder(TheBetweenlands.prefix("biome_spawns"), Registries.BIOME, BiomeSpawnerList.CODEC).synced(BiomeSpawnerList.CODEC, false).build();
	public static final DataMapType<EntityType<?>, AmuletSpawn> AMULET_SPAWNS = DataMapType.builder(TheBetweenlands.prefix("amulet_spawn"), Registries.ENTITY_TYPE, AmuletSpawn.CODEC).synced(AmuletSpawn.CODEC, false).build();

	public static final DataMapType<Item, AnimatorFuel> ANIMATOR_FUEL = DataMapType.builder(TheBetweenlands.prefix("animator_fuel"), Registries.ITEM, AnimatorFuel.CODEC).synced(AnimatorFuel.CODEC, false).build();
	public static final DataMapType<Item, CompostableItem> COMPOSTABLE = DataMapType.builder(TheBetweenlands.prefix("compost_bin_compostables"), Registries.ITEM, CompostableItem.CODEC).synced(CompostableItem.CODEC, false).build();
	public static final DataMapType<Item, DecayFood> DECAY_FOOD = DataMapType.builder(TheBetweenlands.prefix("decay_foods"), Registries.ITEM, DecayFood.CODEC).synced(DecayFood.CODEC, false).build();
	public static final DataMapType<Item, FluxMultiplier> FLUX_MULTIPLIER = DataMapType.builder(TheBetweenlands.prefix("flux_multiplier"), Registries.ITEM, FluxMultiplier.CODEC).synced(FluxMultiplier.CODEC, false).build();
	public static final DataMapType<Item, LightningConversion> LIGHTNING_CONVERSION = DataMapType.builder(TheBetweenlands.prefix("lightning_conversion"), Registries.ITEM, LightningConversion.CODEC).synced(LightningConversion.CODEC, false).build();

	public static final DataMapType<Block, WaterPlant> WATER_PLANT = DataMapType.builder(TheBetweenlands.prefix("water_plant"), Registries.BLOCK, WaterPlant.CODEC).synced(WaterPlant.CODEC, false).build();
}
