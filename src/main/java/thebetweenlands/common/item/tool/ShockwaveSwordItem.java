package thebetweenlands.common.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.component.item.ShockwaveSwordData;
import thebetweenlands.common.entity.projectile.ShockwaveBlock;
import thebetweenlands.common.item.UnbreakableItem;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.ArrayList;
import java.util.List;

public class ShockwaveSwordItem extends SwordItem implements UnbreakableItem {
	public ShockwaveSwordItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (UnbreakableItem.isStackBroken(stack)) {
			tooltip.add(Component.translatable("item.thebetweenlands.broken", stack.getHoverName()).withStyle(ChatFormatting.RED));
		} else {
			tooltip.add(Component.translatable(this.getDescriptionId(stack) + ".desc").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (stack.getDamageValue() + 1 >= stack.getMaxDamage() || !stack.has(DataComponentRegistry.SHOCKWAVE_DATA)) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide()) {
			ShockwaveSwordData data = stack.get(DataComponentRegistry.SHOCKWAVE_DATA);

			if (data.cooldownTimestamp() != -1 && data.cooldownTimestamp() + 60L < level.getGameTime()) {
				stack.set(DataComponentRegistry.SHOCKWAVE_DATA, ShockwaveSwordData.DEFAULT);
				data = stack.get(DataComponentRegistry.SHOCKWAVE_DATA);
            }

			if (data.uses() < 3) {
				double direction = Math.toRadians(context.getPlayer().getYRot());
				Vec3 diag = new Vec3(Math.sin(direction + Mth.HALF_PI), 0, Math.cos(direction + Mth.HALF_PI)).normalize();
				List<BlockPos> spawnedPos = new ArrayList<>();
				boolean waveMade = false;
				for (int distance = -1; distance <= 16; distance++) {
					for (int distance2 = -distance; distance2 <= distance; distance2++) {
						for (int yo = -1; yo <= 1; yo++) {
							int originX = Mth.floor(pos.getX() + 0.5D - Math.sin(direction) * distance - diag.x * distance2 * 0.25D);
							int originY = pos.getY() + yo;
							int originZ = Mth.floor(pos.getZ() + 0.5D + Math.cos(direction) * distance + diag.z * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if (spawnedPos.contains(origin))
								continue;

							spawnedPos.add(origin);

							BlockState block = level.getBlockState(origin);

							if (block.isRedstoneConductor(level, origin) && level.getBlockEntity(origin) == null
								&& block.getDestroySpeed(level, origin) <= 5.0F && block.getDestroySpeed(level, origin) >= 0.0F
								&& level.getBlockState(origin.above()).getCollisionShape(level, origin.above()).isEmpty()) {
								ShockwaveBlock shockwaveBlock = new ShockwaveBlock(EntityRegistry.SHOCKWAVE_BLOCK.get(), level);
								shockwaveBlock.setOrigin(origin, Mth.floor(Math.sqrt(distance * distance + distance2 * distance2)), pos.getX() + 0.5D, pos.getZ() + 0.5D, context.getPlayer());
								shockwaveBlock.moveTo(originX + 0.5D, originY, originZ + 0.5D, 0.0F, 0.0F);
								shockwaveBlock.setBlock(block);
								level.addFreshEntity(shockwaveBlock);
								waveMade = true;
								break;
							}
						}
					}
				}

				if (waveMade) {
					UnbreakableItem.hurtButDontBreak(stack, 2, context.getPlayer());
					level.playSound(null, context.getClickedPos(), SoundRegistry.SHOCKWAVE_SWORD.get(), SoundSource.BLOCKS, 1.25F, 1.0F + level.getRandom().nextFloat() * 0.1F);
					stack.set(DataComponentRegistry.SHOCKWAVE_DATA, data.incrementUses(level));
				}

			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		UnbreakableItem.hurtButDontBreak(stack, 1, attacker);
	}
}
