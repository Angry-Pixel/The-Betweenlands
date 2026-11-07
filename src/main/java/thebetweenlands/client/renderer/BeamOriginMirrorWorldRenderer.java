package thebetweenlands.client.renderer;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.renderer.util.BeamOriginMirrorWorld;
import thebetweenlands.common.block.structure.BrazierBlock;
import thebetweenlands.common.registries.BlockRegistry;

public final class BeamOriginMirrorWorldRenderer implements ResourceManagerReloadListener {
	public static final BeamOriginMirrorWorldRenderer INSTANCE = new BeamOriginMirrorWorldRenderer();

	protected BlockRenderDispatcher blockRenderDispatcher = null;
	
	// Mirror world, to build the models
	protected BeamOriginMirrorWorld mirrorWorld;
	// Only used for immediate rendering
	protected final Map<RenderType, VertexBuffer> mirrorWorldVbos = new Object2ObjectArrayMap<>();
	// Buffer used to construct VBOs
    protected final ByteBufferBuilder sharedBuffer = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
	
	private BeamOriginMirrorWorldRenderer() {
		// Unit
	}

	// #region Create the Mirror World VBOs
	private void createMirrorWorld() {
		if(this.mirrorWorld == null) {
			this.mirrorWorld = new BeamOriginMirrorWorld();
			this.mirrorWorld.setModelLightValue(220);
			this.mirrorWorld.setAOLightValue(0.9f);
		}
	}

	private void createMissingMirrorWorldVbos() {
		this.createMirrorWorld();
		
		if (this.blockRenderDispatcher == null) {
			this.blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
		}

		RandomSource random = RandomSource.create(42);

		final BlockState brazierBottomState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
		final BlockState brazierTopState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
		final BlockState fireState = Blocks.FIRE.defaultBlockState();

		ChunkRenderTypeSet renderTypes = ChunkRenderTypeSet.union(
				getRenderTypes(brazierBottomState, random),
				getRenderTypes(brazierTopState,    random),
				getRenderTypes(fireState,          random)
			);

		Tesselator tesselator = Tesselator.getInstance();

		for(RenderType renderType : renderTypes) {
			VertexBuffer buffer = this.mirrorWorldVbos.get(renderType);
			if(buffer != null && !buffer.isInvalid()) continue;
			this.regenerateMirrorWorldVBO(tesselator, renderType);
		}
	}

	/**
	 * Rebuild and store the mirror world vertex buffer for the specified render type
	 * @param renderType the render type to generate a VBO for
	 */
	private void regenerateMirrorWorldVBO(Tesselator tessellator, @Nonnull RenderType renderType) {
		Objects.requireNonNull(renderType);

		VertexBuffer vbo = this.mirrorWorldVbos.compute(renderType, (key, oldVbo) -> {
			if(oldVbo != null) {
				oldVbo.close();
			}
			return new VertexBuffer(Usage.STATIC);
		});

		PoseStack poseStack = new PoseStack();

		BufferBuilder builder = tessellator.begin(renderType.mode(), renderType.format());

		renderMirrorWorldTo(poseStack, builder, renderType);

		MeshData meshData = builder.build();

		if(meshData != null) {
			if(renderType.sortOnUpload()) {
				meshData.sortQuads(this.sharedBuffer, VertexSorting.DISTANCE_TO_ORIGIN);
			}

			vbo.bind();
			vbo.upload(meshData);

			this.mirrorWorldVbos.put(renderType, vbo);
		} else {
			this.mirrorWorldVbos.remove(renderType);
		}
	}

	private void renderMirrorWorldTo(PoseStack poseStack, VertexConsumer consumer, RenderType renderType) {

		final BlockState brazierBottomState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
		final BlockState brazierTopState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
		final BlockState fireState = Blocks.FIRE.defaultBlockState();

		RandomSource random = RandomSource.create(42);

		boolean renderBrazierBottom = canRenderState(brazierBottomState, random, renderType);
		boolean renderBrazierTop = canRenderState(brazierTopState, random, renderType);
		boolean renderFire = canRenderState(fireState, random, renderType);

		for(int x : new int[] {3, -3}) {
			for(int z : new int[] {3, -3}) {
				poseStack.pushPose();
				if(renderBrazierBottom) {
					random.setSeed(42);
					BlockPos pos = new BlockPos(x, 1, z);
					Vec3 offset = Vec3.atLowerCornerOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(brazierBottomState, pos, this.mirrorWorld, poseStack, consumer, false, random, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();

				poseStack.pushPose();
				if(renderBrazierTop) {
					random.setSeed(42);
					BlockPos pos = new BlockPos(x, 2, z);
					Vec3 offset = Vec3.atLowerCornerOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(brazierTopState, pos, this.mirrorWorld, poseStack, consumer, false, random, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();

				poseStack.pushPose();
				if(renderFire) {
					random.setSeed(42);
					BlockPos pos = new BlockPos(x, 3, z);
					Vec3 offset = Vec3.atLowerCornerOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(fireState, pos, this.mirrorWorld, poseStack, consumer, false, random, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();
			}
		}
	}

	private ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY);
	}

	private boolean canRenderState(BlockState state, RandomSource random, RenderType renderType) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return renderType == null || this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY).contains(renderType);
	}
	// #endregion

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		// Invalidate VBOs
		for (VertexBuffer buffer : this.mirrorWorldVbos.values()) {
			buffer.close();
		}
		
		this.mirrorWorldVbos.clear();
		
		// Update block render dispatcher
		this.blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
	}
}
