package thebetweenlands.api;

import com.google.common.collect.BiMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.api.storage.IDeferredStorageOperation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.SimpleAttachmentType;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectCalculatorType;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;

public class BLRegistries {
	public static final Registry<MapCodec<? extends AspectCalculatorType>> ASPECT_CALCULATOR_TYPE = new RegistryBuilder<>(Keys.ASPECT_CALCULATOR_TYPE).sync(true).create();
	public static final Registry<CenserRecipe<?>> CENSER_RECIPES = new RegistryBuilder<>(Keys.CENSER_RECIPES).sync(true).create();
	public static final Registry<ElixirEffect> ELIXIR_EFFECTS = new RegistryBuilder<>(Keys.ELIXIR_EFFECTS).sync(true).create();
	public static final Registry<IEnvironmentEvent> ENVIRONMENT_EVENTS = new RegistryBuilder<>(Keys.ENVIRONMENT_EVENTS).sync(true).create();
	public static final Registry<SimulacrumEffect> SIMULACRUM_EFFECTS = new RegistryBuilder<>(Keys.SIMULACRUM_EFFECTS).sync(true).create();
	public static final Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>> WORLD_STORAGE = new RegistryBuilder<>(Keys.WORLD_STORAGE).sync(true).create();
	public static final Registry<SimpleAttachmentType<?>> SIMPLE_ATTACHMENT_TYPES = new RegistryBuilder<>(Keys.SIMPLE_ATTACHMENT_TYPES).sync(true).create();

	public static final class Keys {

		private static final String DATAPACK_PREFIX = "betweenlands";

		public static final ResourceKey<Registry<MapCodec<? extends AspectCalculatorType>>> ASPECT_CALCULATOR_TYPE = ResourceKey.createRegistryKey(TheBetweenlands.prefix("aspect_calculator_type"));
		public static final ResourceKey<Registry<AspectItem>> ASPECT_ITEMS = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DATAPACK_PREFIX, "aspect/item"));
		public static final ResourceKey<Registry<AspectType>> ASPECT_TYPES = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DATAPACK_PREFIX, "aspect/type"));
		public static final ResourceKey<Registry<CenserRecipe<?>>> CENSER_RECIPES = ResourceKey.createRegistryKey(TheBetweenlands.prefix("censer_recipe"));
		public static final ResourceKey<Registry<ElixirEffect>> ELIXIR_EFFECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("elixir_effect"));
		public static final ResourceKey<Registry<ElixirRecipe>> ELIXIR_RECIPES = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DATAPACK_PREFIX, "elixir_recipe"));
		public static final ResourceKey<Registry<IEnvironmentEvent>> ENVIRONMENT_EVENTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("environment_event"));
		public static final ResourceKey<Registry<SimulacrumEffect>> SIMULACRUM_EFFECTS = ResourceKey.createRegistryKey(TheBetweenlands.prefix("simulacrum_effect"));
		public static final ResourceKey<Registry<BiMap<ResourceLocation, ? extends IDeferredStorageOperation>>> WORLD_STORAGE = ResourceKey.createRegistryKey(TheBetweenlands.prefix("world_storage"));
		public static final ResourceKey<Registry<SimpleAttachmentType<?>>> SIMPLE_ATTACHMENT_TYPES = ResourceKey.createRegistryKey(TheBetweenlands.prefix("simple_attachment_types"));
	}
}
