package thebetweenlands.common.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.item.UnbreakableItem;

import java.util.List;

public class AncientGreatswordItem extends GreatswordItem implements UnbreakableItem {

	public AncientGreatswordItem(Tier tier, Properties properties) {
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
		UnbreakableItem.hurtButDontBreak(stack, 1, attacker);
	}
}
