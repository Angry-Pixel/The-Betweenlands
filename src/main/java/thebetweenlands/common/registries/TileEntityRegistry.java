package thebetweenlands.common.registries;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityAlembic;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;
import thebetweenlands.common.tile.TileEntityChestBetweenlands;
import thebetweenlands.common.tile.TileEntityCompostBin;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.common.tile.TileEntityItemCage;
import thebetweenlands.common.tile.TileEntityItemShelf;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.common.tile.TileEntityMortar;
import thebetweenlands.common.tile.TileEntityMossBed;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.common.tile.TileEntityPresent;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.common.tile.TileEntityRubberTap;
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

@ObjectHolder(ModInfo.ID)
public class TileEntityRegistry {
	@ObjectHolder("druid_altar")
	public static final TileEntityType<TileEntityDruidAltar> DRUID_ALTAR = null;

	@ObjectHolder("purifier")
	public static final TileEntityType<TileEntityPurifier> PURIFIER = null;

	@ObjectHolder("weedwood_workbench")
	public static final TileEntityType<TileEntityWeedwoodWorkbench> WEEDWOOD_WORKBENCH = null;

	@ObjectHolder("compost_bin")
	public static final TileEntityType<TileEntityCompostBin> COMPOST_BIN = null;

	@ObjectHolder("loot_pot")
	public static final TileEntityType<TileEntityLootPot> LOOT_POT = null;

	@ObjectHolder("mob_spawner")
	public static final TileEntityType<TileEntityMobSpawnerBetweenlands> MOB_SPAWNER = null;

	@ObjectHolder("wisp")
	public static final TileEntityType<TileEntityWisp> WISP = null;

	@ObjectHolder("sulfur_furnace")
	public static final TileEntityType<TileEntityBLFurnace> SULFUR_FURNACE = null;

	@ObjectHolder("sulfur_furnace_dual")
	public static final TileEntityType<TileEntityBLDualFurnace> DUAL_SULFUR_FURNACE = null;

	@ObjectHolder("betweenlands_chest")
	public static final TileEntityType<TileEntityChestBetweenlands> WEEDWOOD_CHEST = null;

	@ObjectHolder("rubber_tap")
	public static final TileEntityType<TileEntityRubberTap> RUBBER_TAP = null;

	@ObjectHolder("spike_trap")
	public static final TileEntityType<TileEntitySpikeTrap> SPIKE_TRAP = null;

	@ObjectHolder("possessed_block")
	public static final TileEntityType<TileEntityPossessedBlock> POSSESSED_BLOCK = null;

	@ObjectHolder("item_cage")
	public static final TileEntityType<TileEntityItemCage> ITEM_CAGE = null;

	@ObjectHolder("weedwood_sign")
	public static final TileEntityType<TileEntityWeedwoodSign> SIGN = null;

	@ObjectHolder("gecko_cage")
	public static final TileEntityType<TileEntityGeckoCage> GECKO_CAGE = null;

	@ObjectHolder("infuser")
	public static final TileEntityType<TileEntityInfuser> INFUSER = null;

	@ObjectHolder("mortar")
	public static final TileEntityType<TileEntityMortar> MORTAR = null;

	@ObjectHolder("animator")
	public static final TileEntityType<TileEntityAnimator> ANIMATOR = null;

	@ObjectHolder("alembic")
	public static final TileEntityType<TileEntityAlembic> ALEMBIC = null;

	@ObjectHolder("dug_soil")
	public static final TileEntityType<TileEntityDugSoil> DUG_SOIL = null;

	@ObjectHolder("item_shelf")
	public static final TileEntityType<TileEntityItemShelf> ITEM_SHELF = null;

	@ObjectHolder("tar_beast_spawner")
	public static final TileEntityType<TileEntityTarBeastSpawner> TAR_BEAST_SPAWNER = null;

	@ObjectHolder("tar_loot_pot_1")
	public static final TileEntityType<TileEntityTarLootPot1> TAR_LOOT_POT_1 = null;

	@ObjectHolder("tar_loot_pot_2")
	public static final TileEntityType<TileEntityTarLootPot2> TAR_LOOT_POT_2 = null;

	@ObjectHolder("tar_loot_pot_3")
	public static final TileEntityType<TileEntityTarLootPot3> TAR_LOOT_POT_3 = null;

	@ObjectHolder("syrmorite_hopper")
	public static final TileEntityType<TileEntityHopperBetweenlands> SYRMORITE_HOPPER = null;

	@ObjectHolder("moss_bed")
	public static final TileEntityType<TileEntityMossBed> MOSS_BED = null;

	@ObjectHolder("aspect_vial")
	public static final TileEntityType<TileEntityAspectVial> ASPECT_VIAL = null;

	@ObjectHolder("aspectrus_crop")
	public static final TileEntityType<TileEntityAspectrusCrop> ASPECTRUS_CROP = null;

