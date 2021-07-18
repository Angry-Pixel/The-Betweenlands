package thebetweenlands.common.registries;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.advancments.AnimateTrigger;
import thebetweenlands.common.advancments.BreakBlockTrigger;
import thebetweenlands.common.advancments.EquipTrigger;
import thebetweenlands.common.advancments.EventTrigger;
import thebetweenlands.common.advancments.GeckoTrigger;
import thebetweenlands.common.advancments.HasAdvancementTrigger;
import thebetweenlands.common.advancments.LocationTrigger;
import thebetweenlands.common.advancments.NoCriteriaTrigger;
import thebetweenlands.common.advancments.RightClickBlockTrigger;
import thebetweenlands.common.advancments.ShockwaveKillTrigger;
import thebetweenlands.common.advancments.SwatShieldTrigger;
import thebetweenlands.common.lib.ModInfo;

public class AdvancementCriterionRegistry {

	public static final LocationTrigger LOCATION = CriteriaTriggers.register(new LocationTrigger());
	public static final EventTrigger EVENT = CriteriaTriggers.register(new EventTrigger());
	public static final ShockwaveKillTrigger SHOCKWAVE_KILL = CriteriaTriggers.register(new ShockwaveKillTrigger());
	public static final BreakBlockTrigger BREAK_BLOCK = CriteriaTriggers.register(new BreakBlockTrigger());
	public static final GeckoTrigger GECKO_TRIGGER = CriteriaTriggers.register(new GeckoTrigger());
	public static final SwatShieldTrigger SWAT_SHIELD = CriteriaTriggers.register(new SwatShieldTrigger());
	public static final HasAdvancementTrigger HAS_ADVANCEMENT = CriteriaTriggers.register(new HasAdvancementTrigger());
	public static final EquipTrigger EQUIP = CriteriaTriggers.register(new EquipTrigger());
	public static final RightClickBlockTrigger CLICK_BLOCK = CriteriaTriggers.register(new RightClickBlockTrigger());
	public static final AnimateTrigger ANIMATE = CriteriaTriggers.register(new AnimateTrigger());

	public static final NoCriteriaTrigger CAVINGROPE_PLACED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "cavingrope_placed")));
	public static final NoCriteriaTrigger CHIROMAW_MATRIARCH_NEST_CLAIMED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "chiromaw_matriarch_nest_claimed")));
	public static final NoCriteriaTrigger CHIROMAW_MATRIARCH_NEST_RAIDED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "chiromaw_matriarch_nest_raided")));
	public static final NoCriteriaTrigger PEAT_FIRE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "peat_fire")));
	public static final NoCriteriaTrigger PYRAD_AGGRO = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "pyrad_aggro")));
	public static final NoCriteriaTrigger CRAGROCK_TOP = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "cragrock_tower_top")));
	public static final NoCriteriaTrigger OCTINE_INGOT_FIRE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "octine_ingot_fire")));
	public static final NoCriteriaTrigger MIDDLE_GEM_UPGRADE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "middle_gem_upgrade")));
	public static final NoCriteriaTrigger SPORELING_HATCH = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "sporeling_hatch")));
	public static final NoCriteriaTrigger SICKLE_USE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "sickle_use")));
	public static final NoCriteriaTrigger HERBLORE_FIND_ALL = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "herblore_find_all")));
	public static final NoCriteriaTrigger PURIFY_TOOL = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "purify_tool")));
	public static final NoCriteriaTrigger COAT_TOOL = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "coat_tool")));
	public static final NoCriteriaTrigger DROP_SHIMMERSTONE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "drop_shimmerstone")));
	public static final NoCriteriaTrigger FLUX_ADDED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "flux_added")));
	public static final NoCriteriaTrigger DAMP_TORCH_PLACED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "damp_torch_placed")));
	public static final NoCriteriaTrigger ROLLED_OVER_BY_BOULDER_SPRITE = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "rolled_over_by_boulder_sprite")));
	public static final NoCriteriaTrigger REVIVED_BLESSED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "revive_blessed")));
	public static final NoCriteriaTrigger WIND_CHIME_PREDICTION = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "wind_chime_prediction")));
	public static final NoCriteriaTrigger STRUCK_BY_LIGHTNING_WHILE_FLYING = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "struck_by_lightning_while_flying")));
	public static final NoCriteriaTrigger SMOKE_ITEM = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "take_item_from_output_smoking_rack")));
	public static final NoCriteriaTrigger TRIM_FISH = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "take_item_from_output_trimming_table")));
	public static final NoCriteriaTrigger CRAB_FILTER = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "take_item_from_output_crab_pot_filter")));
	public static final NoCriteriaTrigger CRAB_POT = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "crab_in_pot")));
	public static final NoCriteriaTrigger USED_ROD_ON_JAR = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "use_worm_jar")));
	public static final NoCriteriaTrigger INFESTED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "infested")));

	public static void preInit() { }
}
