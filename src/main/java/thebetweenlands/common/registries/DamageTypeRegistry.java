package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import thebetweenlands.common.TheBetweenlands;

public class DamageTypeRegistry {

	public static final ResourceKey<DamageType> FLAME_JET = create("flame_jet");
	public static final ResourceKey<DamageType> SWARM = create("swarm");
	public static final ResourceKey<DamageType> VOODOO = create("voodoo");

	public static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<DamageType> context) {
		context.register(FLAME_JET, new DamageType("thebetweenlands.flame_jet", 0.1F));
		context.register(SWARM, new DamageType("thebetweenlands.swarm", 0.1F));
		context.register(VOODOO, new DamageType("thebetweenlands.voodoo", 0.1F));
	}
}
