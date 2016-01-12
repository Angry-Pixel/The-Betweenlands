package thebetweenlands.items.loot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterial;

import java.util.ArrayList;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemExplorerHat extends ItemArmor implements IManualEntryItem {
    public ItemExplorerHat() {
        super(ArmorMaterial.CLOTH, 2, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
        return "thebetweenlands:textures/armour/explorerHat.png";
    }

    @Override
    public String manualName(int meta) {
        return "explorerHat";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{6};
    }

    @Override
    public int metas() {
        return 0;
    }
}
