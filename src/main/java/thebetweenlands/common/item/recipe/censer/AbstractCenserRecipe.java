package thebetweenlands.common.item.recipe.censer;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.Censer;
import thebetweenlands.api.recipes.CenserRecipe;

public abstract class AbstractCenserRecipe<T> implements CenserRecipe<T> {

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
		if (stack.getItem().hasCraftingRemainingItem(stack)) {
			return stack.getItem().getCraftingRemainingItem(stack);
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
	public void save(T context, CompoundTag tag, boolean packet) {

	}

	@Override
	public void read(T context, CompoundTag tag, boolean packet) {

	}

	@Override
	public int update(T context, Censer censer) {
		return 0;
	}

	@Override
	public int getConsumptionDuration(T context, Censer censer) {
		return 5;
	}

	@Override
	public int getConsumptionAmount(T context, Censer censer) {
		return 1;
	}

	@Override
	public void getLocalizedEffectText(T context, Censer censer, List<Component> tooltip) {
		tooltip.add(Component.translatable(String.format("block.thebetweenlands.censer.effect.%s", BLRegistries.CENSER_RECIPES.getKey(this).getPath())));

		int consumptionAmount = this.getConsumptionAmount(context, censer);

		if (consumptionAmount > 0) {
			tooltip.add(Component.empty());

			int amountLeft = censer.getCurrentRemainingInputAmount();
			int consumptionDuration = this.getConsumptionDuration(context, censer);

			float approxRemainingTicks = amountLeft / (float) consumptionAmount * consumptionDuration;
			float approxRemainingSeconds = approxRemainingTicks / 20.0f;
			float approxRemainingMinutes = approxRemainingSeconds / 60.0f;

			tooltip.add(Component.translatable("block.thebetweenlands.censer.remaining_duration", amountLeft, Mth.ceil(approxRemainingTicks), Mth.ceil(approxRemainingSeconds), Mth.ceil(approxRemainingMinutes), StringUtil.formatTickDuration(Mth.ceil(approxRemainingTicks), 20)));
		}
	}

	@Override
	public int getEffectColor(T context, Censer censer, EffectColorType type) {
		return type == EffectColorType.FOG ? 0xFFFFFFFF : 0xFF90BBFF;
	}

	@Override
	public void render(T context, Censer censer, BlockPos pos, float partialTicks) {

	}

	@Override
	public boolean isCreatingDungeonFog(T context, Censer censer) {
		return false;
	}

	@Override
	public Holder<AspectType> getAspectFogType(T context, Censer censer) {
		return null;
	}
}
