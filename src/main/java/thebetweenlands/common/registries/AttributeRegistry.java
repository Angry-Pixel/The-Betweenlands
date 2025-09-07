package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class AttributeRegistry {

	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TheBetweenlands.ID);

	public static final Holder<Attribute> VOLATILE_HEALTH_START = ATTRIBUTES.register("volatile_health_start", () -> new RangedAttribute("volatile_health", 1.0D, 0.0D, 1.0D).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_COOLDOWN = ATTRIBUTES.register("volatile_cooldown", () -> new RangedAttribute("volatile_cooldown", 400.0D, 10.0D, Integer.MAX_VALUE).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_LENGTH = ATTRIBUTES.register("volatile_length", () -> new RangedAttribute("volatile_length", 600.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));
	public static final Holder<Attribute> VOLATILE_MAX_DAMAGE = ATTRIBUTES.register("volatile_max_damage", () -> new RangedAttribute("volatile_max_damage", 20.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));
}
