package thebetweenlands.common.registries;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.infection.AbstractInfectionBehavior;
import thebetweenlands.common.entity.infection.PlantingInfectionBehavior;
import thebetweenlands.common.entity.infection.SporeInfectionBehavior;
import thebetweenlands.common.lib.ModInfo;

public class InfectionBehaviorRegistry {
	private static final BiMap<ResourceLocation, Class<? extends AbstractInfectionBehavior>> MAP = HashBiMap.create();
	private static final Map<ResourceLocation, Factory<?>> FACTORIES = new HashMap<>();

	public static interface Factory<T extends AbstractInfectionBehavior> {
		public T create(EntityLivingBase entity);
	}

	public static void preInit() {
		register(new ResourceLocation(ModInfo.ID, "planting_behavior"), PlantingInfectionBehavior.class, PlantingInfectionBehavior::new);
		register(new ResourceLocation(ModInfo.ID, "spore_behavior"), SporeInfectionBehavior.class, SporeInfectionBehavior::new);
	}

	public static <T extends AbstractInfectionBehavior> void register(ResourceLocation id, Class<T> cls, Factory<T> factory) {
		MAP.put(id, cls);
		FACTORIES.put(id, factory);
	}

	public static Class<? extends AbstractInfectionBehavior> getType(ResourceLocation id) {
		return MAP.get(id);
	}

	public static Factory<? extends AbstractInfectionBehavior> getFactory(ResourceLocation id) {
		return FACTORIES.get(id);
	}

	public static ResourceLocation getId(Class<? extends AbstractInfectionBehavior> behavior) {
		return MAP.inverse().get(behavior);
	}
}
