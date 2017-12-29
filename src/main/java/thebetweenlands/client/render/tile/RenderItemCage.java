package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelItemCage;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.tile.TileEntityItemCage;


@SideOnly(Side.CLIENT)
public class RenderItemCage extends TileEntitySpecialRenderer<TileEntityItemCage> {

	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/item_cage_power.png");
	private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/item_cage.png");
	private final ModelItemCage model = new ModelItemCage();

	@Override
	public void render(TileEntityItemCage tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

		if(ShaderHelper.INSTANCE.isWorldShaderActive() && tile != null) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D, 
					3.5f,
					5.0f / 255.0f * 16.0F, 
					40.0f / 255.0f * 16.0F, 
					60.0f / 255.0f * 16.0F));
		}

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1, -1, -1);
		float f1 = ticks;
		bindTexture(FORCE_TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		float f2 = f1 * 0.0015F;
		float f3 = f1 * 0.0015F;
		GlStateManager.translate(f2, f3, f2);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableBlend();
		float f4 = 0.5F;
		GlStateManager.color(f4, f4, f4, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		model.renderBars();
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1, -1, -1);
		bindTexture(CAGE_TEXTURE);
		model.renderSolid();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
