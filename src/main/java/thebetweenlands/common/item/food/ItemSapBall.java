package thebetweenlands.common.item.food;

import net.minecraft.item.ItemStack;

public class ItemSapBall extends ItemBLFood implements IDecayFood {
	public ItemSapBall() {
		super(0, 0f, false);
		setAlwaysEdible();
	}

	@Override
	public int getDecayHealAmount(ItemStack stack) {
		return 2;
	}
}
