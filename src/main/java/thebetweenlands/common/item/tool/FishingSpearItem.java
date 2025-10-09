package thebetweenlands.common.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.projectile.spear.FishingSpear;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;
import java.util.function.Supplier;

public class FishingSpearItem extends Item implements ProjectileItem {

	private final Supplier<EntityType<? extends FishingSpear>> spearEntity;
	private final int damage;

	public FishingSpearItem(Supplier<EntityType<? extends FishingSpear>> spearEntity, int damage, Properties properties) {
		super(properties);
		this.spearEntity = spearEntity;
		this.damage = damage;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.ANIMATED)) {
			tooltip.add(Component.translatable("item.thebetweenlands.fishing_spear.animated").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		if (entity instanceof Player player) {
			if (!stack.isEmpty()) {
				int i = this.getUseDuration(stack, entity) - timeCharged;
				float f = BowItem.getPowerForTime(i);
				if (f >= 0.1D) {
					if (!level.isClientSide()) {
						FishingSpear spear = this.spearEntity.get().create(level);
						spear.onSpawn(level, player, stack, f == 1.0F);
						spear.setBaseDamage(this.damage);
						spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);

						level.addFreshEntity(spear);
					}

					player.playSound(SoundRegistry.SPEAR_THROW.get(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!player.hasInfiniteMaterials()) {
						player.getInventory().removeItem(stack);
					}
					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		var spear = this.spearEntity.get().create(level);
		spear.setPos(pos.x(), pos.y(), pos.z());
		spear.setPickupItemStack(stack.copyWithCount(1));
		return spear;
	}
}
