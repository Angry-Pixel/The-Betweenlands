package thebetweenlands.client.event.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.equipment.IEquippable;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemTooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<String> toolTip = event.getToolTip();

		if(CompostRecipe.getCompostRecipe(stack) != null) {
			toolTip.add(I18n.format("compost.compostable"));
		}

		CircleGemType circleGem = CircleGemHelper.getGem(stack);
		if(circleGem != CircleGemType.NONE) {
			String colorPrefix = "";
			switch(circleGem){
			case AQUA:
				colorPrefix = "\u00A79";
				break;
			case CRIMSON:
				colorPrefix = "\u00A7c";
				break;
			case GREEN:
				colorPrefix = "\u00A7a";
				break;
			default:
			}
			toolTip.add(colorPrefix + I18n.format("circlegem." + circleGem.name));
		}

		if(stack.getItem() instanceof ItemFood && stack.getItem() != ItemRegistry.CHIROMAW_WING && stack.getItem() != ItemRegistry.ROTTEN_FOOD && ItemRegistry.ITEMS.contains(stack.getItem())) {
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			if(player != null) {
				/*EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.getEntityPlayer(), EntityPropertiesFood.class);
				Sickness sickness = property.getSickness((ItemFood)stack.getItem());
				toolTip.add(I18n.format("foodSickness.state." + sickness.name().toLowerCase()));*/
			}
		}
		if(stack.getItem() instanceof IEquippable) {
			toolTip.add(I18n.format("item.equippable"));
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
