package thebetweenlands.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class WeedwoodRowboatItem extends Item {

	private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

	public WeedwoodRowboatItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			Vec3 vec3 = player.getViewVector(1.0F);
			double d0 = 5.0;
			List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
			if (!list.isEmpty()) {
				Vec3 vec31 = player.getEyePosition();

				for (Entity entity : list) {
					AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
					if (aabb.contains(vec31)) {
						return InteractionResultHolder.pass(itemstack);
					}
				}
			}

			if (hitresult.getType() == HitResult.Type.BLOCK) {
				//TODO
//				WeedwoodBoat boat = new WeedwoodRowboat(level);
//				boat.setYRot(player.getYRot());
//				if (!level.noCollision(boat, boat.getBoundingBox())) {
//					return InteractionResultHolder.fail(itemstack);
//				} else {
//					if (!level.isClientSide) {
//						CustomData attrs = itemstack.get(DataComponents.CUSTOM_DATA);
//						if (attrs != null) {
//							boat.readAdditionalSaveData(attrs.copyTag(), level.registryAccess());
//						}
//						level.addFreshEntity(boat);
//						level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
//						itemstack.consume(1, player);
//					}
//
//					player.awardStat(Stats.ITEM_USED.get(this));
//					return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
//				}
			} else {
				return InteractionResultHolder.pass(itemstack);
			}
		}
	}
}
