package thebetweenlands.common.item.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelExplorersHat;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemExplorersHat extends ItemBLArmor {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/armor/explorers_hat.png");

	@SideOnly(Side.CLIENT)
	private static ModelBiped model;

	public ItemExplorersHat() {
		super(BLMaterialRegistry.ARMOR_BL_CLOTH, 2, EntityEquipmentSlot.HEAD, "explorers_hat");
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
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
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		if(model == null) {
			model = new ModelExplorersHat();
		}
		return model;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}
