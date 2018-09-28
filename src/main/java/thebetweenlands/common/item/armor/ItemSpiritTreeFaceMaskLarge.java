package thebetweenlands.common.item.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelSpiritTreeFaceMaskLarge;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.lib.ModInfo;

public class ItemSpiritTreeFaceMaskLarge extends ItemSpiritTreeFaceMask {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large.png");

	@SideOnly(Side.CLIENT)
	private static ModelBiped model;

	public ItemSpiritTreeFaceMaskLarge() {
		super("spirit_tree_face_mask_large", (world, pos, facing) -> new EntitySpiritTreeFaceMask(world, pos, facing, EntitySpiritTreeFaceMask.Type.LARGE));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		if(model == null) {
			model = new ModelSpiritTreeFaceMaskLarge();
		}
		return model;
	}
}
