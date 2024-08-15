package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.*;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<EntityType<?>, EntityType<SwampHag>> SWAMP_HAG = ENTITY_TYPES.register("swamp_hag", () -> EntityType.Builder.of(SwampHag::new, MobCategory.MONSTER).sized(0.6f, 1.8f).build(prefix("swamp_hag")));
	public static final DeferredHolder<EntityType<?>, EntityType<Gecko>> GECKO = ENTITY_TYPES.register("gecko", () -> EntityType.Builder.of(Gecko::new, MobCategory.CREATURE).sized(0.75F, 0.35F).build(prefix("gecko")));
	public static final DeferredHolder<EntityType<?>, EntityType<Wight>> WIGHT = ENTITY_TYPES.register("wight", () -> EntityType.Builder.of(Wight::new, MobCategory.MONSTER).sized(0.7F, 2.2F).build(prefix("wight")));
	public static final DeferredHolder<EntityType<?>, EntityType<Anadia>> ANADIA = ENTITY_TYPES.register("anadia", () -> EntityType.Builder.of(Anadia::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).build(prefix("anadia")));
	public static final DeferredHolder<EntityType<?>, EntityType<BubblerCrab>> BUBBLER_CRAB = ENTITY_TYPES.register("bubbler_crab", () -> EntityType.Builder.of(BubblerCrab::new, MobCategory.WATER_CREATURE).sized(0.7F, 0.6F).build(prefix("bubbler_crab")));
	public static final DeferredHolder<EntityType<?>, EntityType<SiltCrab>> SILT_CRAB = ENTITY_TYPES.register("silt_crab", () -> EntityType.Builder.of(SiltCrab::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.6F).build(prefix("silt_crab")));
	public static final DeferredHolder<EntityType<?>, EntityType<Seat>> SEAT = ENTITY_TYPES.register("seat", () -> EntityType.Builder.<Seat>of(Seat::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().noSummon().build(prefix("seat")));

	private static String prefix(String name) {
		return TheBetweenlands.prefix(name).toString();
	}
}
