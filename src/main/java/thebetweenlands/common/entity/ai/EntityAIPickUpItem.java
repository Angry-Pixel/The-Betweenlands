package thebetweenlands.common.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.pathfinding.Path;

public class EntityAIPickUpItem extends EntityAIBase {
	protected final EntityLiving entity;
	
	public EntityAIPickUpItem(EntityLiving entity) {
		this.entity = entity;
	}
	
	@Override
	public boolean shouldExecute() {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty() && getClosestEntityItem(entity, 16.0D) !=null;
	}
	
	@Override
	public void startExecuting() {
	}
	
	@Override
	public boolean shouldContinueExecuting() {
        return shouldExecute();
    }
	
	@Override
	public void updateTask() {
		if (!entity.world.isRemote && shouldExecute()) {
			EntityItem entityitem = getClosestEntityItem(entity, 16.0D);
			if (entityitem != null) {
				float distance = entityitem.getDistance(entity);
				if (distance >= 1.5F && !entityitem.cannotPickup() && !entityitem.isDead) {
					double x = entityitem.posX;
					double y = entityitem.posY;
					double z = entityitem.posZ;
					entity.getLookHelper().setLookPosition(x, y, z, 20.0F, 8.0F);
					moveToItem(entityitem);
					return;
				}
				if (distance < 1.5F) {
					entity.getMoveHelper().setMoveTo(entityitem.posX, entityitem.posY, entityitem.posZ, 0.5D);
					entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, entityitem.getItem().copy());
	
					entityitem.getItem().shrink(1);
					if (entityitem.getItem().getCount() <= 0)
						entityitem.setDead();
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public EntityItem getClosestEntityItem(final Entity entityItem, double distance) {
		List<EntityItem> list = entity.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, entity.getEntityBoundingBox().grow(distance, distance, distance));
		for (Iterator<EntityItem> iterator = list.iterator(); iterator.hasNext();) {
			EntityItem item = iterator.next();
			if (item.getAge() >= item.lifespan)
				iterator.remove();
		}
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public void moveToItem(Entity entityItem) {
		Path pathentity = entity.getNavigator().getPathToEntityLiving(entity);
		if (pathentity != null) {
			//entity.getNavigator().setPath(pathentity, 0.5D);
			entity.getNavigator().tryMoveToXYZ(entityItem.posX, entityItem.posY, entityItem.posZ, 0.5D);
		}
	}

}
