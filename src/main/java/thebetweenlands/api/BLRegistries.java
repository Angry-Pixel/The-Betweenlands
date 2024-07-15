package thebetweenlands.api;

import com.google.common.collect.BiMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;

public class BLRegistries {
	public static final Registry<AspectType> ASPECTS = new RegistryBuilder<>(Keys.ASPECTS).sync(true).create();
	public static final Registry<ElixirEffect> ELIXIR_EFFECTS = new RegistryBuilder<>(Keys.ELIXIR_EFFECTS).sync(true).create();
	public static final Registry<IEnvironmentEvent> ENVIRONMENT_EVENTS = new RegistryBuilder<>(Keys.ENVIRONMENT_EVENTS).sync(true).create();
	public static final Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>> WORLD_STORAGE = new RegistryBuilder<>(Keys.WORLD_STORAGE).sync(true).create();

	public static final class Keys {
		public static final ResourceKey<Registry<AspectType>> ASPECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("aspect"));
		public static final ResourceKey<Registry<ElixirEffect>> ELIXIR_EFFECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("elixir_effect"));
		public static final ResourceKey<Registry<IEnvironmentEvent>> ENVIRONMENT_EVENTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("environment_event"));
		public static final ResourceKey<Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>>> WORLD_STORAGE = ResourceKey.createRegistryKey(TheBetweenlands.prefix("world_storage"));
	}
}
