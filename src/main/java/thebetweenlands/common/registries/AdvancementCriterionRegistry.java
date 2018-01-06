package thebetweenlands.common.registries;

import net.minecraft.advancements.CriteriaTriggers;
import thebetweenlands.common.advancments.*;

public class AdvancementCriterionRegistry {

    public static final LocationTrigger LOCATION = CriteriaTriggers.register(new LocationTrigger());
    public static final EventTrigger EVENT = CriteriaTriggers.register(new EventTrigger());
    public static final ShockwaveKillTrigger SHOCKWAVE_KILL = CriteriaTriggers.register(new ShockwaveKillTrigger());
    public static final BreakBlockTrigger BREAK_BLOCK = CriteriaTriggers.register(new BreakBlockTrigger());
    public static final PyradAggroTrigger PYRAD_AGGRO = CriteriaTriggers.register(new PyradAggroTrigger());
    public static final GeckoTrigger GECKO_TRIGGER = CriteriaTriggers.register(new GeckoTrigger());
    public static final SwatShieldTrigger SWAT_SHIELD = CriteriaTriggers.register(new SwatShieldTrigger());

    public static void preInit() { }
}
