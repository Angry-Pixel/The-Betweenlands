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
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class RingOfDispersionWorldRenderer {
	private final FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);

	private int rangeXZ;
	private int rangeY;

	private World world;

	private BlockPos pos = BlockPos.ORIGIN;
	private BlockPos renderedPos = BlockPos.ORIGIN;

	private boolean currentlyUsingVbos = false;

	private final Map<BlockRenderLayer, BufferBuilder> bufferBuilders = new EnumMap<>(BlockRenderLayer.class);
	private final Map<BlockRenderLayer, VertexBatchRenderer> batchRenderers = new EnumMap<>(BlockRenderLayer.class);

	public RingOfDispersionWorldRenderer(int rangeXZ, int rangeY) {
		this.rangeXZ = rangeXZ;
		this.rangeY = rangeY;
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

	protected VertexBatchRenderer getBatchRenderer(BlockRenderLayer layer) {
		VertexBatchRenderer renderer = this.batchRenderers.get(layer);
		if(renderer == null) {
			this.batchRenderers.put(layer, renderer = new VertexBatchRenderer(DefaultVertexFormats.BLOCK, OpenGlHelper.useVbo()));
		}
		return renderer;
	}

	public void render() {
		if(this.world == null) {
			return;
		}

		if(this.currentlyUsingVbos != OpenGlHelper.useVbo()) {
			this.deleteBuffers();
		}
		this.currentlyUsingVbos = OpenGlHelper.useVbo();

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
		//VertexBuffer buffer = this.getVertexBuffer(layer);
		VertexBatchRenderer batchRenderer = this.getBatchRenderer(layer);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		RenderHelper.disableStandardItemLighting();

		//mc.entityRenderer.enableLightmap();
		mc.entityRenderer.disableLightmap();

		batchRenderer.render();

		mc.entityRenderer.disableLightmap();
	}

	protected void rebuildVertexBuffer() {
		BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		for(BlockRenderLayer layer : BlockRenderLayer.values()) {
			this.getBufferBuilder(layer).begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		}

		for(int xo = -this.rangeXZ; xo <= this.rangeXZ; xo++) {
			for(int yo = -this.rangeY; yo <= 0; yo++) {
				for(int zo = -this.rangeXZ; zo <= this.rangeXZ; zo++) {
					BlockPos pos = new BlockPos(this.renderedPos.add(xo, yo, zo));
					IBlockState state = this.world.getBlockState(pos);

					EnumBlockRenderType renderType = state.getRenderType();

					if(!state.getBlock().isAir(state, this.world, pos) && (renderType == EnumBlockRenderType.MODEL || renderType == EnumBlockRenderType.LIQUID)) {
						state = state.getActualState(this.world, pos);

						IBakedModel blockModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);

						state = state.getBlock().getExtendedState(state, this.world, pos);

						if(blockModel != null) {
							for(BlockRenderLayer layer : BlockRenderLayer.values()) {
								if(state.getBlock().canRenderInLayer(state, layer)) {
									int prevPass = MinecraftForgeClient.getRenderPass();
									BlockRenderLayer prevLayer = MinecraftForgeClient.getRenderLayer();

									ForgeHooksClient.setRenderPass(0);
									ForgeHooksClient.setRenderLayer(layer);

									BufferBuilder bufferBuilder = this.getBufferBuilder(layer);

									bufferBuilder.setTranslation(-this.renderedPos.getX(), -this.renderedPos.getY(), -this.renderedPos.getZ());

									switch(renderType) {
									case MODEL:
										blockRenderer.getBlockModelRenderer().renderModel(this.world, blockModel, state, pos, bufferBuilder, layer == BlockRenderLayer.TRANSLUCENT);
										break;
									case LIQUID:
										blockRenderer.renderBlock(state, pos, this.world, bufferBuilder);
										break;
									}

									bufferBuilder.setTranslation(0, 0, 0);

									ForgeHooksClient.setRenderPass(prevPass);
									ForgeHooksClient.setRenderLayer(prevLayer);
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

			VertexBatchRenderer batchRenderer = this.getBatchRenderer(layer);
			batchRenderer.compile(bufferBuilder);
		}
	}

	public void deleteBuffers() {
		this.bufferBuilders.clear();

		for(VertexBatchRenderer batchRenderer : this.batchRenderers.values()) {
			batchRenderer.deleteBuffers();
		}
		this.batchRenderers.clear();
	}
}
