package thebetweenlands.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class EntityCache {

	private static final Map<EntityType<?>, Entity> ENTITY_MAP = new WeakHashMap<>();
	private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

	@Nullable
	public static LivingEntity fetchEntity(EntityType<?> type, @Nullable Level level) {
		if (level != null && !IGNORED_ENTITIES.contains(type)) {
			Entity entity = null;
			if (type == EntityType.PLAYER) {
				entity = Minecraft.getInstance().player;
			} else {
				try {
					entity = ENTITY_MAP.computeIfAbsent(type, t -> {
						Entity created = t.create(level);
						if (created != null) {
							created.setYRot(0.0F);
							created.setYHeadRot(0.0F);
							created.setYBodyRot(0.0F);
							created.hasImpulse = false;
							if (created instanceof Mob mob) {
								mob.setNoAi(true);
							}
						}
						return created;
					});
				} catch (Exception e) {
					TheBetweenlands.LOGGER.error("Failed to cache a render for entity {}", type.getDescriptionId(), e);
				}
			}
			if (entity instanceof LivingEntity living) {
				return living;
			} else {
				addEntityToBlacklist(type);
			}
		}
		return null;
	}

	public static void addEntityToBlacklist(EntityType<?> type) {
		IGNORED_ENTITIES.add(type);
		ENTITY_MAP.remove(type);
	}
}
