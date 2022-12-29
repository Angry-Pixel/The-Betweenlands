package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.client.render.model.armor.ModelSilkMask;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSilkMask extends Item3DArmor {

	public ItemSilkMask() {
		super(BLMaterialRegistry.ARMOR_DECORATIVE, 2, EntityEquipmentSlot.HEAD, "silk_mask");
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public ModelBodyAttachment createModel() {
		return new ModelSilkMask();
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
