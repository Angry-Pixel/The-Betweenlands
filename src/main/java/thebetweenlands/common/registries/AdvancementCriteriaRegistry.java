package thebetweenlands.common.registries;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.AnimateTrigger;
import thebetweenlands.common.advancement.BreakBlockTrigger;
import thebetweenlands.common.advancement.NoCriteriaTrigger;

public class AdvancementCriteriaRegistry {

	public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<CriterionTrigger<?>, AnimateTrigger> ANIMATE = TRIGGERS.register("animate_item", AnimateTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, BreakBlockTrigger> BREAK_BLOCK = TRIGGERS.register("break_block", BreakBlockTrigger::new);


	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CAVING_ROPE_PLACED = TRIGGERS.register("caving_rope_placed", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CHIROMAW_MATRIARCH_NEST_CLAIMED = TRIGGERS.register("chiromaw_matriarch_nest_claimed", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CHIROMAW_MATRIARCH_NEST_RAIDED = TRIGGERS.register("chiromaw_matriarch_nest_raided", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> PEAT_FIRE = TRIGGERS.register("peat_fire", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> PYRAD_AGGRO = TRIGGERS.register("pyrad_aggro", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CRAGROCK_TOP = TRIGGERS.register("cragrock_tower_top", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> OCTINE_INGOT_FIRE = TRIGGERS.register("octine_ingot_fire", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> MIDDLE_GEM_UPGRADE = TRIGGERS.register("middle_gem_upgrade", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> SPORELING_HATCH = TRIGGERS.register("sporeling_hatch", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> SICKLE_USE = TRIGGERS.register("sickle_use", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> HERBLORE_FIND_ALL = TRIGGERS.register("herblore_find_all", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> PURIFY_TOOL = TRIGGERS.register("purify_tool", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> COAT_TOOL = TRIGGERS.register("coat_tool", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> DROP_SHIMMERSTONE = TRIGGERS.register("drop_shimmerstone", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> FLUX_ADDED = TRIGGERS.register("flux_added", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> DAMP_TORCH_PLACED = TRIGGERS.register("damp_torch_placed", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> ROLLED_OVER_BY_BOULDER_SPRITE = TRIGGERS.register("rolled_over_by_boulder_sprite", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> REVIVED_BLESSED = TRIGGERS.register("revive_blessed", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> WIND_CHIME_PREDICTION = TRIGGERS.register("wind_chime_prediction", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> STRUCK_BY_LIGHTNING_WHILE_FLYING = TRIGGERS.register("struck_by_lightning_while_flying", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> SMOKE_ITEM = TRIGGERS.register("take_item_from_output_smoking_rack", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> TRIM_FISH = TRIGGERS.register("take_item_from_output_trimming_table", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CRAB_FILTER = TRIGGERS.register("take_item_from_output_crab_pot_filter", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> CRAB_POT = TRIGGERS.register("crab_in_pot", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> USED_ROD_ON_JAR = TRIGGERS.register("use_worm_jar", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> WORM_FROM_DIRT = TRIGGERS.register("worm_from_dirt", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> INFESTED = TRIGGERS.register("infested", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> SIT_ON_TACKLE_BOX = TRIGGERS.register("sit_on_tackle_box", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> USED_FUMIGANT = TRIGGERS.register("used_fumigant", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> BREAK_ALCOVE_URN = TRIGGERS.register("break_alcove_urn", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> MOTH_HOUSE_MAXED = TRIGGERS.register("moth_house_maxed", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> ITEM_FRAME_INVISIBLE = TRIGGERS.register("item_frame_invisible", NoCriteriaTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, NoCriteriaTrigger> ITEM_FRAME_GLOWING = TRIGGERS.register("item_frame_glowing", NoCriteriaTrigger::new);

}
