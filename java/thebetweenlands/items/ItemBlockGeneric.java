package thebetweenlands.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGeneric
        extends ItemBlock
{
	public ItemBlockGeneric(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return this.getUnlocalizedName() + "_" + is.getItemDamage();
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	public Block getBlock() {
		return field_150939_a;
	}
}
