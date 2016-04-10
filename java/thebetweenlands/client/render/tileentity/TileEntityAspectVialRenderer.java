package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelAlembic;
import thebetweenlands.tileentities.TileEntityAspectVial;
import thebetweenlands.utils.ColorUtils;

@SideOnly(Side.CLIENT)
public class TileEntityAspectVialRenderer extends TileEntitySpecialRenderer {
	private static final ModelAlembic MODEL = new ModelAlembic();
	public static final ResourceLocation TEXTURE1 = new ResourceLocation("thebetweenlands:textures/tiles/vialBlock1.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("thebetweenlands:textures/tiles/vialBlock2.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityAspectVial vial = (TileEntityAspectVial) tile;

		int meta = tile.getBlockMetadata();
		switch(meta) {
		default:
		case 0:
			bindTexture(TEXTURE1);
			break;
		case 1:
			bindTexture(TEXTURE2);
			break;
		}
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glScaled(2, 2, 2);
		GL11.glTranslated(-0.3F, -0.75F, 0.25F);

		if(vial.getAspect() != null) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			float[] aspectRGBA = ColorUtils.getRGBA(vial.getAspect().type.getColor());
			GL11.glColor4f(aspectRGBA[0] * 2, aspectRGBA[1] * 2, aspectRGBA[2] * 2, 0.98F);

			float filled = vial.getAspect().amount / TileEntityAspectVial.MAX_AMOUNT;

			if(filled != 0.0F) {
				GL11.glPushMatrix();
				GL11.glTranslated(0, -(23.5F * 0.0625F) * filled + (23.5F * 0.0625F), 0);
				GL11.glScalef(1, filled, 1);
				GL11.glFrontFace(GL11.GL_CW);
				MODEL.jar_liquid.render(0.0625F);
				GL11.glFrontFace(GL11.GL_CCW);
				GL11.glPopMatrix();
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1, 1, 1, 1.0F);
		}

		this.MODEL.davids_jar.render(0.0625F);
		GL11.glPopMatrix();
	}
}