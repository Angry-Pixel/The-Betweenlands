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
import thebetweenlands.client.render.model.armor.ModelAmphibianArmor;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemAmphibianArmor extends ItemBLArmor {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/armor/amphibian.png");
	public static final ResourceLocation TEXTURE_AQUA = new ResourceLocation(ModInfo.ID, "textures/armor/amphibian_aqua.png");
	public static final ResourceLocation TEXTURE_CRIMSON = new ResourceLocation(ModInfo.ID, "textures/armor/amphibian_crimson.png");
	public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation(ModInfo.ID, "textures/armor/amphibian_green.png");

	@SideOnly(Side.CLIENT)
	private static ModelAmphibianArmor model;

	public ItemAmphibianArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIAN, 3, slot, "amphibian");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		CircleGemType gem = CircleGemHelper.getGem(stack);
		switch (gem) {
		case AQUA:
			return TEXTURE_AQUA.toString();
		case CRIMSON:
			return TEXTURE_CRIMSON.toString();
		case GREEN:
			return TEXTURE_GREEN.toString();
		default:
			return TEXTURE.toString();
		}
	}

	@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		if(model == null) {
			model = new ModelAmphibianArmor();
		}
		model.setVisibilities(armorSlot);
		return model;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
