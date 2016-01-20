package thebetweenlands.items.loot;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.manual.IManualEntryItem;

public class ItemExplorerHat extends ItemArmor implements IManualEntryItem {
	private static final String TEXTURE = ModInfo.ID + ":textures/armour/explorerHat.png";

	public ItemExplorerHat() {
		super(ArmorMaterial.CLOTH, 2, 0);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemStack, ItemStack materialItemStack) {
		return false;
	}

	@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}

	@Override
	public ArmorMaterial getArmorMaterial() {
		return ArmorMaterial.CHAIN;
	}

	@Override
	public String manualName(int meta) {
		return "explorerHat";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[] { 6 };
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
		return TEXTURE;
	}

	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		return TheBetweenlands.proxy.getExplorersHatModel();
	}
}
