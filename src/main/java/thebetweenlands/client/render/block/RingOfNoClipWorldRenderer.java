package thebetweenlands.client.render.block;

import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RingOfNoClipWorldRenderer {
	private final FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);

	private int rangeXZ;

	private World world;

	private BlockPos pos = BlockPos.ORIGIN;
	private BlockPos renderedPos = BlockPos.ORIGIN;

	private final VertexBufferUploader vertexBufferUploader = new VertexBufferUploader();
	private final Map<BlockRenderLayer, BufferBuilder> bufferBuilders = new EnumMap<>(BlockRenderLayer.class);
	private final Map<BlockRenderLayer, VertexBuffer> vertexBuffers = new EnumMap<>(BlockRenderLayer.class);

	public RingOfNoClipWorldRenderer(int rangeXZ) {
		this.rangeXZ = rangeXZ;
		this.fogColor.put(new float[4]);
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	protected BufferBuilder getBufferBuilder(BlockRenderLayer layer) {
		BufferBuilder builder = this.bufferBuilders.get(layer);
		if(builder == null) {
			this.bufferBuilders.put(layer, builder = new BufferBuilder(4000 /*TODO find good default value*/));
		}
		return builder;
	}

	protected VertexBuffer getVertexBuffer(BlockRenderLayer layer) {
		VertexBuffer buffer = this.vertexBuffers.get(layer);
		if(buffer == null) {
			this.vertexBuffers.put(layer, buffer = new VertexBuffer(DefaultVertexFormats.BLOCK));
		}
		return buffer;
	}

	public void render() {
		if(this.world == null) {
			return;
		}

		if(!this.pos.equals(this.renderedPos)) {
			this.renderedPos = this.pos;
			this.rebuildVertexBuffer();
		}

		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.pushMatrix();

		GlStateManager.translate(this.renderedPos.getX() - mc.getRenderManager().renderPosX, this.renderedPos.getY() - mc.getRenderManager().renderPosY, this.renderedPos.getZ() - mc.getRenderManager().renderPosZ);


		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.enableFog();

		this.fogColor.rewind();
		GlStateManager.glFog(GL11.GL_FOG_COLOR, this.fogColor);

		GlStateManager.setFog(FogMode.LINEAR);
		GlStateManager.setFogStart(this.rangeXZ - 1);
		GlStateManager.setFogEnd(this.rangeXZ);

		this.renderBuffers(mc);

		GlStateManager.disableFog();


		GlStateManager.popMatrix();
	}

	protected void renderBuffers(Minecraft mc) {
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);
		GlStateManager.disableAlpha();

		this.renderLayerBuffer(mc, BlockRenderLayer.SOLID);

		GlStateManager.enableAlpha();

		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, mc.gameSettings.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
		this.renderLayerBuffer(mc, BlockRenderLayer.CUTOUT_MIPPED);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		this.renderLayerBuffer(mc, BlockRenderLayer.CUTOUT);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		this.renderLayerBuffer(mc, BlockRenderLayer.TRANSLUCENT);

		GlStateManager.disableAlpha();
		this.renderLayerBuffer(mc, BlockRenderLayer.TRANSLUCENT);

		GlStateManager.enableAlpha();
	}

	protected void renderLayerBuffer(Minecraft mc, BlockRenderLayer layer) {
		VertexBuffer buffer = this.getVertexBuffer(layer);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		RenderHelper.disableStandardItemLighting();

		mc.entityRenderer.enableLightmap();

		//TODO Replace magic constants

		GlStateManager.glEnableClientState(32884);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.glEnableClientState(32888);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.glEnableClientState(32888);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.glEnableClientState(32886);

		buffer.bindBuffer();

		GlStateManager.glVertexPointer(3, 5126, 28, 0);
		GlStateManager.glColorPointer(4, 5121, 28, 12);
		GlStateManager.glTexCoordPointer(2, 5126, 28, 16);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.glTexCoordPointer(2, 5122, 28, 24);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);

		buffer.drawArrays(GL11.GL_QUADS);

		buffer.unbindBuffer();
		GlStateManager.resetColor();

		for (VertexFormatElement element : DefaultVertexFormats.BLOCK.getElements()) {
			VertexFormatElement.EnumUsage usage = element.getUsage();
			int index = element.getIndex();

			switch (usage) {
			case POSITION:
				GlStateManager.glDisableClientState(32884);
				break;
			case UV:
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
				GlStateManager.glDisableClientState(32888);
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
				break;
			case COLOR:
				GlStateManager.glDisableClientState(32886);
				GlStateManager.resetColor();
			}
		}

		mc.entityRenderer.disableLightmap();
	}

	protected void rebuildVertexBuffer() {
		BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		for(BlockRenderLayer layer : BlockRenderLayer.values()) {
			this.getBufferBuilder(layer).begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		}

		for(int xo = -5; xo <= 5; xo++) {
			for(int yo = -1; yo <= 0; yo++) {
				for(int zo = -5; zo <= 5; zo++) {
					BlockPos pos = new BlockPos(this.renderedPos.add(xo, yo, zo));
					IBlockState state = this.world.getBlockState(pos);

					if(!state.getBlock().isAir(state, this.world, pos) && state.getRenderType() == EnumBlockRenderType.MODEL) {
						state = state.getActualState(this.world, pos);

						IBakedModel blockModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);

						state = state.getBlock().getExtendedState(state, this.world, pos);

						if(blockModel != null) {
							for(BlockRenderLayer layer : BlockRenderLayer.values()) {
								if(state.getBlock().canRenderInLayer(state, layer)) {
									BufferBuilder bufferBuilder = this.getBufferBuilder(layer);

									bufferBuilder.setTranslation(-this.renderedPos.getX(), -this.renderedPos.getY(), -this.renderedPos.getZ());

									blockRenderer.getBlockModelRenderer().renderModel(this.world, blockModel, state, pos, bufferBuilder, layer == BlockRenderLayer.TRANSLUCENT);

									bufferBuilder.setTranslation(0, 0, 0);
								}
							}
						}
					}
				}
			}
		}

		for(BlockRenderLayer layer : BlockRenderLayer.values()) {
			BufferBuilder bufferBuilder = this.getBufferBuilder(layer);

			if(layer == BlockRenderLayer.TRANSLUCENT) {
				//Sort only on build, resorting is likely not necessary since the buffers
				//are rebuilt after moving a block
				bufferBuilder.sortVertexData(0, 2, 0);
			}

			bufferBuilder.finishDrawing();
			this.vertexBufferUploader.setVertexBuffer(this.getVertexBuffer(layer));
			this.vertexBufferUploader.draw(bufferBuilder);
		}
	}

	public void deleteBuffers() {
		this.bufferBuilders.clear();

		for(VertexBuffer vertexBuffer : this.vertexBuffers.values()) {
			vertexBuffer.deleteGlBuffers();
		}
		this.vertexBuffers.clear();
	}
}
