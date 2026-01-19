package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class AttributeRegistry {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TheBetweenlands.ID);

	public static final Holder<Attribute> VOLATILE_HEALTH_START = ATTRIBUTES.register("volatile_health_start", () -> new RangedAttribute("volatile_health", 1.0D, 0.0D, 1.0D).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_COOLDOWN = ATTRIBUTES.register("volatile_cooldown", () -> new RangedAttribute("volatile_cooldown", 400.0D, 10.0D, Integer.MAX_VALUE).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_LENGTH = ATTRIBUTES.register("volatile_length", () -> new RangedAttribute("volatile_length", 600.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_MAX_DAMAGE = ATTRIBUTES.register("volatile_max_damage", () -> new RangedAttribute("volatile_max_damage", 20.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));

    /**
     * Reduces the rate at which decay is gained over time (0.0 = no decay reduction, 0.5 = 50% decay reduction, 1.0 = 100% decay reduction).
     * Use NeoForge's PercentageAttribute to keep in line with Vanilla Knockback Resistance attribute (which NeoForge changes to be percentage-based).
     */
    public static final Holder<Attribute> DECAY_RESISTANCE = ATTRIBUTES.register("decay_resistance", () -> new PercentageAttribute("decay_resistance", 0.0, 0.0, 1.0).setSyncable(true));

    /**
     * Reduces the rate at which corrosion is gained over time (0.0 = no corrosion speed reduction, 0.5 = 50% corrosion speed reduction, 1.0 = 100% corrosion speed reduction).
     * Use NeoForge's PercentageAttribute to keep in line with Vanilla Knockback Resistance attribute (which NeoForge changes to be percentage-based).
     */
    public static final Holder<Attribute> CORROSION_RESISTANCE = ATTRIBUTES.register("corrosion_resistance", () -> new PercentageAttribute("corrosion_resistance", 0.0, 0.0, 1.0).setSyncable(true));
}
