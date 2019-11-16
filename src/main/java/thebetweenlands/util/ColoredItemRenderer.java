package thebetweenlands.util;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Mostly C&P from {@link RenderItem}, but doesn't set blending state when rendering the item and allows specifying a color
 */
public class ColoredItemRenderer {
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	public static void renderItemAndEffectIntoGUI(RenderItem renderer, @Nullable EntityLivingBase entity, final ItemStack stack, int x, int y, float r, float g, float b, float a) {
		int color = 0;
		color |= (int)(b * 255) & 0xFF;
		color |= ((int)(g * 255) & 0xFF) << 8;
		color |= ((int)(r * 255) & 0xFF) << 16;
		color |= ((int)(a * 255) & 0xFF) << 24;
		renderItemAndEffectIntoGUI(renderer, entity, stack, x, y, color);
	}

	public static void renderItemAndEffectIntoGUI(RenderItem renderer, @Nullable EntityLivingBase entity, final ItemStack stack, int x, int y, int color)
	{
		if (!stack.isEmpty())
		{
			renderer.zLevel += 50.0F;

			try
			{
				renderItemModelIntoGUI(renderer, color, stack, x, y, renderer.getItemModelWithOverrides(stack, (World)null, entity));
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
				crashreportcategory.addDetail("Item Type", new ICrashReportDetail<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf((Object)stack.getItem());
					}
				});
				crashreportcategory.addDetail("Item Aux", new ICrashReportDetail<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack.getMetadata());
					}
				});
				crashreportcategory.addDetail("Item NBT", new ICrashReportDetail<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf((Object)stack.getTagCompound());
					}
				});
				crashreportcategory.addDetail("Item Foil", new ICrashReportDetail<String>()
				{
					@Override
					public String call() throws Exception
					{
						return String.valueOf(stack.hasEffect());
					}
				});
				throw new ReportedException(crashreport);
			}

			renderer.zLevel -= 50.0F;
		}
	}

	public static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, float r, float g, float b, float a) {
		int color = 0;
		color |= (int)(b * 255) & 0xFF;
		color |= ((int)(g * 255) & 0xFF) << 8;
		color |= ((int)(r * 255) & 0xFF) << 16;
		color |= ((int)(a * 255) & 0xFF) << 24;
		renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text, color);
	}
	
	public static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, int color)
	{
		if (!stack.isEmpty())
		{
			if (stack.getCount() != 1 || text != null)
			{
				String s = text == null ? String.valueOf(stack.getCount()) : text;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				fr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), multiplyColors(16777215, color));
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}

			if (stack.getItem().showDurabilityBar(stack))
			{
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				double health = stack.getItem().getDurabilityForDisplay(stack);
				int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
				int i = Math.round(13.0F - (float)health * 13.0F);
				int j = multiplyColors(rgbfordisplay, color);
				draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, (color >>> 24) & 0xFF);
				draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, (color >>> 24) & 0xFF);
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}

			EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
			float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

			if (f3 > 0.0F)
			{
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				Tessellator tessellator1 = Tessellator.getInstance();
				BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
				int bg = multiplyColors(0xFFFFFF7F, color);
				draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), bg >> 16 & 255, bg >> 8 & 255, bg & 255, bg >> 24 & 255);
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
	}

	private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha)
	{
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	private static void renderItemModelIntoGUI(RenderItem renderer, int color, ItemStack stack, int x, int y, IBakedModel bakedmodel)
	{
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		GlStateManager.pushMatrix();
		manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		manager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		setupGuiTransform(renderer, x, y, bakedmodel.isGui3d());
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
		renderItem(renderer, color, stack, bakedmodel);
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		manager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}

	private static void renderItem(RenderItem renderer, int color, ItemStack stack, IBakedModel model) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			if (model.isBuiltInRenderer()) {
				GlStateManager.enableRescaleNormal();
				stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
			} else {
				renderer.renderModel(model, color, stack);

				if (stack.hasEffect()) {
					renderer.renderEffect(model);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	private static void renderEffect(RenderItem renderer, int color, IBakedModel model) {
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();

		int tintColor = multiplyColors(-8372020, color);

		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		manager.bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderer.renderModel(model, tintColor, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderer.renderModel(model, tintColor, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		manager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	private static void setupGuiTransform(RenderItem renderer, int xPosition, int yPosition, boolean isGui3d) {
		GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + renderer.zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);

		if (isGui3d) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}
	
	private static int multiplyColors(int c1, int c2) {
		float c1b = c1 & 0xFF;
		float c1g = (c1 >>> 8) & 0xFF;
		float c1r = (c1 >>> 16) & 0xFF;
		float c1a = (c1 >>> 24) & 0xFF;

		float c2b = c2 & 0xFF;
		float c2g = (c2 >>> 8) & 0xFF;
		float c2r = (c2 >>> 16) & 0xFF;
		float c2a = (c2 >>> 24) & 0xFF;

		int color = 0;
		color |= (int)(c1b * c2b * 255) & 0xFF;
		color |= ((int)(c1g * c2g * 255) & 0xFF) << 8;
		color |= ((int)(c1r * c2r * 255) & 0xFF) << 16;
		color |= ((int)(c1a * c2a * 255) & 0xFF) << 24;
		
		return color;
	}
}