	@ObjectHolder("repeller")
	public static final TileEntityType<TileEntityRepeller> REPELLER = null;

	@ObjectHolder("present")
	public static final TileEntityType<TileEntityPresent> PRESENT = null;

	@ObjectHolder("waystone")
	public static final TileEntityType<TileEntityWaystone> WAYSTONE = null;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

		register(new RegistryHelper<TileEntityType.Builder<?>>() {
			@Override
			public <F extends TileEntityType.Builder<?>> F reg(String regName, F obj, @Nullable Consumer<F> callback) {
				Type<?> fixerType = null;

				//TODO 1.13 Entity registering like this? Or use TileEntityType.register(...)?
				try {
					fixerType = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.BLOCK_ENTITY, ModInfo.ID + ":" + regName);
				} catch (IllegalArgumentException illegalstateexception) {
					if (SharedConstants.developmentMode) {
						throw illegalstateexception;
					}

					TheBetweenlands.logger.warn("No data fixer registered for Betweenlands block entity {}", ModInfo.ID + ":" + regName);
				}

				TileEntityType<?> type = obj.build(fixerType);
				type.setRegistryName(ModInfo.ID, regName);
				registry.register(type);
				return obj;
			}
		});
	}

	private static void register(RegistryHelper<TileEntityType.Builder<?>> reg) {
		reg.reg("druid_altar", TileEntityType.Builder.create(TileEntityDruidAltar::new));
		reg.reg("purifier", TileEntityType.Builder.create(TileEntityPurifier::new));
		reg.reg("weedwood_workbench", TileEntityType.Builder.create(TileEntityWeedwoodWorkbench::new));
		reg.reg("compost_bin", TileEntityType.Builder.create(TileEntityCompostBin::new));
		reg.reg("loot_pot", TileEntityType.Builder.create(TileEntityLootPot::new));
		reg.reg("mob_spawner", TileEntityType.Builder.create(TileEntityMobSpawnerBetweenlands::new));
		reg.reg("wisp", TileEntityType.Builder.create(TileEntityWisp::new));
		reg.reg("sulfur_furnace", TileEntityType.Builder.create(TileEntityBLFurnace::new));
		reg.reg("sulfur_furnace_dual", TileEntityType.Builder.create(TileEntityBLDualFurnace::new));
		reg.reg("betweenlands_chest", TileEntityType.Builder.create(TileEntityChestBetweenlands::new));
		reg.reg("rubber_tap", TileEntityType.Builder.create(TileEntityRubberTap::new));
		reg.reg("spike_trap", TileEntityType.Builder.create(TileEntitySpikeTrap::new));
		reg.reg("possessed_block", TileEntityType.Builder.create(TileEntityPossessedBlock::new));
		reg.reg("item_cage", TileEntityType.Builder.create(TileEntityItemCage::new));
		reg.reg("weedwood_sign", TileEntityType.Builder.create(TileEntityWeedwoodSign::new));
		//TODO 1.13 Flower pot TE removed due to flattening
		//reg.reg("mud_flower_pot", TileEntityType.Builder.create(TileEntityMudFlowerPot::new));
		reg.reg("gecko_cage", TileEntityType.Builder.create(TileEntityGeckoCage::new));
		reg.reg("infuser", TileEntityType.Builder.create(TileEntityInfuser::new));
		reg.reg("mortar", TileEntityType.Builder.create(TileEntityMortar::new));
		reg.reg("animator", TileEntityType.Builder.create(TileEntityAnimator::new));
		reg.reg("alembic", TileEntityType.Builder.create(TileEntityAlembic::new));
		reg.reg("dug_soil", TileEntityType.Builder.create(TileEntityDugSoil::new));
		reg.reg("item_shelf", TileEntityType.Builder.create(TileEntityItemShelf::new));
		reg.reg("tar_beast_spawner", TileEntityType.Builder.create(TileEntityTarBeastSpawner::new));
		reg.reg("tar_loot_pot_1", TileEntityType.Builder.create(TileEntityTarLootPot1::new));
		reg.reg("tar_loot_pot_2", TileEntityType.Builder.create(TileEntityTarLootPot2::new));
		reg.reg("tar_loot_pot_3", TileEntityType.Builder.create(TileEntityTarLootPot3::new));
		reg.reg("syrmorite_hopper", TileEntityType.Builder.create(TileEntityHopperBetweenlands::new));
		reg.reg("moss_bed", TileEntityType.Builder.create(TileEntityMossBed::new));
		reg.reg("aspect_vial", TileEntityType.Builder.create(TileEntityAspectVial::new));
		reg.reg("aspectrus_crop", TileEntityType.Builder.create(TileEntityAspectrusCrop::new));
		reg.reg("repeller", TileEntityType.Builder.create(TileEntityRepeller::new));
		reg.reg("present", TileEntityType.Builder.create(TileEntityPresent::new));
		reg.reg("waystone", TileEntityType.Builder.create(TileEntityWaystone::new));
	}
}
