package thebetweenlands.common.registries;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.simulacrum.*;

public class SimulacrumEffectRegistry {

	public static final DeferredRegister<SimulacrumEffect> EFFECTS = DeferredRegister.create(BLRegistries.Keys.SIMULACRUM_EFFECTS, TheBetweenlands.ID);

	public static final DeferredHolder<SimulacrumEffect, NoneSimulacrumEffect> NONE = EFFECTS.register("none", NoneSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, RandomSimulacrumEffect> RANDOM = EFFECTS.register("random", RandomSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, ThemSimulacrumEffect> THEM = EFFECTS.register("them", ThemSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, ImitationSimulacrumEffect> IMITATION = EFFECTS.register("imitation", ImitationSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, NoneSimulacrumEffect> WEAKNESS = EFFECTS.register("weakness", NoneSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, NoneSimulacrumEffect> RESURRECTION = EFFECTS.register("resurrection", NoneSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, SanctuarySimulacrumEffect> SANCTUARY = EFFECTS.register("sanctuary", SanctuarySimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, FertilitySimulacrumEffect> FERTILITY = EFFECTS.register("fertility", FertilitySimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, NoneSimulacrumEffect> ATTRACTION = EFFECTS.register("attraction", NoneSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, WispSimulacrumEffect> WISP = EFFECTS.register("wisp", WispSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, WisdomSimulacrumEffect> WISDOM = EFFECTS.register("wisdom", WisdomSimulacrumEffect::new);
	public static final DeferredHolder<SimulacrumEffect, BlessingSimulacrumEffect> BLESSING = EFFECTS.register("blessing", BlessingSimulacrumEffect::new);

}
