package thebetweenlands.client.render.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;

@SideOnly(Side.CLIENT)
public class RenderWeedwoodSign extends TileEntitySpecialRenderer<TileEntityWeedwoodSign> {
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/tiles/weedwood_sign.png");

	private final ModelSign model = new ModelSign();

	@Override
	public void render(TileEntityWeedwoodSign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		Block block = te.getBlockType();
		GlStateManager.pushMatrix();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (block == BlockRegistry.STANDING_WEEDWOOD_SIGN) {
			GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			float f1 = (float)(te.getBlockMetadata() * 360) / 16.0F;
			GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
			this.model.signStick.showModel = true;
		} else {
			int meta = te.getBlockMetadata();
			float rotation = 0.0F;

			if (meta == 2) {
				rotation = 180.0F;
			}

			if (meta == 4) {
				rotation = 90.0F;
			}

			if (meta == 5) {
				rotation = -90.0F;
			}

			GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			this.model.signStick.showModel = false;
		}

		if (destroyStage >= 0) {
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(SIGN_TEXTURE);
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
		this.model.renderSign();
		GlStateManager.popMatrix();
		FontRenderer fontrenderer = this.getFontRenderer();
		GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);

		if (destroyStage < 0) {
			for (int i = 0; i < te.signText.length; ++i) {
				if (te.signText[i] != null) {
					ITextComponent itextcomponent = te.signText[i];
					List<ITextComponent> list = GuiUtilRenderComponents.splitText(itextcomponent, 90, fontrenderer, false, true);
					String s = list != null && !list.isEmpty() ? ((ITextComponent)list.get(0)).getFormattedText() : "";

					if (i == te.lineBeingEdited) {
						s = "> " + s + " <";
						fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - te.signText.length * 5, 0);
					} else {
						fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - te.signText.length * 5, 0);
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}