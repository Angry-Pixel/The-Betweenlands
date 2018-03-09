package thebetweenlands.common.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;

public class ShapelessOverrideDummyRecipe implements IRecipe {
	private ResourceLocation regName;

	public final IRecipe override, original;

	private NonNullList<Ingredient> ingredients;

	public ShapelessOverrideDummyRecipe(IRecipe override, IRecipe original) {
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
		if(this.ingredients == null) {
			NonNullList<Ingredient> originalIngredients = this.original.getIngredients();
			NonNullList<Ingredient> overrideIngredients = this.override.getIngredients();
			this.ingredients = NonNullList.withSize(originalIngredients.size(), Ingredient.EMPTY);
			for(int i = 0; i < originalIngredients.size(); i++) {
				Ingredient originalIngredient = originalIngredients.get(i);
				if(i < overrideIngredients.size() && originalIngredient != Ingredient.EMPTY) {
					Ingredient overrideIngredient = overrideIngredients.get(i);
					this.ingredients.set(i, new OverrideIngredient(this, overrideIngredient, originalIngredient));
				} else {
					this.ingredients.set(i, originalIngredient);
				}
			}
		}
		return this.ingredients;
	}

	@Override
	public String getGroup() {
		return this.original.getGroup();
	}

	/**
	 * Called by {@link OverrideIngredient} if an ingredient is invalidated
	 */
	protected void invalidate() {
		this.ingredients = null;
	}

	public static class ShapedOverrideDummyRecipe extends ShapelessOverrideDummyRecipe implements IShapedRecipe {
		public final IShapedRecipe shapedOriginal;

		public ShapedOverrideDummyRecipe(IRecipe override, IShapedRecipe original) {
			super(override, original);
			this.shapedOriginal = original;
		}

		@Override
		public int getRecipeWidth() {
			return this.shapedOriginal.getRecipeWidth();
		}

		@Override
		public int getRecipeHeight() {
			return this.shapedOriginal.getRecipeHeight();
		}
	}

	public static class OverrideIngredient extends Ingredient {
		public final Ingredient removed, original;
		public final ShapelessOverrideDummyRecipe recipe;

		private ItemStack[] stacks = null;
		private IntList itemIds = null;
		
		private int originalMatchingStacksLength = -1;
		private int originalValidItemStacksPackedSize = -1;

		public OverrideIngredient(ShapelessOverrideDummyRecipe recipe, Ingredient removed, Ingredient original) {
			super(0);
			this.recipe = recipe;
			this.removed = removed;
			this.original = original;
		}

		@Override
		public ItemStack[] getMatchingStacks() {
			//TODO: Check if original and removed Ingredient were invalidated
			ItemStack[] originalStacks = this.original.getMatchingStacks();
			if(this.stacks == null || originalStacks.length != this.originalMatchingStacksLength) {
				List<ItemStack> matchingStacks = new ArrayList<ItemStack>(originalStacks.length);
				for(ItemStack stack : originalStacks) {
					if(!this.removed.apply(stack)) {
						matchingStacks.add(stack);
					}
				}
				this.stacks = matchingStacks.toArray(new ItemStack[0]);
				this.originalMatchingStacksLength = originalStacks.length;
			}
			return this.stacks;
		}

		@Override
		public boolean apply(@Nullable ItemStack stack) {
			if(this.removed.apply(stack)) {
				return false;
			}
			return this.original.apply(stack);
		}

		@Override
		public IntList getValidItemStacksPacked() {
			//TODO: Check if original and removed Ingredient were invalidated
			IntList original = this.original.getValidItemStacksPacked();
			if(this.itemIds == null || this.originalValidItemStacksPackedSize != original.size()) {
				this.itemIds = new IntArrayList(original);
				this.itemIds.removeAll(this.removed.getValidItemStacksPacked());
				this.originalValidItemStacksPackedSize = original.size();
			}
			return this.itemIds;
		}

		@Override
		protected void invalidate() {
			//TODO: Invalidate original and removed Ingredient
			this.stacks = null;
			this.itemIds = null;
			this.recipe.invalidate();
		}

		@Override
		public boolean isSimple() {
			return this.original.isSimple();
		}
	}
}
