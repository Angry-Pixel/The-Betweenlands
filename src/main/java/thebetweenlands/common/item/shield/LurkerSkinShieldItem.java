package thebetweenlands.common.item.shield;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.LurkerSkinRaft;
import thebetweenlands.common.registries.ToolMaterialRegistry;

import java.util.List;

public class LurkerSkinShieldItem extends BaseShieldItem {
	public LurkerSkinShieldItem(Properties properties) {
		super(ToolMaterialRegistry.LURKER_SKIN, properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		BlockHitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hitresult.getType() != HitResult.Type.MISS) {
			Vec3 vec3 = player.getViewVector(1.0F);
			List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), EntitySelector.NO_SPECTATORS.and(Entity::isPickable));
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
				if (level.getBlockState(hitresult.getBlockPos()).liquid()) {
					LurkerSkinRaft raft = new LurkerSkinRaft(level, hitresult.getLocation().x(), hitresult.getLocation().y() - 0.12D, hitresult.getLocation().z(), itemstack);
					raft.setYRot(player.getYRot());
					if (!level.noCollision(raft, raft.getBoundingBox())) {
						return InteractionResultHolder.fail(itemstack);
					} else {
						if (!level.isClientSide()) {
							level.addFreshEntity(raft);
							if (!player.isShiftKeyDown()) {
								player.startRiding(raft);
							}
							level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
							itemstack.consume(1, player);
						}

						player.awardStat(Stats.ITEM_USED.get(this));
						return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
					}
				}
			}
		}
		return super.use(level, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}
}
