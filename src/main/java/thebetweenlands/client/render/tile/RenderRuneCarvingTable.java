package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelRuneCarvingTable;
import thebetweenlands.common.block.container.BlockRuneCarvingTable;
import thebetweenlands.common.tile.TileEntityRuneCarvingTable;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderRuneCarvingTable extends TileEntitySpecialRenderer<TileEntityRuneCarvingTable> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/rune_carving_table.png");
	public static final ModelRuneCarvingTable MODEL = new ModelRuneCarvingTable();

	@Override
	public void render(TileEntityRuneCarvingTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		this.bindTexture(TEXTURE);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (tile == null || !tile.hasWorld()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5F, y + 1.25F, z + 0.5F);
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			MODEL.render();
			GlStateManager.popMatrix();
			return;
		}

		if(destroyStage >= 0) {
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.scale(8.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		} else {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		int rotation = StatePropertyHelper.getStatePropertySafely(tile, BlockRuneCarvingTable.class, BlockRuneCarvingTable.FACING, EnumFacing.NORTH).getHorizontalIndex() * 90;
		GlStateManager.rotate(rotation - 180, 0, 1, 0);
		MODEL.render();
		GlStateManager.popMatrix();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if(destroyStage >= 0) {
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		}
	}
}

