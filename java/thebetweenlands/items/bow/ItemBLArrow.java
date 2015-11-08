package thebetweenlands.items.bow;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemBLArrow extends Item implements IManualEntryItem {
	private EnumArrowType type;

	public ItemBLArrow(String texture, EnumArrowType type) {
		super();
		this.type = type;
		setTextureName("thebetweenlands:" + texture);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		ItemBLArrow item = (ItemBLArrow) stack.getItem();
		if (item == BLItemRegistry.octineArrow)
			list.add(StatCollector.translateToLocal("arrow.caution"));
		if (item == BLItemRegistry.basiliskArrow)
			list.add(StatCollector.translateToLocal("arrow.stunning"));
	}

	public EnumArrowType getType() {
		return this.type;
	}

	@Override
	public String manualName(int meta) {
		return type.name() + "arrow";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}
}
