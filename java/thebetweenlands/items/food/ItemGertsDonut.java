package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemGertsDonut extends ItemFood implements IManualEntryItem {
    public ItemGertsDonut() {
        super(6, 13.2F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        player.heal(8.0F);
    }

    @Override
    public String manualName(int meta) {
        return "gertsDonut";
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
    public int metas() {
        return 0;
    }
}
