package thebetweenlands.common.item.shield;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.registries.ToolMaterialRegistry;

import java.util.List;

public class OctineShieldItem extends BaseShieldItem {
	public OctineShieldItem(Properties properties) {
		super(ToolMaterialRegistry.OCTINE, properties);
	}

	@Override
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(source.getDirectEntity() != null) {
			source.getDirectEntity().igniteForSeconds(4);
		}
		super.onAttackBlocked(stack, attacked, damage, source);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}
}
