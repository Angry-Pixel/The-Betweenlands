package thebetweenlands.common.recipe.censer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.client.handler.ItemTooltipHandler;

public abstract class AbstractCenserRecipe<T> implements ICenserRecipe<T> {
	private static final List<ICenserRecipe<?>> RECIPES = new ArrayList<ICenserRecipe<?>>();

	public static void addRecipe(ICenserRecipe<?> recipe) {
		RECIPES.add(recipe);
	}

	public static void removeRecipe(ICenserRecipe<?> recipe) {
		RECIPES.remove(recipe);
	}

	public static List<ICenserRecipe<?>> getRecipes() {
		return RECIPES;
	}

	public static ICenserRecipe<?> getRecipe(ItemStack input) {
		if(!input.isEmpty()) {
			for(ICenserRecipe<?> recipe : AbstractCenserRecipe.getRecipes()) {
				if(recipe.matchesInput(input)) {
					return recipe;
				}
			}
		}
		return null;
	}

	public static ICenserRecipe<?> getRecipeWithSecondaryInput(ItemStack secondaryStack) {
		if(!secondaryStack.isEmpty()) {
			for(ICenserRecipe<?> recipe : AbstractCenserRecipe.getRecipes()) {
				if(recipe.matchesSecondaryInput(secondaryStack)) {
					return recipe;
				}
			}
		}
		return null;
	}

	public static ICenserRecipe<?> getRecipe(FluidStack input) {
		if(input != null && input.amount > 0) {
			for(ICenserRecipe<?> recipe : AbstractCenserRecipe.getRecipes()) {
				if(recipe.matchesInput(input)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return false;
	}

	@Override
	public boolean matchesSecondaryInput(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack consumeInput(ItemStack stack) {
		if(stack.getItem().hasContainerItem(stack)) {
			stack = stack.getItem().getContainerItem(stack);
			if (!stack.isEmpty() && stack.isItemStackDamageable() && stack.getMetadata() > stack.getMaxDamage()) {
				return ItemStack.EMPTY;
			}
			return stack;
		}
		ItemStack result = stack.copy();
		result.shrink(1);
		return result;
	}

	@Override
	public int getInputAmount(ItemStack stack) {
		return 1000;
	}

	@Override
	public boolean matchesInput(FluidStack stack) {
		return false;
	}

	@Override
	public T createContext(ItemStack stack) {
		return null;
	}

	@Override
	public T createContext(FluidStack stack) {
		return null;
	}

	@Override
	public void onStart(T context) {

	}

	@Override
	public void onStop(T context) {

	}

	@Override
	public void save(T context, NBTTagCompound nbt, boolean packet) {

	}

	@Override
	public void read(T context, NBTTagCompound nbt, boolean packet) {

	}

	@Override
	public int update(T context, ICenser censer) {
		return 0;
	}

	@Override
	public int getConsumptionDuration(T context, ICenser censer) {
		return 5;
	}

	@Override
	public int getConsumptionAmount(T context, ICenser censer) {
		return 1;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void getLocalizedEffectText(T context, ICenser censer, List<String> tooltip) {
		String key = String.format("tooltip.bl.censer.effect.%s", this.getId().getPath());

		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocal(key), TextFormatting.WHITE.toString()));

		int consumptionAmount = this.getConsumptionAmount(context, censer);

		if(consumptionAmount > 0) {
			tooltip.add("");

			int amountLeft = censer.getCurrentRemainingInputAmount();
			int consumptionDuration = this.getConsumptionDuration(context, censer);

			float approxRemainingTicks = amountLeft / (float)consumptionAmount * consumptionDuration;
			float approxRemainingSeconds = approxRemainingTicks / 20.0f;
			float approxRemainingMinutes = approxRemainingSeconds / 60.0f;

			tooltip.addAll(ItemTooltipHandler.splitTooltip(TextFormatting.DARK_PURPLE + I18n.translateToLocalFormatted("tooltip.bl.censer.remaining_duration", 
					amountLeft, MathHelper.ceil(approxRemainingTicks), MathHelper.ceil(approxRemainingSeconds), MathHelper.ceil(approxRemainingMinutes), StringUtils.ticksToElapsedTime(MathHelper.ceil(approxRemainingTicks))),
					TextFormatting.WHITE.toString()));
		}
	}

	@Override
	public int getEffectColor(T context, ICenser censer, EffectColorType type) {
		return type == EffectColorType.FOG ? 0xFFFFFFFF : 0xFF90BBFF;
	}

	@Override
	public void render(T context, ICenser censer, double x, double y, double z, float partialTicks) {

	}

	@Override
	public boolean isCreatingDungeonFog(T context, ICenser censer) {
		return false;
	}

	@Override
	public IAspectType getAspectFogType(T context, ICenser censer) {
		return null;
	}
}
