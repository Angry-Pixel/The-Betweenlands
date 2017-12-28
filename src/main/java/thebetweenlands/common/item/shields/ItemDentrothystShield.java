package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.event.SplashPotionEvent;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemDentrothystShield extends ItemBLShield {
	public final boolean green;

	public ItemDentrothystShield(boolean green) {
		super(BLMaterialRegistry.TOOL_DENTROTHYST);
		this.green = green;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.dentrothyst_shield"), 0));
	}

	@SubscribeEvent
	public static void onSplashPotion(SplashPotionEvent event) {
		EntityLivingBase target = event.getTarget();
		if(target.isActiveItemStackBlocking()) {
			ItemStack stack = target.getActiveItemStack();
			if(!stack.isEmpty() && stack.getItem() instanceof ItemDentrothystShield) {
				if(!event.getPotionEffect().getIsAmbient()) {
					event.setCanceled(true);
				}
			}
		}
	}
}
