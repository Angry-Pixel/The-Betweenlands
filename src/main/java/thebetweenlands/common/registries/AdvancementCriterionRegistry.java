package thebetweenlands.common.registries;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.advancments.*;
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
    public static final NoCriteriaTrigger CAVINGROPE_PLACED = CriteriaTriggers.register(new NoCriteriaTrigger(new ResourceLocation(ModInfo.ID, "cavingrope_placed")));

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

    public static void preInit() { }
}
