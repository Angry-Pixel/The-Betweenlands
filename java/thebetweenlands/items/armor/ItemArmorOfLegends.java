package thebetweenlands.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.entries.IManualEntryItem;
import thebetweenlands.recipes.BLMaterials;

/**
 * Created by Bart on 11-9-2015.
 */
public class ItemArmorOfLegends extends ItemArmor implements IManualEntryItem {
    public ItemArmorOfLegends(int armorType) {
        super(BLMaterials.armorOfLegends, 2, armorType);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (stack.getItem() == BLItemRegistry.legendaryLeggings)
            return "thebetweenlands:textures/armour/legendary2.png";
        else
            return "thebetweenlands:textures/armour/legendary1.png";
    }

    @Override
    public String manualName(int meta) {
        return "armorOfLegends";
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
