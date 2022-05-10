package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSilkMask extends ItemBLArmor {
	public ItemSilkMask() {
		super(BLMaterialRegistry.ARMOR_DECORATIVE, 2, EntityEquipmentSlot.HEAD, "silk_mask");
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isBookEnchantable(ItemStack is, ItemStack book) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
