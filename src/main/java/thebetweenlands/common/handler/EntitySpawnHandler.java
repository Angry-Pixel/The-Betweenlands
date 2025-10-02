package thebetweenlands.common.handler;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import thebetweenlands.common.component.entity.circlegem.CircleGem;
import thebetweenlands.common.component.entity.circlegem.CircleGemHelper;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.item.equipment.AmuletItem;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class EntitySpawnHandler {

	private static final List<Holder<Item>> AMULETS = List.of(ItemRegistry.CRIMSON_AMULET, ItemRegistry.GREEN_AMULET, ItemRegistry.AQUA_AMULET);

	public static void handleAmuletSpawns(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();

		if (!entity.level().isClientSide() && !event.loadedFromDisk() && CircleGemHelper.getGems(entity).isEmpty()) {
			//Add gem modifier to projectiles
			if (entity instanceof Projectile projectile) {
				if (projectile.getOwner() instanceof LivingEntity living) {
					copyGemModifier(living, projectile);
				}
			}

			//Random chance for spawned mobs to have an amulet
			if (entity.getType().builtInRegistryHolder().getData(DataMapRegistry.AMULET_SPAWNS) != null) {
				int chance = entity.getType().builtInRegistryHolder().getData(DataMapRegistry.AMULET_SPAWNS).chance();
				if (entity.getRandom().nextInt(chance) == 0) {
					Holder<Item> amulet = AMULETS.get(entity.getRandom().nextInt(AMULETS.size()));
					AmuletItem.addAmulet(amulet, entity, false, false);
				}
			}
		}
	}

	/**
	 * Copies the gem modifier of the active or held item of the source entity to the spawned entity
	 *
	 * @param source
	 * @param entity
	 */
	private static void copyGemModifier(LivingEntity source, Entity entity) {
		ItemStack activeItem = source.getUseItem();
		if (activeItem.isEmpty() && source.getUsedItemHand() != null) {
			activeItem = source.getItemInHand(source.getUsedItemHand());
		}
		if (activeItem.isEmpty()) {
			activeItem = source.getMainHandItem();
		}

		if (!activeItem.isEmpty()) {
			CircleGemType gem = CircleGemHelper.getGem(activeItem);
			if (gem != CircleGemType.NONE) {
				CircleGemHelper.addGem(entity, gem, CircleGem.CombatType.OFFENSIVE);
			}
		}
	}
}
