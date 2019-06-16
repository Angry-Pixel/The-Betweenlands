package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall2;
import thebetweenlands.common.item.shields.ItemLivingWeedwoodShield;
import thebetweenlands.common.item.shields.ItemWeedwoodShield;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.LightingUtil;

public class RenderLivingWeedwoodShield extends TileEntityItemStackRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");
	private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small_glow.png");

	protected final ItemStack normalShield;
	protected final ModelSpiritTreeFaceSmall2 faceModel;

	public RenderLivingWeedwoodShield() {
		this.normalShield = new ItemStack(ItemRegistry.WEEDWOOD_SHIELD);
		this.faceModel = new ModelSpiritTreeFaceSmall2();
	}

	protected void copy(ItemStack stack) {
		((ItemWeedwoodShield)this.normalShield.getItem()).setBurningTicks(this.normalShield, ((ItemWeedwoodShield)stack.getItem()).getBurningTicks(stack));
	}

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		this.copy(stack);

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();
		
		GlStateManager.pushMatrix();

		GlStateManager.translate(0.5D, 0.5D, 0.5D);


		ITextureObject atlas = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		//Need to restore before rendering item because item renderer restores too
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		atlas.restoreLastBlurMipmap();

		mc.getRenderItem().renderItem(this.normalShield, TransformType.NONE);
		
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		atlas.setBlurMipmap(false, false);

		textureManager.bindTexture(TEXTURE);

		GlStateManager.scale(-1, -1, 1);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.translate(-0.5D, -0.65D, 0.02D);
		GlStateManager.scale(0.75D, 0.75D, 0.75D);
		GlStateManager.rotate(-5, 1, 0, 0);

		this.faceModel.renderOnShield();

		int spitTicks = ((ItemLivingWeedwoodShield) stack.getItem()).getSpitTicks(stack);

		if(spitTicks > 0) {
			float overlayAlpha = (spitTicks - partialTicks) / 15.0F;

			textureManager.bindTexture(TEXTURE_GLOW);

			GlStateManager.doPolygonOffset(0, -3.0F);
			GlStateManager.enablePolygonOffset();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.depthMask(false);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1, 1, 1, overlayAlpha);

			this.faceModel.renderOnShield();

			LightingUtil.INSTANCE.setLighting(255);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.color(overlayAlpha,overlayAlpha, overlayAlpha, overlayAlpha);

			this.faceModel.renderOnShield();

			LightingUtil.INSTANCE.revert();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(true);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
		}

		GlStateManager.popMatrix();
		
		//Reset GL state to what GUI item rendering expects
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	}
}
