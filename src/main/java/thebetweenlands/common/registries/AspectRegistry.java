package thebetweenlands.common.registries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.common.TheBetweenlands;

public class AspectRegistry {

	public static final ResourceKey<AspectType> ARMANIIS = registerKey("armaniis");
	public static final ResourceKey<AspectType> AZUWYNN = registerKey("azuwynn");
	public static final ResourceKey<AspectType> BYARIIS = registerKey("byariis");
	public static final ResourceKey<AspectType> BYRGINAZ = registerKey("byrginaz");
	public static final ResourceKey<AspectType> CELAWYNN = registerKey("celawynn");
	public static final ResourceKey<AspectType> DAYUNIIS = registerKey("dayuniis");
	public static final ResourceKey<AspectType> FERGALAZ = registerKey("fergalaz");
	public static final ResourceKey<AspectType> FIRNALAZ = registerKey("firnalaz");
	public static final ResourceKey<AspectType> FREIWYNN = registerKey("freiwynn");
	public static final ResourceKey<AspectType> GEOLIIRGAZ = registerKey("geoliirgaz");
	public static final ResourceKey<AspectType> ORDANIIS = registerKey("ordaniis");
	public static final ResourceKey<AspectType> UDURIIS = registerKey("uduriis");
	public static final ResourceKey<AspectType> WODREN = registerKey("wodren");
	public static final ResourceKey<AspectType> YEOWYNN = registerKey("yeowynn");
	public static final ResourceKey<AspectType> YIHINREN = registerKey("yihinren");
	public static final ResourceKey<AspectType> YUNUGAZ = registerKey("yunugaz");


	public static ResourceKey<AspectType> registerKey(String name) {
		return ResourceKey.create(BLRegistries.Keys.ASPECTS, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<AspectType> context) {
		context.register(ARMANIIS, new AspectType(0xFFFFCC00));
		context.register(AZUWYNN, new AspectType(0xFFE01414));
		context.register(BYARIIS, new AspectType(0xFF293828));
		context.register(BYRGINAZ, new AspectType(0xFF1EBBDB));
		context.register(CELAWYNN, new AspectType(0xFF4ACE48));
		context.register(DAYUNIIS, new AspectType(0xFFB148CE));
		context.register(FERGALAZ, new AspectType(0xFF29B539));
		context.register(FIRNALAZ, new AspectType(0xFFFF7F00));
		context.register(FREIWYNN, new AspectType(0xFFC1D8F4));
		context.register(GEOLIIRGAZ, new AspectType(0xFF222228));
		context.register(ORDANIIS, new AspectType(0xFF64EF99));
		context.register(UDURIIS, new AspectType(0xFF3A1352));
		context.register(WODREN, new AspectType(0xFF63C2AF));
		context.register(YEOWYNN, new AspectType(0xFFFC0069));
		context.register(YIHINREN, new AspectType(0xFFFFFFFF));
		context.register(YUNUGAZ, new AspectType(0xFF00FFBB));
	}
}
