package thebetweenlands.common.item.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelSpiritTreeFaceMaskSmall;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.lib.ModInfo;

public class ItemSpiritTreeFaceMaskSmall extends ItemSpiritTreeFaceMask {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/spirit_tree_face_small_mask_overlay.png");
	public static final ResourceLocation OVERLAY_SIDE_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/spirit_tree_face_small_mask_overlay_side.png");

	@SideOnly(Side.CLIENT)
	private static ModelBiped model;

	public ItemSpiritTreeFaceMaskSmall() {
		super("spirit_tree_face_mask_small", (world, pos, facing) -> new EntitySpiritTreeFaceMask(world, pos, facing, EntitySpiritTreeFaceMask.Type.SMALL));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
		if(model == null) {
			model = new ModelSpiritTreeFaceMaskSmall(true);
		}
		return model;
	}

	@Override
	protected ResourceLocation getOverlayTexture(ItemStack stack, EntityPlayer player, float partialTicks) {
		return OVERLAY_TEXTURE;
	}

	@Override
	protected ResourceLocation getOverlaySideTexture(ItemStack stack, EntityPlayer player, float partialTicks, boolean left) {
		return OVERLAY_SIDE_TEXTURE;
	}
}