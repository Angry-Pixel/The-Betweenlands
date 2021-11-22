package thebetweenlands.common.item.misc;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSapSpit extends Item {
    public ItemSapSpit() {
        setCreativeTab(BLCreativeTabs.ITEMS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.bl.item.sap_spit"));
    }
}
