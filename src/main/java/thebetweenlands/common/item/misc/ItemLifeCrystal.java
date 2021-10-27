package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.TranslationHelper;

public class ItemLifeCrystal extends Item {
	private boolean isRechargeable;

	public ItemLifeCrystal(int maxLife, boolean isRechargeable) {
		this.setMaxDamage(maxLife);
		this.maxStackSize = 1;
		this.isRechargeable = isRechargeable;

		this.addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
			int damage = stack.getItemDamage();
			if (damage >= stack.getMaxDamage())
				return 4;
			if (damage > stack.getMaxDamage() * 0.75f)
				return 3;
			if (damage > stack.getMaxDamage() * 0.5f)
				return 2;
			if (damage > stack.getMaxDamage() * 0.25f)
				return 1;
			return 0;
		});

		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	public boolean isRechargeable(ItemStack stack) {
		return this.isRechargeable;
	}

	@Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

	@Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)  {
        return false;
    }

	@Override
    public int getItemEnchantability() {
        return 0;
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.life_crystal.remaining", Math.round(100F - 100F / stack.getMaxDamage() * getDamage(stack)) + "%"));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage && this.isRechargeable(stack)) {
			//Don't let the crystal break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}
}