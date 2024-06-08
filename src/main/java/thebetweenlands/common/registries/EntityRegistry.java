package thebetweenlands.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.entitys.EntityGecko;
import thebetweenlands.common.entitys.EntitySwampHag;
import thebetweenlands.common.entitys.EntityWight;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TheBetweenlands.ID);
	
	// new EntitySwampHag(null, null)
	// TODO: Set up all ai properly, in my rush to get this all working i didnt finish the ai goals
	public static final RegistryObject<EntityType<EntitySwampHag>> SWAMP_HAG = ENTITY_TYPES.register("swamp_hag", () -> EntityType.Builder.of(EntitySwampHag::new, MobCategory.MONSTER).sized(0.6f, 1.8f).build(new ResourceLocation(TheBetweenlands.ID, "swamp_hag").toString()));
	public static final RegistryObject<EntityType<EntityGecko>> GECKO = ENTITY_TYPES.register("gecko", () -> EntityType.Builder.of(EntityGecko::new, MobCategory.CREATURE).sized(0.75F, 0.35F).build(new ResourceLocation(TheBetweenlands.ID, "gecko").toString()));
	public static final RegistryObject<EntityType<EntityWight>> WIGHT = ENTITY_TYPES.register("wight", () -> EntityType.Builder.of(EntityWight::new, MobCategory.MONSTER).sized(0.7F, 2.2F).build(new ResourceLocation(TheBetweenlands.ID, "wight").toString()));
	
	
	public static void register(IEventBus eventbus) {
		ENTITY_TYPES.register(eventbus);
	}
}
