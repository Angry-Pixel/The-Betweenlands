package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.item.equipment.ItemAmulet;

public class EntitySpawnHandler {
	private EntitySpawnHandler() {
	}

	public static final int AMULET_SPAWN_CHANCE = 40;
	public static final List<Class<? extends EntityLivingBase>> AMULET_SPAWNS = new ArrayList<Class<? extends EntityLivingBase>>();

	static {
		AMULET_SPAWNS.add(EntitySwampHag.class);
		//AMULET_SPAWNS.add(EntityDreadfulMummy.class);
		AMULET_SPAWNS.add(EntityPeatMummy.class);
		AMULET_SPAWNS.add(EntityTarBeast.class);
		AMULET_SPAWNS.add(EntityWight.class);
		AMULET_SPAWNS.add(EntityCryptCrawler.class);
	}

	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();

		if (!entity.world.isRemote) {
			//Add gem modifier to arrows
			if (entity instanceof EntityArrow) {
				EntityArrow entityArrow = (EntityArrow) entity;
				if (entityArrow.shootingEntity instanceof EntityLivingBase) {
					EntityLivingBase source = (EntityLivingBase) entityArrow.shootingEntity;
					copyGemModifier(source, entityArrow);
				}
			}

			//Add gem modifier to throwables
			if (entity instanceof EntityThrowable) {
				EntityThrowable entityThrown = (EntityThrowable) entity;
				if (entityThrown.getThrower() != null) {
					copyGemModifier(entityThrown.getThrower(), entityThrown);
				}
			}

			//Random chance for spawned mobs to have an amulet
			if (AMULET_SPAWNS.contains(entity.getClass()) && entity.getEntityData() != null && entity.world.rand.nextInt(AMULET_SPAWN_CHANCE) == 0) {
				CircleGemType gem = CircleGemType.TYPES[entity.world.rand.nextInt(CircleGemType.TYPES.length - 1)];
				ItemAmulet.addAmulet(gem, entity, false, false);
			}
		}
	}

	/**
	 * Copies the gem modifier of the active or held item of the source entity to the spawned entity
	 *
	 * @param source
	 * @param entity
	 */
	private static void copyGemModifier(EntityLivingBase source, Entity entity) {
		ItemStack activeItem = source.getActiveItemStack();
		if (activeItem.isEmpty() && source.getActiveHand() != null) {
			activeItem = source.getHeldItem(source.getActiveHand());
		}
		if (activeItem.isEmpty()) {
			activeItem = source.getHeldItemMainhand();
		}

		if (!activeItem.isEmpty()) {
			CircleGemType gem = CircleGemHelper.getGem(activeItem);
			if (gem != CircleGemType.NONE) {
				CircleGemHelper.addGem(entity, gem, CombatType.OFFENSIVE);
			}
		}
	}
}
