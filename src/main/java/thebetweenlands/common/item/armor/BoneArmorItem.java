package thebetweenlands.common.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;

public class BoneArmorItem extends ArmorItem {
	public static final ResourceLocation ARMOR_TEXTURE = TheBetweenlands.prefix("textures/models/armor/bone_layer.png");
	
	public BoneArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
		super(material, type, properties);
	}

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return ARMOR_TEXTURE;
	}
}
