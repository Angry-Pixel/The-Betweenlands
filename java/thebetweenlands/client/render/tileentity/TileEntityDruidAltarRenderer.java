package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelAltar;
import thebetweenlands.client.model.block.ModelStone4;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDruidAltarRenderer extends TileEntitySpecialRenderer {
	private final ModelAltar model = new ModelAltar();
	private final ModelStone4 stone4 = new ModelStone4();

	private final ResourceLocation ACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarActive.png");
	private final ResourceLocation NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/druidAltar.png");
	private final ResourceLocation GLOW = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarGlow.png");

	public static TileEntityDruidAltarRenderer instance;

	public TileEntityDruidAltarRenderer() {
	}

	@Override
	public void func_147497_a(TileEntityRendererDispatcher renderer) {
		super.func_147497_a(renderer);
		instance = this;
	}

	public void renderTile(double x, double y, double z) {
		bindTexture(NORMAL);

		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		renderStones(x, y, z, 0);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(true);
		char c0 = 61680;
		int j = c0 % 65536;
		int k = c0 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

		bindTexture(GLOW);

		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		renderStones(x, y, z, 0);
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	private void renderTile(TileEntityDruidAltar tile, double x, double y, double z) {
		if (tile.blockMetadata == 1)
			bindTexture(ACTIVE);
		else
			bindTexture(NORMAL);

		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		renderStones(x, y, z, tile.rotation);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDepthMask(true);
		char c0 = 61680;
		int j = c0 % 65536;
		int k = c0 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
		bindTexture(GLOW);
		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		renderStones(x, y, z, tile.rotation);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
	}

	private void renderMainModel(double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll();
		GL11.glPopMatrix();
	}

	private void renderStones(double x, double y, double z, float rotation) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		stone4.renderAll();
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
		renderTile((TileEntityDruidAltar) tile, x, y, z);
	}
}