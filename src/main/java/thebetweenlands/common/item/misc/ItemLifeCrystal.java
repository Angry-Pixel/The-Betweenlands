package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.TranslationHelper;

import java.util.List;

public class ItemLifeCrystal extends Item {

    public ItemLifeCrystal() {
        setMaxDamage(128);
        maxStackSize = 1;
        addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
            int damage = stack.getItemDamage();
            if (damage == getMaxDamage())
                return 4;
            if (damage > 96 && damage < getMaxDamage())
                return 3;
            if (damage > 64 && damage <= 96)
                return 2;
            if (damage > 32 && damage <= 64)
                return 1;
            return 0;
        });
        setCreativeTab(BLCreativeTabs.ITEMS);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(TranslationHelper.translateToLocal("tooltip.lifeCrystal.remaining", Math.round(100F - 100F / getMaxDamage() * getDamage(stack)) + "%"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
}