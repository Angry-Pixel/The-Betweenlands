package thebetweenlands.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBarnacle extends ItemBlock {

	public ItemBlockBarnacle(Block block) {
		super(block);
	}
	
	@Override
    public String getTranslationKey(ItemStack stack) {
        return "item.thebetweenlands.barnacle_larvae";
    }

	@Override
    public String getTranslationKey() {
        return "item.thebetweenlands.barnacle_larvae";
    }
}
