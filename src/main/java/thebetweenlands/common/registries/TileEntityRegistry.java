package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityBarrel;
import thebetweenlands.common.tile.TileEntityBeamOrigin;
import thebetweenlands.common.tile.TileEntityBeamRelay;
import thebetweenlands.common.tile.TileEntityCenser;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDecayPitControl;
import thebetweenlands.common.tile.TileEntityDecayPitGroundChain;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityGroundItem;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityLootUrn;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMossBed;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudTilesSpikeTrap;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPresent;
import thebetweenlands.common.tile.TileEntityPuffshroom;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.common.tile.TileEntityRubberTap;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.common.tile.TileEntityTarLootPot1;
import thebetweenlands.common.tile.TileEntityTarLootPot2;
import thebetweenlands.common.tile.TileEntityTarLootPot3;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWisp;
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
		registerTileEntity(TileEntityMossBed.class, "moss_bed");
		registerTileEntity(TileEntityAspectVial.class, "aspect_vial");
		registerTileEntity(TileEntityAspectrusCrop.class, "aspectrus_crop");
		registerTileEntity(TileEntityRepeller.class, "repeller");
		registerTileEntity(TileEntityPresent.class, "present");
		registerTileEntity(TileEntityPuffshroom.class, "puffshroom");
		registerTileEntity(TileEntityBeamOrigin.class, "beam_origin");
		registerTileEntity(TileEntityBeamRelay.class, "beam_relay");
		registerTileEntity(TileEntityWaystone.class, "waystone");
		registerTileEntity(TileEntityMudBrickAlcove.class, "mud_bricks_alcove");
		registerTileEntity(TileEntityLootUrn.class, "loot_urn");
		registerTileEntity(TileEntityDungeonDoorRunes.class, "dungeon_door_runes");
		registerTileEntity(TileEntityDungeonDoorCombination.class, "dungeon_door_combination");
		registerTileEntity(TileEntityMudBricksSpikeTrap.class, "mud_bricks_spike_trap");
		registerTileEntity(TileEntityMudTilesSpikeTrap.class, "mud_tiles_spike_trap");
		registerTileEntity(TileEntityGroundItem.class, "ground_item");
		registerTileEntity(TileEntityDecayPitControl.class, "decay_pit_control");
		registerTileEntity(TileEntityDecayPitHangingChain.class, "decay_pit_hanging_chain");
		registerTileEntity(TileEntityDecayPitGroundChain.class, "decay_pit_ground_chain");
		registerTileEntity(TileEntityCenser.class, "censer");
		registerTileEntity(TileEntityBarrel.class, "tar_barrel");
		registerTileEntity(TileEntitySimulacrum.class, "simulacrum");
	}

	private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, new ResourceLocation(ModInfo.ID, baseName));
	}
}
