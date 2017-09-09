package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.tile.*;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityTarBeastSpawner;

public class TileEntityRegistry {
	private TileEntityRegistry() { }

	public static void init() {
		registerTileEntity(TileEntityDruidAltar.class, "druid_altar");
		registerTileEntity(TileEntityPurifier.class, "purifier");
		registerTileEntity(TileEntityWeedwoodWorkbench.class, "weedwood_workbench");
		registerTileEntity(TileEntityCompostBin.class, "compost_bin");
		registerTileEntity(TileEntityLootPot.class, "loot_pot");
		registerTileEntity(TileEntityMobSpawnerBetweenlands.class, "mob_spawner");
		registerTileEntity(TileEntityWisp.class, "wisp");
		registerTileEntity(TileEntityBLFurnace.class, "sulfur_furnace");
		registerTileEntity(TileEntityBLDualFurnace.class, "sulfur_furnace_dual");
		registerTileEntity(TileEntityChestBetweenlands.class, "betweenlands_chest");
		registerTileEntity(TileEntityRubberTap.class, "rubber_tap");
		registerTileEntity(TileEntitySpikeTrap.class, "spike_trap");
		registerTileEntity(TileEntityPossessedBlock.class, "possessed_block");
		registerTileEntity(TileEntityItemCage.class, "item_cage");
		registerTileEntity(TileEntityWeedwoodSign.class, "weedwood_sign");
		registerTileEntity(TileEntityMudFlowerPot.class, "mud_flower_pot");
		registerTileEntity(TileEntityGeckoCage.class, "gecko_cage");
		registerTileEntity(TileEntityInfuser.class, "infuser");
		registerTileEntity(TileEntityMortar.class, "mortar");
		registerTileEntity(TileEntityAnimator.class, "animator");
		registerTileEntity(TileEntityAlembic.class, "alembic");
		registerTileEntity(TileEntityDugSoil.class, "dug_soil");
		registerTileEntity(TileEntityItemShelf.class, "item_shelf");
		registerTileEntity(TileEntityTarBeastSpawner.class, "tar_beast_spawner");
		registerTileEntity(TileEntityTarLootPot1.class, "tar_loot_pot_1");
		registerTileEntity(TileEntityTarLootPot2.class, "tar_loot_pot_2");
		registerTileEntity(TileEntityTarLootPot3.class, "tar_loot_pot_3");
		registerTileEntity(TileEntityHopperBetweenlands.class, "syrmorite_hopper");
	}

	private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}
}
