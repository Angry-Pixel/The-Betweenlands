package thebetweenlands.common.item.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;

public class ItemGem extends Item {
	public final CircleGemType type;

	public ItemGem(CircleGemType type) {
		this.type = type;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		CircleGemHelper.setGem(stack, this.type);
		return super.initCapabilities(stack, nbt);
	}
}
