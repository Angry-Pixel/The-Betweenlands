package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import thebetweenlands.common.TheBetweenlands;

public class DamageTypeRegistry {

	public static final ResourceKey<DamageType> VOODOO = create("voodoo");

	public static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<DamageType> context) {
		context.register(VOODOO, new DamageType("thebetweenlands.voodoo", 0.1F));
	}
}
