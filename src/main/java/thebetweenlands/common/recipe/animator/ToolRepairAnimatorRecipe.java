package thebetweenlands.common.recipe.animator;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.recipes.IAnimatorRecipe;

public class ToolRepairAnimatorRecipe implements IAnimatorRecipe {
	private Item tool;
	private int minRepairLifeCost, fullRepairLifeCost, minRepairFuelCost, fullRepairFuelCost;

	public ToolRepairAnimatorRecipe(Item tool, int minRepairLifeCost, int fullRepairLifeCost, int minRepairFuelCost, int fullRepairFuelCost) {
		this.tool = tool;
		this.fullRepairFuelCost = fullRepairFuelCost;
		this.fullRepairLifeCost = fullRepairLifeCost;
		this.minRepairLifeCost = minRepairLifeCost;
		this.minRepairFuelCost = minRepairFuelCost;
	}

	public ToolRepairAnimatorRecipe(IAnimatorRepairable repairable) {
		this.tool = (Item) repairable;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return !stack.isEmpty() && stack.getItemDamage() > 0 && stack.getItem() == this.tool && (this.tool instanceof IAnimatorRepairable == false || ((IAnimatorRepairable)this.tool).isRepairableByAnimator(stack));
	}

	@Override
	public int getRequiredFuel(ItemStack stack) {
		int minRepairFuelCost = this.minRepairFuelCost;
		int fullRepairFuelCost = this.fullRepairFuelCost;
		if(this.tool instanceof IAnimatorRepairable) {
			minRepairFuelCost = ((IAnimatorRepairable)this.tool).getMinRepairFuelCost(stack);
			fullRepairFuelCost = ((IAnimatorRepairable)this.tool).getFullRepairFuelCost(stack);
		}
		return minRepairFuelCost + MathHelper.ceil((fullRepairFuelCost - minRepairFuelCost) / (float)stack.getMaxDamage() * (float)stack.getItemDamage());
	}

	@Override
	public int getRequiredLife(ItemStack stack) {
		int minRepairLifeCost = this.minRepairLifeCost;
		int fullRepairLifeCost = this.fullRepairLifeCost;
		if(this.tool instanceof IAnimatorRepairable) {
			minRepairLifeCost = ((IAnimatorRepairable)this.tool).getMinRepairLifeCost(stack);
			fullRepairLifeCost = ((IAnimatorRepairable)this.tool).getFullRepairLifeCost(stack);
		}
		return minRepairLifeCost + MathHelper.ceil((fullRepairLifeCost - minRepairLifeCost) / (float)stack.getMaxDamage() * (float)stack.getItemDamage());
	}

	@Override
	public Entity getRenderEntity(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack getResult(ItemStack stack) {
		ItemStack result = stack.copy();
		result.setItemDamage(0);
		return result;
	}

	@Override
	public Class<? extends Entity> getSpawnEntityClass(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
		return true;
	}

	@Override
	public boolean getCloseOnFinish(ItemStack stack) {
		return false;
	}

	public Item getTool() {
		return tool;
	}
}
