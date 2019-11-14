package thebetweenlands.common.item.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPestle extends Item implements IAnimatorRepairable {

    public ItemPestle() {
        setMaxDamage(128);
        maxStackSize = 1;
        setCreativeTab(BLCreativeTabs.GEARS);
        addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
            if(hasTag(stack) && stack.getTagCompound().getBoolean("active"))
                return 1;
            return 0;
        });
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add(TranslationHelper.translateToLocal("tooltip.bl.pestle"));
        list.add(TranslationHelper.translateToLocal("tooltip.bl.pestle.remaining", Math.round(100F - 100F / getMaxDamage() * getDamage(stack)), (getMaxDamage() - getDamage(stack))));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean map) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        stack.setTagCompound(new NBTTagCompound());
    }

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            return false;
        }
        return true;
    }

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 12;
	}
}