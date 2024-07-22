package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.Anadia;
import thebetweenlands.common.entities.EntityGecko;
import thebetweenlands.common.entities.EntitySwampHag;
import thebetweenlands.common.entities.EntityWight;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TheBetweenlands.ID);

	// new EntitySwampHag(null, null)
	// TODO: Set up all ai properly, in my rush to get this all working i didnt finish the ai goals
	public static final DeferredHolder<EntityType<?>, EntityType<EntitySwampHag>> SWAMP_HAG = ENTITY_TYPES.register("swamp_hag", () -> EntityType.Builder.of(EntitySwampHag::new, MobCategory.MONSTER).sized(0.6f, 1.8f).build(prefix("swamp_hag")));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityGecko>> GECKO = ENTITY_TYPES.register("gecko", () -> EntityType.Builder.of(EntityGecko::new, MobCategory.CREATURE).sized(0.75F, 0.35F).build(prefix("gecko")));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityWight>> WIGHT = ENTITY_TYPES.register("wight", () -> EntityType.Builder.of(EntityWight::new, MobCategory.MONSTER).sized(0.7F, 2.2F).build(prefix("wight")));
	public static final DeferredHolder<EntityType<?>, EntityType<Anadia>> ANADIA = ENTITY_TYPES.register("anadia", () -> EntityType.Builder.of(Anadia::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).build(prefix("anadia")));

	private static String prefix(String name) {
		return TheBetweenlands.prefix(name).toString();
	}
}
