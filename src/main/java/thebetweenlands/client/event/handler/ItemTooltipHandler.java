package thebetweenlands.client.event.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.capability.foodsickness.IFoodSicknessCapability;
import thebetweenlands.common.item.equipment.IEquippable;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemTooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<String> toolTip = event.getToolTip();

		CompostRecipe recipe = CompostRecipe.getCompostRecipe(stack);
		if(recipe != null) {
			String debug = "";
			if(event.isShowAdvancedItemTooltips()) {
				debug = " (T: " + ScreenRenderHandler.ASPECT_AMOUNT_FORMAT.format(recipe.compostTime / 20.0F) + "s A: " + recipe.compostAmount + ")";
			}
			toolTip.add(I18n.format("tooltip.compost.compostable") + debug);
		}

		CircleGemType circleGem = CircleGemHelper.getGem(stack);
		if(circleGem != CircleGemType.NONE) {
			toolTip.add(I18n.format("tooltip.circlegem." + circleGem.name));
		}

		if(stack.getItem() instanceof ItemFood && stack.getItem() != ItemRegistry.CHIROMAW_WING && stack.getItem() != ItemRegistry.ROTTEN_FOOD && ItemRegistry.ITEMS.contains(stack.getItem())) {
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			if(player != null && player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
				IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
				FoodSickness sickness = cap.getSickness((ItemFood)stack.getItem());
				String debug = "";
				if(event.isShowAdvancedItemTooltips()) {
					debug = " (" + cap.getFoodHatred((ItemFood)stack.getItem()) + "/" + sickness.maxHatred + ")";
				}
				toolTip.add(I18n.format("tooltip.foodSickness.state." + sickness.name().toLowerCase()) + debug);
			}
		}

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(stack.getItem() instanceof IEquippable && player != null && ((IEquippable)stack.getItem()).canEquip(stack, player, player)) {
			toolTip.add(I18n.format("tooltip.item.equippable"));
		}
	}

	/**
	 * Splits a tooltip into multiple lines
	 * @param tooltip
	 * @param indent
	 * @return
	 */
	public static List<String> splitTooltip(String tooltip, int indent) {
		String indentStr = new String(new char[indent]).replace('\0', ' ');
		List<String> lines = new ArrayList<String>();
		String[] splits = tooltip.split("\\\\n");
		for(int i = 0; i < splits.length; i++) {
			splits[i] = indentStr + splits[i];
		}
		lines.addAll(Arrays.asList(splits));
		return lines;
	}
}
