package thebetweenlands.common.item.armor;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemSkullMask extends ItemBLArmor {
	private static final ResourceLocation SKULL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/skull_mask.png");

	public ItemSkullMask() {
		super(BLMaterialRegistry.ARMOR_BONE, 2, EntityEquipmentSlot.HEAD, "skull_mask");
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
	
	@Override
	protected ResourceLocation getOverlayTexture(ItemStack stack, EntityPlayer player, float partialTicks) {
		return SKULL_TEXTURE;
	}
	
	@Override
	protected int getOverlayTextureFilter(ItemStack stack, EntityPlayer player, float partialTicks) {
		return GL11.GL_LINEAR;
	}
}
