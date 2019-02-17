package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.TranslationHelper;

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TranslationHelper.translateToLocal("tooltip.life_crystal.remaining", Math.round(100F - 100F / getMaxDamage() * getDamage(stack)) + "%"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
}