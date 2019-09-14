package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityMovingWall;

@SideOnly(Side.CLIENT)
public class RenderMovingWall extends Render<EntityMovingWall> {

	public RenderMovingWall(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityMovingWall entity, double x, double y, double z, float entityYaw, float partialTicks) {
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		texture.setBlurMipmap(false, false);

		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);

		IBakedModel itemModel1 = renderItem.getItemModelWithOverrides(entity.cachedStackTop(), (World) null, (EntityLivingBase) null);
		itemModel1 = ForgeHooksClient.handleCameraTransforms(itemModel1, ItemCameraTransforms.TransformType.NONE, false);
		
		IBakedModel itemModel2 = renderItem.getItemModelWithOverrides(entity.cachedStackMid(), (World) null, (EntityLivingBase) null);
		itemModel2 = ForgeHooksClient.handleCameraTransforms(itemModel2, ItemCameraTransforms.TransformType.NONE, false);
		
		IBakedModel itemModel3 = renderItem.getItemModelWithOverrides(entity.cachedStackBot(), (World) null, (EntityLivingBase) null);
		itemModel3 = ForgeHooksClient.handleCameraTransforms(itemModel3, ItemCameraTransforms.TransformType.NONE, false);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.5D, z);
		GlStateManager.rotate(entity.rotationYaw, 0F, 1F, 0F);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0D, 1D, 0D);
		renderItem.renderItem(entity.cachedStackTop(), itemModel1);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(- 1D, 1D, 0D);
		renderItem.renderItem(entity.cachedStackTop(), itemModel1);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(1D, 1D, 0D);
		renderItem.renderItem(entity.cachedStackTop(), itemModel1);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0D, 0D, 0D);
		renderItem.renderItem(entity.cachedStackMid(), itemModel2);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(- 1D, 0D, 0D);
		renderItem.renderItem(entity.cachedStackMid(), itemModel2);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(1D, 0D, 0D);
		renderItem.renderItem(entity.cachedStackMid(), itemModel2);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0D, - 1D, 0D);
		renderItem.renderItem(entity.cachedStackBot(), itemModel3);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(- 1D, - 1D, 0D);
		renderItem.renderItem(entity.cachedStackBot(), itemModel3);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(1D, - 1D, 0D);
		renderItem.renderItem(entity.cachedStackBot(), itemModel3);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		texture.restoreLastBlurMipmap();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMovingWall entity) {
		return null;
	}
}
