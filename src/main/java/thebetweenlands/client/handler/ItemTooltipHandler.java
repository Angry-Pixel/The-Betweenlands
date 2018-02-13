package thebetweenlands.client.handler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.item.IDecayFood;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.api.item.IFoodSicknessItem;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class ItemTooltipHandler {
public static final DecimalFormat COMPOST_AMOUNT_FORMAT = new DecimalFormat("#.##");
	
	static {
		COMPOST_AMOUNT_FORMAT.setRoundingMode(RoundingMode.CEILING);
	}
	
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<String> toolTip = event.getToolTip();
		EntityPlayer player = event.getEntityPlayer();

		ICompostBinRecipe recipe = CompostRecipe.getCompostRecipe(stack);
		if(recipe != null) {
			String debug = "";
			if(event.getFlags().isAdvanced()) {
				debug = " (T: " + COMPOST_AMOUNT_FORMAT.format(recipe.getCompostingTime(stack) / 20.0F) + "s A: " + recipe.getCompostAmount(stack) + ")";
			}
			toolTip.add(I18n.format("tooltip.compost.compostable") + debug);
		}

		CircleGemType circleGem = CircleGemHelper.getGem(stack);
		if(circleGem != CircleGemType.NONE) {
			toolTip.add(I18n.format("tooltip.circlegem." + circleGem.name));
		}

		if(stack.getItem() instanceof IDecayFood) {
			((IDecayFood)stack.getItem()).getDecayFoodTooltip(stack, player != null ? player.world : null, toolTip, event.getFlags());
		}
		
		if(player != null) {
			if(ConfigHandler.useFoodSickness && stack.getItem() instanceof ItemFood && stack.getItem() instanceof IFoodSicknessItem && ((IFoodSicknessItem)stack.getItem()).canGetSickOf(player, stack)) {
				if(player.hasCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null)) {
					IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
					FoodSickness sickness = cap.getSickness((ItemFood)stack.getItem());
					int hatred = cap.getFoodHatred((ItemFood)stack.getItem());
					((IFoodSicknessItem)stack.getItem()).getSicknessTooltip(stack, sickness, hatred, event.getFlags().isAdvanced(), toolTip);
				}
			}
	
			if(stack.getItem() instanceof IEquippable && ((IEquippable)stack.getItem()).canEquip(stack, player, player)) {
				toolTip.add(I18n.format("tooltip.item.equippable"));
			}
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
