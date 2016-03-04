package thebetweenlands.event.entity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import thebetweenlands.entities.mobs.EntityDreadfulMummy;
import thebetweenlands.entities.mobs.EntityPeatMummy;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.EntityGem;
import thebetweenlands.items.equipment.ItemAmulet;

public class EntitySpawnHandler {
	public static final EntitySpawnHandler INSTANCE = new EntitySpawnHandler();

	public static final int AMULET_SPAWN_CHANCE = 40;
	public static final List<Class<? extends EntityLivingBase>> AMULET_SPAWNS = new ArrayList<Class<? extends EntityLivingBase>>();

	static {
		AMULET_SPAWNS.add(EntitySwampHag.class);
		AMULET_SPAWNS.add(EntityDreadfulMummy.class);
		AMULET_SPAWNS.add(EntityPeatMummy.class);
		AMULET_SPAWNS.add(EntityTarBeast.class);
		AMULET_SPAWNS.add(EntityWight.class);
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		//Add gem modifier to arrows
		if(event.entity instanceof EntityArrow) {
			EntityArrow entityArrow = (EntityArrow)event.entity;
			if(entityArrow.shootingEntity instanceof EntityLivingBase) {
				EntityLivingBase source = (EntityLivingBase)entityArrow.shootingEntity;
				this.copyGemModifier(source, entityArrow);
			}
		}
		//Add gem modifier to throwables
		if(event.entity instanceof EntityThrowable) {
			EntityThrowable entityThrown = (EntityThrowable)event.entity;
			if(entityThrown.getThrower() != null) {
				this.copyGemModifier(entityThrown.getThrower(), entityThrown);
			}
		}

		//Random chance for spawned mobs to have an amulet
		if(AMULET_SPAWNS.contains(event.entity.getClass()) && event.entity.getEntityData() != null && 
				/*event.entity.getEntityData().hasKey("naturallySpawned") && event.entity.getEntityData().getBoolean("naturallySpawned") &&*/
				event.entity.worldObj.rand.nextInt(AMULET_SPAWN_CHANCE) == 0) {
			CircleGem gem = CircleGem.TYPES[event.entity.worldObj.rand.nextInt(CircleGem.TYPES.length - 1)];
			ItemAmulet.addAmulet(gem, event.entity, false, false);
		}
	}

	/**
	 * Copies the gem modifier of the held item of the source entity to the spawned entity
	 * @param source
	 * @param entity
	 */
	private void copyGemModifier(EntityLivingBase source, Entity entity) {
		if(source.getHeldItem() != null) {
			CircleGem gem = CircleGem.getGem(source.getHeldItem());
			if(gem != CircleGem.NONE) {
				CircleGem.addGem(entity, gem, EntityGem.Type.OFFENSIVE);
			}
		}
	}
}
