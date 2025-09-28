package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawHatchling;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationChiromawMatriarchNest;

import java.util.List;

public class ChiromawHatchlingItem extends MobItem<ChiromawHatchling> {
	public ChiromawHatchlingItem(Properties properties, boolean electric) {
		super(properties, 5.0D, EntityRegistry.CHIROMAW_HATCHLING.get(), hatchling -> hatchling.setElectricBoogaloo(electric));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getClickedFace() == Direction.UP) {
			return super.useOn(context);
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onCapturedByPlayer(Player player, InteractionHand hand, ItemStack captured, LivingEntity chiromaw) {
		if (player instanceof ServerPlayer sp) {
			AABB checkBox = player.getBoundingBox().inflate(8);

			if (player.level().getEntitiesOfClass(ChiromawHatchling.class, checkBox, EntitySelector.LIVING_ENTITY_STILL_ALIVE).isEmpty()) {
				var storage = BetweenlandsWorldStorage.getForLevel(player.level());
				if (storage.isPresent()) {
					List<LocationChiromawMatriarchNest> nests = storage.get().getLocalStorageHandler().getLocalStorages(LocationChiromawMatriarchNest.class, checkBox, location -> location.getBoundingBox().intersects(checkBox));

					if (nests.isEmpty()) {
						return;
					}

					for (LocationChiromawMatriarchNest nest : nests) {
						if (nest.getGuard().isClear(player.level())) {
							return;
						}
					}

					AdvancementCriteriaRegistry.CHIROMAW_MATRIARCH_NEST_RAIDED.get().trigger(sp);
				}
			}
		}
	}

	@Override
	protected InteractionResult spawnCapturedEntity(Player player, Level level, InteractionHand hand, Direction facing, Vec3 hitVec, Entity entity, boolean isNewEntity) {
		if (!level.isClientSide() && entity instanceof ChiromawHatchling hatchling) {
			hatchling.setOwnerUUID(player.getUUID());
			hatchling.setFoodCraved(hatchling.chooseNewFoodFromLootTable((ServerLevel) level));
			hatchling.setPos(BlockPos.containing(hitVec).getBottomCenter());
		}

		return super.spawnCapturedEntity(player, level, hand, facing, hitVec, entity, isNewEntity);
	}
}
