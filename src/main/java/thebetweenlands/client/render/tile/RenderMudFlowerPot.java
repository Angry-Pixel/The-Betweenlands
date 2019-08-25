package thebetweenlands.client.render.tile;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.block.IsolatedBlockModelRenderer;
import thebetweenlands.common.block.misc.BlockMudFlowerPot;
import thebetweenlands.common.tile.TileEntityMudFlowerPot;
import thebetweenlands.util.StatePropertyHelper;

public class RenderMudFlowerPot extends TileEntitySpecialRenderer<TileEntityMudFlowerPot> {
	private static final IsolatedBlockModelRenderer BLOCK_RENDERER = new IsolatedBlockModelRenderer();
	
	static {
		BLOCK_RENDERER.setUseRandomOffsets(false);
	}
	
	@Override
	public void render(TileEntityMudFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState flowerBlockState = StatePropertyHelper.getStatePropertySafely(te, BlockMudFlowerPot.class, BlockMudFlowerPot.FLOWER, null, false, true);

		if(flowerBlockState != null && flowerBlockState.getBlock() != Blocks.AIR) {
			World world = te.getWorld();
			BlockPos pos = te.getPos();
			
			IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(flowerBlockState);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexBuffer = tessellator.getBuffer();
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();

			if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.325F, y + 0.4F, z + 0.325F);
			GlStateManager.scale(0.35F, 0.35F, 0.35F);

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			BLOCK_RENDERER.setLighting((IBlockState blockState, @Nullable EnumFacing facing) -> {
				return world.getBlockState(pos.up()).getPackedLightmapCoords(world, facing != null ? pos.up().offset(facing) : pos.up());
			}).setTint((IBlockState blockState, int tintIndex) -> {
				return Minecraft.getMinecraft().getBlockColors().colorMultiplier(flowerBlockState, world, pos.up(), tintIndex);
			});
			
			BLOCK_RENDERER.renderModel(te.getWorld(), BlockPos.ORIGIN, model, flowerBlockState, MathHelper.getPositionRandom(te.getPos()), vertexBuffer);
			
			tessellator.draw();

			GlStateManager.popMatrix();

			GlStateManager.shadeModel(GL11.GL_FLAT);
			RenderHelper.enableStandardItemLighting();
		}
	}
}
