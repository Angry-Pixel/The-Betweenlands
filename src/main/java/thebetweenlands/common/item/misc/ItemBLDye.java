package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemBLDye extends Item implements ItemRegistry.IMultipleItemModelDefinition {
    public ItemBLDye() {
        setMaxDamage(0);
        setHasSubtypes(true);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getTranslationKey() + "." + EnumBLDyeColor.byMetadata(i).getTranslationKey();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            Stream.of(EnumBLDyeColor.values()).forEach(t -> list.add(new ItemStack(this, 1, t.getMetadata())));
        }
    }

    @Override
    public Map<Integer, ResourceLocation> getModels() {
        Map<Integer, ResourceLocation> models = new HashMap<>();
        for(EnumBLDyeColor color : EnumBLDyeColor.values()) {
            models.put(color.getMetadata(), new ResourceLocation(ModInfo.ID, "dye_" + color.getDyeColorName()));
        }
        return models;
    }
}
