package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRubberTap;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.common.tile.spawner.TileEntityAlembic;
import thebetweenlands.common.tile.spawner.TileEntityMobSpawnerBetweenlands;

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
	}

	private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, "tile.thebetweenlands." + baseName);
	}
}
