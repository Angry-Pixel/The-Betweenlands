package thebetweenlands.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class OverrideDummyRecipe implements IRecipe {
	private ResourceLocation regName;

	public final IRecipe override, original;

	public OverrideDummyRecipe(IRecipe override, IRecipe original) {
		this.override = override;
		this.original = original;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		this.regName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return this.regName;
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return this.original.getRegistryType();
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		if(this.override.matches(inv, worldIn)) {
			return false;
		}
		return this.original.matches(inv, worldIn);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return this.original.getCraftingResult(inv);
	}

	@Override
	public boolean canFit(int width, int height) {
		return this.original.canFit(width, height);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.original.getRecipeOutput();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return this.original.getRemainingItems(inv);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.original.getIngredients();
	}

	@Override
	public String getGroup() {
		return this.original.getGroup();
	}
}
