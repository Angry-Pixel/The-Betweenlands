package thebetweenlands.items.lanterns;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;

public enum LightVariant {
	WEEDWOOD_LANTERN("weedwood_lantern", 32, new Object[]{" I ", "SDS", "IGI", 'I', "ingotIron", 'D', "dye", 'S', new ItemStack(Blocks.wooden_slab, 1, 5), 'G', "stickWood"}, 60);

	static {
		LightVariant[] variants = values();
		for (int meta = 0; meta < variants.length; meta++) {
			variants[meta].craftingResult = new ItemStack(BLItemRegistry.light, 4, meta);
		}
	}

	private String name;

	private float spacing;

	private ItemStack craftingResult;

	private Object[] craftingRecipe;

	private int tickCycle;

	LightVariant(String name, float spacing, Object[] craftingRecipe, int tickCycle) {
		this.name = name;
		this.spacing = spacing;
		this.craftingRecipe = craftingRecipe;
		this.tickCycle = tickCycle;
	}

	public String getName() {
		return name;
	}

	public float getSpacing() {
		return spacing;
	}

	public ItemStack getCraftingResult() {
		return craftingResult;
	}

	public Object[] getCraftingRecipe() {
		return craftingRecipe;
	}

	public int getTickCycle() {
		return tickCycle;
	}

	public boolean alwaysDoTwinkleLogic() {
		return false;
	}

	public static LightVariant getLightVariant(int index) {
		LightVariant[] variants = values();
		return variants[index < 0 || index >= variants.length ? 0 : index];
	}
}
