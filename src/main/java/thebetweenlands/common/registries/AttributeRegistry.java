package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class AttributeRegistry {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TheBetweenlands.ID);

	public static final Holder<Attribute> VOLATILE_HEALTH_START_ATTRIB = ATTRIBUTES.register("volatile_health_start", () -> new RangedAttribute("bl.volatileHealthStart", 1.0D, 0.0D, 1.0D).setSyncable(true));    // Volatile Health Percentage Start
	public static final Holder<Attribute> VOLATILE_COOLDOWN_ATTRIB = ATTRIBUTES.register("volatile_cooldown", () -> new RangedAttribute("bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE).setSyncable(true));    // Volatile Cooldown
	public static final Holder<Attribute> VOLATILE_FLIGHT_SPEED_ATTRIB = ATTRIBUTES.register("volatile_flight_speed", () -> new RangedAttribute("bl.volatileFlightSpeed", 0.32D, 0.0D, 5.0D).setSyncable(true));    // Volatile Flight Speed
	public static final Holder<Attribute> VOLATILE_LENGTH_ATTRIB = ATTRIBUTES.register("volatile_length", () -> new RangedAttribute("bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));    // Volatile Length
	public static final Holder<Attribute> VOLATILE_MAX_DAMAGE_ATTRIB = ATTRIBUTES.register("volatile_max_damage", () -> new RangedAttribute("bl.volatileMaxDamage", 20.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));    // Volatile Max Damage
}
