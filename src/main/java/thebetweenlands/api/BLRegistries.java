package thebetweenlands.api;

import com.google.common.collect.BiMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;

public class BLRegistries {
	public static final Registry<CenserRecipe<?>> CENSER_RECIPES = new RegistryBuilder<>(Keys.CENSER_RECIPES).sync(true).create();
	public static final Registry<ElixirEffect> ELIXIR_EFFECTS = new RegistryBuilder<>(Keys.ELIXIR_EFFECTS).sync(true).create();
	public static final Registry<IEnvironmentEvent> ENVIRONMENT_EVENTS = new RegistryBuilder<>(Keys.ENVIRONMENT_EVENTS).sync(true).create();
	public static final Registry<SimulacrumEffect> SIMULACRUM_EFFECTS = new RegistryBuilder<>(Keys.SIMULACRUM_EFFECTS).sync(true).create();
	public static final Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>> WORLD_STORAGE = new RegistryBuilder<>(Keys.WORLD_STORAGE).sync(true).create();

	public static final class Keys {

		private static final String DATAPACK_PREFIX = "betweenlands";

		public static final ResourceKey<Registry<AspectType>> ASPECTS = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DATAPACK_PREFIX, "aspect"));
		public static final ResourceKey<Registry<CenserRecipe<?>>> CENSER_RECIPES = ResourceKey.createRegistryKey(TheBetweenlands.prefix("censer_recipe"));
		public static final ResourceKey<Registry<ElixirEffect>> ELIXIR_EFFECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("elixir_effect"));
		public static final ResourceKey<Registry<IEnvironmentEvent>> ENVIRONMENT_EVENTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("environment_event"));
		public static final ResourceKey<Registry<SimulacrumEffect>> SIMULACRUM_EFFECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("simulacrum_effect"));
		public static final ResourceKey<Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>>> WORLD_STORAGE = ResourceKey.createRegistryKey(TheBetweenlands.prefix("world_storage"));
	}
}
