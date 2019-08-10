package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelCryptCrawler;

@SideOnly(Side.CLIENT)
public class LayerHeldItemCryptCrawler implements LayerRenderer<EntityLivingBase> {
	protected final RenderLivingBase<?> livingEntityRenderer;

	public LayerHeldItemCryptCrawler(RenderLivingBase<?> livingEntityRendererIn) {
		livingEntityRenderer = livingEntityRendererIn;
	}

	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
		ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
		ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

		if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
			GlStateManager.pushMatrix();
			renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GlStateManager.popMatrix();
		}
	}

	private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			translateToHand(handSide);
			GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

			boolean flag = handSide == EnumHandSide.LEFT;
			GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, -0.55F, -0.35F);
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transformType, flag);
			GlStateManager.popMatrix();
		}
	}

	protected void translateToHand(EnumHandSide handSide) {
		((ModelCryptCrawler)livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
	}

	public boolean shouldCombineTextures() {
		return false;
	}
}
