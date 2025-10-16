package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.effects.vanilla.*;

public class MobEffectRegistry {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TheBetweenlands.ID);

	public static final DeferredHolder<MobEffect, MobEffect> PETRIFY = EFFECTS.register("petrify", () -> new PetrifyEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("petrify_slowdown"), -1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	public static final DeferredHolder<MobEffect, MobEffect> BLESSED = EFFECTS.register("blessed", BlessedEffect::new);
	public static final DeferredHolder<MobEffect, MobEffect> SHOCKED = EFFECTS.register("shocked", () -> new ShockedEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("shocked_slowdown"), -0.95f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	public static final DeferredHolder<MobEffect, MobEffect> ROOT_BOUND = EFFECTS.register("root_bound", () -> new RootBoundEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, TheBetweenlands.prefix("root_bound_slowdown"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	public static final DeferredHolder<MobEffect, MobEffect> ENLIGHTENED = EFFECTS.register("enlightened", EnlightenedEffect::new);
}
