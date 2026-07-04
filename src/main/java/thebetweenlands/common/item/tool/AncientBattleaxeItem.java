package thebetweenlands.common.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.item.UnbreakableItem;

import java.util.List;

public class AncientBattleaxeItem extends GreataxeItem implements UnbreakableItem {

	public AncientBattleaxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (UnbreakableItem.isStackBroken(stack)) {
			tooltip.add(Component.translatable("item.thebetweenlands.broken", stack.getDisplayName()).withStyle(ChatFormatting.RED));
		}
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		UnbreakableItem.hurtButDontBreak(stack, 2, attacker);
	}

	@Override
	protected double getBlockBreakReach(LivingEntity entity, ItemStack stack) {
		return UnbreakableItem.isStackBroken(stack) ? 0.0D : 3.0D;
	}

	@Override
	protected double getBlockBreakHalfAngle(LivingEntity entity, ItemStack stack) {
		return UnbreakableItem.isStackBroken(stack) ? 0.0D : 55.0D;
	}

	@Override
	public float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.225F;
	}

	@Override
	public double getAoEReach(LivingEntity entityLiving, ItemStack stack) {
		return 2.2D;
	}
}
