package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;
import thebetweenlands.common.block.misc.BlockMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.util.IsolatedBlockModelRenderer;

public class RenderMudFlowerPot extends TileEntitySpecialRenderer<TileEntityMudFlowerPot> {
	private static final IsolatedBlockModelRenderer blockRenderer = new IsolatedBlockModelRenderer().setUseRandomOffsets(false);

	@Override
	public final void renderTileEntityAt(TileEntityMudFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.25F, y + 0.35F, z + 0.75F);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		//vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		BlockPos pos = te.getPos();
		IBlockState potBlockState = te.getBlockType().getExtendedState(te.getWorld().getBlockState(pos), te.getWorld(), pos);

		if(potBlockState instanceof IExtendedBlockState) {
			IBlockState flowerBlockState = ((IExtendedBlockState)potBlockState).getValue(BlockMudFlowerPot.FLOWER);

			if(flowerBlockState != null) {
				IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(flowerBlockState);
				if(model != null) {
					/*blockRenderer.setLighting((IBlockState blockState, @Nullable EnumFacing facing) -> {
						return flowerBlockState.getPackedLightmapCoords(te.getWorld(), facing != null ? pos.up().offset(facing) : pos.up());
					}).setTint((IBlockState blockState, int tintIndex) -> {
						return Minecraft.getMinecraft().getBlockColors().colorMultiplier(flowerBlockState, null, null, tintIndex);
					});

					blockRenderer.renderModel(pos, model, flowerBlockState, MathHelper.getPositionRandom(pos), vertexBuffer);*/
					//TODO: Doesn't work, crashes when the face of a baked quad is null
					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(model, flowerBlockState, 1, true);
				}
			}
		}

		//tessellator.draw();

		GlStateManager.popMatrix();

		RenderHelper.enableStandardItemLighting();
	}
}
