package thebetweenlands.common.recipe.censer;

import net.minecraft.item.ItemStack;

class CenserRecipeElixirContext {
	public final ItemStack elixir;
	
	private boolean isConsuming = false;
	
	CenserRecipeElixirContext(ItemStack elixir) {
		this.elixir = elixir;
	}
	
	public void setConsuming(boolean consuming) {
		this.isConsuming = consuming;
	}
	
	public boolean isConsuming() {
		return this.isConsuming;
	}
}
