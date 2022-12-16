package thebetweenlands.common.registries;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.lib.ModInfo;
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
		registerTileEntity(TileEntityOfferingTable.class, "offering_bowl");
		registerTileEntity(TileEntityWindChime.class, "wind_chime");
		registerTileEntity(TileEntityFishingTackleBox.class, "fishing_tackle_box");
		registerTileEntity(TileEntitySmokingRack.class, "smoking_rack");
		registerTileEntity(TileEntityFishTrimmingTable.class, "fish_trimming_table");
		registerTileEntity(TileEntityCrabPot.class, "crab_pot");
		registerTileEntity(TileEntityCrabPotFilter.class, "crab_pot_filter");
		registerTileEntity(TileEntitySiltGlassJar.class, "silt_glass_jar");
		registerTileEntity(TileEntityMudLootPot1.class, "mud_loot_pot_1");
		registerTileEntity(TileEntityMudLootPot2.class, "mud_loot_pot_2");
		registerTileEntity(TileEntityMudLootPot3.class, "mud_loot_pot_3");
		registerTileEntity(TileEntitySteepingPot.class, "steeping_pot");
		registerTileEntity(TileEntityGrubHub.class, "grub_hub");
		registerTileEntity(TileEntityWaterFilter.class, "water_filter");
		registerTileEntity(TileEntityFilteredSiltGlassJar.class, "filtered_silt_glass_jar");
		registerTileEntity(TileEntityMothHouse.class, "moth_house");
	}

	private static void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
		GameRegistry.registerTileEntity(cls, new ResourceLocation(ModInfo.ID, baseName));
	}
}
