package thebetweenlands.common.item.armor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.lib.ModInfo;

public abstract class Item3DArmor extends ItemBLArmor {
	@SideOnly(Side.CLIENT)
	private static Map<Class<? extends Item3DArmor>, ModelBodyAttachment> modelCache;

	@SideOnly(Side.CLIENT)
	private ModelBodyAttachment model;

	public Item3DArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String armorName) {
		super(materialIn, renderIndexIn, equipmentSlotIn, armorName);

		this.armorTextures[0] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + ".png";
		this.armorTextures[1] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + ".png";
	}

	@SideOnly(Side.CLIENT)
	protected ModelBodyAttachment createModel() {
		return null;
	}

	@Override
	public ItemBLArmor setGemArmorTextureOverride(CircleGemType type, String armorName) {
		this.gemArmorTextures[type.ordinal()][0] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + ".png";
		this.gemArmorTextures[type.ordinal()][1] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + ".png";
		return this;
	}

	@Override
	public int getColor(ItemStack itemStack) {
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		if(modelCache == null) {
			modelCache = new HashMap<>();
		}

		if(this.model == null) {
			this.model = modelCache.get(this.getClass());
		}

		if(this.model == null) {
			this.model = this.createModel();
			modelCache.put(this.getClass(), this.model);
		}

		if(this.model != null) {
			this.model.setVisibilities(armorSlot);
		}

		return this.model;
	}
}
