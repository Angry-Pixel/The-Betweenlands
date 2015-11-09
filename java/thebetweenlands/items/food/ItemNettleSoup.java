package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemNettleSoup extends ItemFood implements IManualEntryItem {

    public ItemNettleSoup() {
        super(10, 8.4F, false);
    }

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_BOWL);
	}

	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player) {
		is.stackSize--;
		player.getFoodStats().addStats(10, 8.4F);
		world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
	
		if (is.stackSize != 0)
			player.inventory.addItemStackToInventory(getContainerItem(is));

		if(is.stackSize == 0)
			return getContainerItem(is);

		return is;
	}

	@Override
	public String manualName(int meta) {
		return "nettleSoup";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int[] metas() {
		return new int[0];
	}
}
