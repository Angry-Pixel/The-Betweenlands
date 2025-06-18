package thebetweenlands.client.renderer.block;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.GlStateBackup;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.renderer.util.BeamOriginMirrorWorld;
import thebetweenlands.client.renderer.util.ProxyMultiBufferSource;
import thebetweenlands.client.renderer.util.Stencil;
import thebetweenlands.client.renderer.util.StencilInfo;
import thebetweenlands.client.renderer.util.StencilState;
import thebetweenlands.client.renderer.util.StencilType;
import thebetweenlands.client.renderer.util.rendertype.ModifierSupportedRenderType;
import thebetweenlands.client.renderer.util.rendertype.ProxyRenderType;
import thebetweenlands.client.renderer.util.rendertype.RenderModifiers;
import thebetweenlands.client.renderer.util.rendertype.modifier.InvertCullingModifier;
import thebetweenlands.client.renderer.util.rendertype.modifier.RunnableModifier;
import thebetweenlands.client.renderer.util.rendertype.modifier.StencilModifier;
import thebetweenlands.client.renderer.util.rendertype.modifier.StencilModifier.StencilContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.BeamOriginBlockEntity;
import thebetweenlands.common.block.structure.BrazierBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class BeamOriginRenderer implements BlockEntityRenderer<BeamOriginBlockEntity> {
    
	// These all need to be moved to BLRenderTypes
	
	public static final TextureStateShard PRISM_MIRROR_TEXTURE = new RenderStateShard.TextureStateShard(
			TheBetweenlands.prefix("textures/entity/block/prism_mirror.png"), false, false
		);
	
    public static final RenderType BEAM_ORIGIN_MIRROR_STENCIL_LAYER = RenderType.create(
    			"thebetweenlands:beam_origin_mirror",
    			DefaultVertexFormat.POSITION,
    			Mode.TRIANGLES,
    			RenderType.TRANSIENT_BUFFER_SIZE,
    			true,
    			true,
    			RenderType.CompositeState.builder()
                	.setShaderState(RenderType.POSITION_SHADER)
	                .setLightmapState(RenderType.NO_LIGHTMAP)
	                .setTextureState(RenderType.NO_TEXTURE)
	                .setWriteMaskState(WriteMaskStateShard.DEPTH_WRITE)
	                .createCompositeState(true)
	            );

    public static final RenderModifiers BEAM_ORIGIN_MIRROR_MODIFIERS = RenderModifiers.build()
    		.add(StencilModifier.INSTANCE) // main stencil, locks to the mirror tri
    		.add(InvertCullingModifier.INSTANCE)
    		.add(RunnableModifier.INSTANCE) // should probably be replaced in future
    		.build("thebetweenlands:beam_origin_mirror");

    public static final RenderModifiers BEAM_ORIGIN_MIRROR_WORLD_MODIFIERS = RenderModifiers.build()
    		.add(RunnableModifier.INSTANCE) // should be replaced by a "No Writing To Render Buffers" modifier instead
    		.build("thebetweenlands:beam_origin_mirror_world");
    
    public static final RenderType BEAM_ORIGIN_LAYER = RenderType.create(
			"thebetweenlands:beam_origin",
			DefaultVertexFormat.BLOCK,
			Mode.TRIANGLES,
			RenderType.TRANSIENT_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
                .setLightmapState(RenderType.NO_LIGHTMAP)
                .setShaderState(RenderType.POSITION_COLOR_TEX_LIGHTMAP_SHADER) // TODO no POSITION_TEX_COLOR_NORMAL shader
                .setTextureState(PRISM_MIRROR_TEXTURE)
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderType.CULL)
                .createCompositeState(true)
            );

    public static final RenderType BEAM_ORIGIN_TOP_LAYER = RenderType.create(
    			"thebetweenlands:beam_origin_top",
    			DefaultVertexFormat.BLOCK,
    			Mode.QUADS,
    			RenderType.TRANSIENT_BUFFER_SIZE,
    			true,
    			true,
    			RenderType.CompositeState.builder()
	                .setLightmapState(RenderType.NO_LIGHTMAP)
	                .setShaderState(RenderType.POSITION_COLOR_TEX_LIGHTMAP_SHADER) // TODO no POSITION_TEX_COLOR_NORMAL shader
	                .setTextureState(PRISM_MIRROR_TEXTURE)
	                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
	                .setCullState(RenderType.CULL)
	                .createCompositeState(true)
	            );
	
    // This is just a little bit of memoization to help make some of the old code clearer, I'm happy with this
    // also, I flipped vertex 1 and vertex 2 so the faces are facing the front instead of the back
    
	public static final double TRIANGLE_HEIGHT = 1.65D;
	public static final double TRIANGLE_WIDTH  = 0.45D;

	protected static final Vec3 TRIANGLE_ORIGIN = Vec3.ZERO;
	protected static final Vec3[][] TRIANGLE_VERTICES;
	
	static {
		Vec3 vertex0 = TRIANGLE_ORIGIN.add(-TRIANGLE_WIDTH, 0, 0).add(TRIANGLE_WIDTH, 1, 0);
		Vec3 vertex1 = TRIANGLE_ORIGIN.add(0, TRIANGLE_HEIGHT, -TRIANGLE_WIDTH).add(TRIANGLE_WIDTH, 1, 0);
		Vec3 vertex2 = TRIANGLE_ORIGIN.add(0, TRIANGLE_HEIGHT, TRIANGLE_WIDTH).add(TRIANGLE_WIDTH, 1, 0);
		
		TRIANGLE_VERTICES = new Vec3[][] {
			{vertex0, vertex1, vertex2},
			{vertex0, vertex1.add(0, 0, TRIANGLE_WIDTH*2), vertex2.add(-TRIANGLE_WIDTH*2, 0, 0)},
			{vertex0, vertex1.add(-TRIANGLE_WIDTH*2, 0, TRIANGLE_WIDTH*2), vertex2.add(-TRIANGLE_WIDTH*2, 0, -TRIANGLE_WIDTH*2)},
			{vertex0, vertex1.add(-TRIANGLE_WIDTH*2, 0, 0), vertex2.add(0, 0, -TRIANGLE_WIDTH*2)},
		};		
	}
	
	protected final BlockRenderDispatcher blockRenderDispatcher;
	protected final EntityRenderDispatcher entityRenderer;
	
	
	protected BeamOriginMirrorWorld mirrorWorld;
	// only used for immediate rendering
	protected Map<RenderType, VertexBuffer> mirrorWorldVbos = new Object2ObjectArrayMap<>();
    protected final ByteBufferBuilder sharedBuffer;
	
	public BeamOriginRenderer(BlockEntityRendererProvider.Context context) {
		this.blockRenderDispatcher = context.getBlockRenderDispatcher();
		this.entityRenderer = context.getEntityRenderer();
		
		this.sharedBuffer = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
	}
	
	@Override
	public AABB getRenderBoundingBox(BeamOriginBlockEntity blockEntity) {
		return new AABB(-2.0D / 16.0, -8.0D / 16.0, -2.0D / 16.0, 18.0D / 16.0, 19.0D / 16.0, 18.0D / 16.0).move(blockEntity.getBlockPos());
	}
	
	@Override
	public void render(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

		Minecraft.getInstance().getProfiler().push("thebetweenlands:beam_origin");
		
		this.createMissingMirrorWorldVbos();
		
		poseStack.pushPose();
		poseStack.translate(0.5F, -1.5F, 0.5F);
		
		float visibility = Mth.lerp(partialTick, blockEntity.prevVisibility, blockEntity.visibility);
		
		float rotation = Mth.lerp(partialTick, blockEntity.prevRotation, blockEntity.rotation);
		
		Quaternionf quat = new Quaternionf().rotationY(rotation);

		// render mirror triangles
		
		renderMirrorTri(blockEntity, partialTick, poseStack, bufferSource, transformVec(quat, TRIANGLE_VERTICES[0][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[0][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[0][2], TRIANGLE_ORIGIN), visibility);
		renderMirrorTri(blockEntity, partialTick, poseStack, bufferSource, transformVec(quat, TRIANGLE_VERTICES[1][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[1][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[1][2], TRIANGLE_ORIGIN), visibility);
		renderMirrorTri(blockEntity, partialTick, poseStack, bufferSource, transformVec(quat, TRIANGLE_VERTICES[2][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[2][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[2][2], TRIANGLE_ORIGIN), visibility);
		renderMirrorTri(blockEntity, partialTick, poseStack, bufferSource, transformVec(quat, TRIANGLE_VERTICES[3][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[3][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[3][2], TRIANGLE_ORIGIN), visibility);

		// render top quad
		renderTop(blockEntity, partialTick, poseStack, bufferSource, quat, visibility);

		poseStack.popPose();

		Minecraft.getInstance().getProfiler().pop();
	}
	
	// TODO this needs to have the three mirror layers done
	protected void renderTop(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Quaternionf quat, float visibility) {

		Vector3f p0 = transformVec(quat, TRIANGLE_VERTICES[0][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p1 = transformVec(quat, TRIANGLE_VERTICES[1][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p2 = transformVec(quat, TRIANGLE_VERTICES[2][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p3 = transformVec(quat, TRIANGLE_VERTICES[3][2], TRIANGLE_ORIGIN).toVector3f();


		VertexConsumer bufferBuilder = bufferSource.getBuffer(BEAM_ORIGIN_TOP_LAYER);

		final float alpha = 0.75f + 0.25f * (1 - visibility);
		int color = FastColor.ARGB32.colorFromFloat(alpha, visibility, visibility, visibility);
		
		Pose pose = poseStack.last();
		
		bufferBuilder.addVertex(pose, p3).setColor(color).setUv(0.25f, 0.25f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p2).setColor(color).setUv(0.25f, 0.75f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p1).setColor(color).setUv(0.75f, 0.75f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p0).setColor(color).setUv(0.75f, 0.25f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		
	}
	
	protected void renderMirrorTri(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, float visibility) {
		poseStack.pushPose();

		final Vec3 mirrorNormal;
		{
			Vec3 normal = vertex1.subtract(vertex0).cross(vertex2.subtract(vertex0)).normalize();
//			normal = normal.multiply(1, 0, 1).normalize(); // DEBUG - render on the xz axis
			mirrorNormal = normal;
		}
		Vector3f normalF = mirrorNormal.toVector3f();

//		Vec3 mirrorCenter = vertex0.add(vertex1).add(vertex2).scale(1d / 3d);
		Vec3 mirrorCenter = vertex0;
		
		Tesselator tessellator = Tesselator.getInstance();
		
		Pose mirrorPose = poseStack.last().copy();
		
		// draw the actual mirror
		{
			final float alpha = 0.75f + 0.25f * (1 - visibility);
			int color = FastColor.ARGB32.colorFromFloat(alpha, visibility, visibility, visibility);

			VertexConsumer consumer = bufferSource.getBuffer(BEAM_ORIGIN_LAYER);
			
			consumer.addVertex(mirrorPose, (float)vertex0.x, (float)vertex0.y, (float)vertex0.z).setColor(color).setUv(0.5f, 1f).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
			consumer.addVertex(mirrorPose, (float)vertex1.x, (float)vertex1.y, (float)vertex1.z).setColor(color).setUv(0.75f, 0).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
			consumer.addVertex(mirrorPose, (float)vertex2.x, (float)vertex2.y, (float)vertex2.z).setColor(color).setUv(0.25f, 0).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
		}
		
		
		// rendering reflections stuff
		MirrorTriRenderer mirrorRenderer = (Runnable renderStatePatch) -> {
			
			BufferBuilder builder = tessellator.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION);

			builder.addVertex(mirrorPose, (float)vertex0.x, (float)vertex0.y, (float)vertex0.z);
			builder.addVertex(mirrorPose, (float)vertex1.x, (float)vertex1.y, (float)vertex1.z);
			builder.addVertex(mirrorPose, (float)vertex2.x, (float)vertex2.y, (float)vertex2.z);
			
			MeshData mesh = builder.buildOrThrow();
			
			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.setupRenderState();
			GlStateBackup backup = new GlStateBackup();
			RenderSystem.backupGlState(backup);

			renderStatePatch.run();
			
			BufferUploader.drawWithShader(mesh);

			RenderSystem.restoreGlState(backup);
			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.clearRenderState();
		};
		
		// clone the current PoseStack
		PoseStack mirrorWorldPoseStack = new PoseStack();
		mirrorWorldPoseStack.last().pose().set(mirrorPose.pose());
		mirrorWorldPoseStack.last().normal().set(mirrorPose.normal());
		
		final Supplier<List<? extends Entity>> entityGetter; 
		if(!blockEntity.hasLevel()) {
			entityGetter = List::of;
		} else {
			List<Player> players = blockEntity.getLevel().getEntitiesOfClass(Player.class, new AABB(blockEntity.getBlockPos()).inflate(20));
			entityGetter = () -> players;
		}
		
		MultiBufferSource stencilledBufferSource = new ProxyMultiBufferSource(bufferSource, (renderType, cachedRenderType) -> {
			if(cachedRenderType != null) return cachedRenderType;
			
			return new BeamOriginMirrorWorldRenderType(renderType, partialTick, mirrorWorldPoseStack.last().pose(), mirrorWorldPoseStack.last().normal(), mirrorRenderer, mirrorCenter, mirrorNormal, entityGetter, Vec3.atLowerCornerWithOffset(blockEntity.getBlockPos(), 0, -4.0, 0.0));
		});
		
		// debug normals
		if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
//			VertexConsumer lineConsumer = stencilledBufferSource.getBuffer(RenderType.LINES); // DEBUG - clip normals inside stencil mask
			VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.LINES);

			lineConsumer.addVertex(poseStack.last(), vertex0.toVector3f()).setColor(0, 0, 255, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
			lineConsumer.addVertex(poseStack.last(), vertex0.add(mirrorNormal).toVector3f()).setColor(0, 0, 255, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);

			lineConsumer.addVertex(poseStack.last(), mirrorCenter.toVector3f()).setColor(255, 0, 0, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
			lineConsumer.addVertex(poseStack.last(), mirrorCenter.add(mirrorNormal).toVector3f()).setColor(255, 0, 0, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
		}

//		renderMirror(blockEntity, partialTick, poseStack, bufferSource, mirrorCenter, normal, stencilRenderer); // DEBUG - render mirror world outside the stencil
		renderMirror(blockEntity, partialTick, poseStack, stencilledBufferSource, mirrorCenter, mirrorNormal);
		
		poseStack.popPose();
	}

	protected void renderMirror(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 mirrorCenter, Vec3 mirrorNormal) {
		renderMirror(blockEntity, partialTick, poseStack, bufferSource, mirrorCenter, mirrorNormal, null);
	}
	
	protected void renderMirror(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 mirrorCenter, Vec3 mirrorNormal, @Nullable RenderType limitRenderType) {
		
		if(limitRenderType != null) {
			bufferSource = new ProxyMultiBufferSource(bufferSource, (type, cache) -> {
				if(cache != null) {
					return cache;
				} else if(type == limitRenderType) {
					return type;
				} else {
					return null;
				}
			});
		}
		
		poseStack.pushPose();
		
		mirrorTransform(poseStack, mirrorCenter, mirrorNormal);

		Vec3 renderCenter = Vec3.atLowerCornerWithOffset(blockEntity.getBlockPos(), 0.5, -1.5, 0.5);

		// this is good
		if(blockEntity.hasLevel()) {
			renderMirrorEntities(blockEntity.getLevel(), new AABB(blockEntity.getBlockPos()).inflate(20), renderCenter, partialTick, poseStack, bufferSource);
		}
		
		// quite a bit of this could be rewritten

		this.createMirrorWorld();

		RandomSource random = RandomSource.create(42);
		
		final BlockState brazierBottomState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
		final BlockState brazierTopState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
		final BlockState fireState = Blocks.FIRE.defaultBlockState();
		
		ChunkRenderTypeSet renderTypes = ChunkRenderTypeSet.union(
				getRenderTypes(brazierBottomState, random),
				getRenderTypes(brazierTopState, random),
				getRenderTypes(fireState, random)
			);
		
		for(RenderType renderType : renderTypes) {
			if(limitRenderType != null && renderType != limitRenderType) continue;
			// we could use a java.lang.reflect.Proxy to make a vertex consumer tee to help with generating the vertex buffer
			VertexConsumer builder = bufferSource.getBuffer(renderType);
			
			this.renderMirrorWorld(poseStack, builder, renderType);
		}
		
		poseStack.popPose();
		
	}
	
	protected ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY);
	}
	
	protected boolean canRenderState(BlockState state, RandomSource random, RenderType renderType) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return renderType == null || this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY).contains(renderType);
	}
	
	protected void createMirrorWorld() {
		if(this.mirrorWorld == null) {
			this.mirrorWorld = new BeamOriginMirrorWorld();
			this.mirrorWorld.setModelLightValue(220);
			this.mirrorWorld.setAOLightValue(0.9f);
		}
	}
	
	// a lot of this could probably be redone, we also need a vertex buffer
	
	protected void clearMirrorWorldVBOs() {
		Set<RenderType> mirrorKeys = Set.copyOf(this.mirrorWorldVbos.keySet());
		for(RenderType renderType : mirrorKeys) {
			VertexBuffer buffer = this.mirrorWorldVbos.remove(renderType);
			if(buffer != null) {
				buffer.close();
			}
		}
	}
	
	protected void createMissingMirrorWorldVbos() {

		this.createMirrorWorld();

		RandomSource random = RandomSource.create(42);
		
		final BlockState brazierBottomState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
		final BlockState brazierTopState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
		final BlockState fireState = Blocks.FIRE.defaultBlockState();
		
		ChunkRenderTypeSet renderTypes = ChunkRenderTypeSet.union(
				getRenderTypes(brazierBottomState, random),
				getRenderTypes(brazierTopState, random),
				getRenderTypes(fireState, random)
			);

//		Tesselator tesselator = RenderSystem.isOnRenderThread() ? RenderSystem.renderThreadTesselator() : Tesselator.getInstance();
		Tesselator tesselator = Tesselator.getInstance();
		
		for(RenderType renderType : renderTypes) {
			VertexBuffer buffer = this.mirrorWorldVbos.get(renderType);
			if(buffer != null && !buffer.isInvalid()) continue;
			this.regenerateMirrorWorldVBO(tesselator, renderType);
		}
	}
	
	/**
	 * Rebuild and store the vertex buffer for the specified render type
	 * @param renderType the render type to generate a VBO for
	 */
	protected void regenerateMirrorWorldVBO(Tesselator tessellator, @Nonnull RenderType renderType) {
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
				// TODO separate cache for different vertex sorting modes?
//				meshData.sortQuads(this.sharedBuffer, RenderSystem.getVertexSorting());
				meshData.sortQuads(this.sharedBuffer, VertexSorting.DISTANCE_TO_ORIGIN);
			}

			vbo.bind();
			vbo.upload(meshData);
			
			this.mirrorWorldVbos.put(renderType, vbo);
		} else {
			this.mirrorWorldVbos.remove(renderType);
		}
	}


	protected void renderMirrorEntities(Level level, AABB entityAABB, Vec3 renderCenter, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
		final List<? extends Entity> entities;
		if(level != null) {
			entities = level.getEntitiesOfClass(Player.class, entityAABB);
		} else {
			entities = List.of();
		}
		renderMirrorEntities(entities, renderCenter, partialTick, poseStack, bufferSource);
	}
	
	protected void renderMirrorEntities(List<? extends Entity> entities, Vec3 renderCenter, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
		GlStateBackup backup = new GlStateBackup();
		for(Entity entity : entities) {
			EntityRenderer<? super Entity> renderer = this.entityRenderer.getRenderer(entity);
			if(renderer != null) {
				RenderSystem.backupGlState(backup);
				poseStack.pushPose();

		        double renderX = Mth.lerp((double)partialTick, entity.xo, entity.getX()) - renderCenter.x;
		        double renderY = Mth.lerp((double)partialTick, entity.yo, entity.getY()) - renderCenter.y;
		        double renderZ = Mth.lerp((double)partialTick, entity.zo, entity.getZ()) - renderCenter.z;
		        
		        float rotationYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

		        this.entityRenderer.setRenderShadow(false);
		        
		        this.entityRenderer.render(
		        		entity,
		        		renderX, renderY, renderZ,
		        		rotationYaw,
		        		partialTick,
		        		poseStack,
		        		bufferSource,
		        		255
	        		);
		        
				poseStack.popPose();
				RenderSystem.restoreGlState(backup);
			}
		}
		
        this.entityRenderer.setRenderShadow(true);
	}

	protected void renderEntireMirrorWorldImmediately(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack) {
		this.renderEntireMirrorWorldImmediately(blockEntity.hasLevel() ? blockEntity.getLevel() : null, new AABB(blockEntity.getBlockPos()).inflate(20), partialTick, poseStack);
	}

	protected void renderEntireMirrorWorldImmediately(@Nullable Level level, AABB entityAABB, float partialTick, PoseStack poseStack) {
		final List<? extends Entity> entities;
		if(level != null) {
			entities = level.getEntitiesOfClass(Player.class, entityAABB);
		} else {
			entities = List.of();
		}
		this.renderEntireMirrorWorldImmediately(entities, partialTick, poseStack, false);
	}

	protected void renderEntireMirrorWorldImmediately(List<? extends Entity> entities, float partialTick, PoseStack poseStack, boolean useVboRenderTypes) {

		MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(sharedBuffer);
		
		poseStack.pushPose();
		Vec3 renderCenter = new Vec3(0.5, 2.5, 0.5);

		renderMirrorEntities(entities, renderCenter, partialTick, poseStack, bufferSource);
		bufferSource.endLastBatch();
		poseStack.popPose();
		
		renderEntireMirrorWorldImmediatelyWithoutEntities(poseStack, useVboRenderTypes);
	}

	protected void renderEntireMirrorWorldImmediately(List<? extends Entity> entities, float partialTick, PoseStack poseStack, Vec3 entityOffset, Runnable renderStatePatch) {

		MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(sharedBuffer);
		
		MultiBufferSource patchedBufferSource;
		if(renderStatePatch == null) {
			patchedBufferSource = bufferSource;
		} else {
			patchedBufferSource = BEAM_ORIGIN_MIRROR_WORLD_MODIFIERS.wrapBufferSourceAndConfigure(
					bufferSource,
					(RenderType originalRenderType, ModifierSupportedRenderType modifiers) -> {
						modifiers.getContextOptional(RunnableModifier.INSTANCE)
							.ifPresent((ctx) -> {
								ctx.setSetupRenderState(renderStatePatch);
							});
					}
				);
		}
		
		poseStack.pushPose();
		renderMirrorEntities(entities, entityOffset, partialTick, poseStack, patchedBufferSource);
		bufferSource.endLastBatch();
		poseStack.popPose();
		
		renderEntireMirrorWorldImmediatelyWithoutEntities(poseStack, renderStatePatch);
	}

	protected void renderEntireMirrorWorldImmediatelyWithoutEntities(PoseStack poseStack, boolean useVboRenderTypes) {
		for(RenderType renderType : this.mirrorWorldVbos.keySet()) {
			renderMirrorWorldVbo(poseStack, renderType, useVboRenderTypes);
		}
	}

	protected void renderEntireMirrorWorldImmediatelyWithoutEntities(PoseStack poseStack, Runnable renderStatePatch) {
		GlStateBackup backup = new GlStateBackup();
		for(RenderType renderType : this.mirrorWorldVbos.keySet()) {
			RenderSystem.backupGlState(backup);
			renderType.setupRenderState();
			renderStatePatch.run();
			renderMirrorWorldVbo(poseStack, renderType, false);
			renderType.clearRenderState();
			RenderSystem.restoreGlState(backup);
		}
	}
	
	/**
	 * Renders a mirror world vertex buffer immediately
	 * @param poseStack
	 * @param renderType
	 */
	protected void renderMirrorWorldVbo(PoseStack poseStack, RenderType renderType, boolean useVboRenderTypes) {
		VertexBuffer vertexBuffer = this.mirrorWorldVbos.get(renderType);
		
		if(vertexBuffer != null && !vertexBuffer.isInvalid()) {
			GlStateBackup backup = null;
			if(useVboRenderTypes) {
				backup = new GlStateBackup();
				RenderSystem.backupGlState(backup);
				renderType.setupRenderState();
			}
			renderVboWithPose(poseStack.last().pose(), vertexBuffer);
			if(useVboRenderTypes) {
				renderType.clearRenderState();
				RenderSystem.restoreGlState(backup);
			}
		}	
	}

	/**
	 * Renders a vertex buffer immediately with a modified view matrix
	 * @param poseStack
	 * @param renderType
	 */
	public static void renderVboWithPose(Matrix4f pose, VertexBuffer vertexBuffer) {
		if(!Objects.requireNonNull(vertexBuffer).isInvalid()) {
//			Matrix4fStack stack = RenderSystem.getModelViewStack();
//			
//			stack.pushMatrix();
//			
//			try {
//				stack.mul(poseStack.last().pose());
//				RenderSystem.applyModelViewMatrix();
				Matrix4f modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
				modelViewMatrix.mul(pose);
				
				vertexBuffer.bind();
				vertexBuffer.drawWithShader(modelViewMatrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
				VertexBuffer.unbind();
//			} finally {
//				stack.popMatrix();
//				RenderSystem.applyModelViewMatrix();
//			}
		}
		
	}
	
	protected void renderMirrorWorld(PoseStack poseStack, VertexConsumer builder, RenderType renderType) {
		poseStack.pushPose();
		
		// translate to make sure we're at (0, 0, 0) in the fake world
		poseStack.translate(-0.5F, -2.5F, -0.5F);
		
		renderMirrorWorldTo(poseStack, builder, renderType);
		
		poseStack.popPose();
	}
	
	protected void renderMirrorWorldTo(PoseStack poseStack, VertexConsumer consumer, RenderType renderType) {

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

	// This is okay I think
	// look into whether it's possible to rewrite so changing the culled faces from BACK to FRONT isn't necessary
	
	public static void mirrorTransform(PoseStack poseStack, Vec3 mirrorCenter, Vec3 mirrorNormal) {
		poseStack.translate(mirrorCenter.x, mirrorCenter.y, mirrorCenter.z);

		scaleTransform(poseStack, mirrorNormal, -1);

		poseStack.translate(-mirrorCenter.x, -mirrorCenter.y, -mirrorCenter.z);
	}

	public static void scaleTransform(PoseStack poseStack, Vec3 axis, float scale) {
		float yaw = -(float)Math.atan2(axis.z, axis.x);
		float pitch = (float)(Math.atan2(Math.sqrt(axis.x*axis.x + axis.z*axis.z), -axis.y) + Math.PI);

		poseStack.mulPose(Axis.YP.rotation(yaw));
		poseStack.mulPose(Axis.ZP.rotation(pitch));
		
		poseStack.scale(1, scale, 1);

		poseStack.mulPose(Axis.ZN.rotation(pitch));
		poseStack.mulPose(Axis.YN.rotation(yaw));
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result
	 * @param quat			Quaternion to use for the transform
	 * @param point			Point to rotate
	 * @param centerPoint	Rotation center
	 * @return
	 */
	public static Vec3 transformVec(Quaternionf quat, Vec3 point, Vec3 centerPoint) {
		return new Vec3(transformVec(quat, point.toVector3f(), centerPoint.toVector3f()));
	}
	
	/**
	 * Transforms/Rotates the given point around the given center and returns the result
	 * @param quat			Quaternion to use for the transform
	 * @param point			Point to rotate
	 * @param centerPoint	Rotation center
	 * @return
	 */
	public static Vector3f transformVec(Quaternionf quat, Vector3f point, Vector3f centerPoint) {
		return quat.transform(point.sub(centerPoint, new Vector3f())).add(centerPoint);
	}
	
	
	// this is so cursed
	// TODO can probably be generalized and then re-used for things like dungeon worm holes
	public class BeamOriginMirrorWorldRenderType extends ProxyRenderType {

		public static final String PREFIX = "thebetweenlands:beam_origin_mirror_world/";

		protected final PoseStack poseStack;
		
		protected final MirrorTriRenderer mirrorRenderer;
		
		protected final StencilType stencilType = StencilType.STENCIL_IS_KEPT;
		protected final Deque<StencilInfo> stencilStack;
		
		protected final float partialTick;

		protected final Vec3 mirrorCenter;
		protected final Vec3 mirrorNormal;

		protected final Supplier<List<? extends Entity>> entityGetter;
		protected final Vec3 entityOffset;
		
		protected final GlStateBackup backup = new GlStateBackup();
		
		public BeamOriginMirrorWorldRenderType(RenderType delegate, float partialTick, Matrix4f pose, Matrix3f normal, MirrorTriRenderer mirrorRenderer, Vec3 mirrorCenter, Vec3 mirrorNormal, Supplier<List<? extends Entity>> entityGetter, Vec3 entityOffset) {
			super(PREFIX + delegate.name, delegate);
			
			this.poseStack = new PoseStack();
			this.poseStack.last().pose().set(pose);
			this.poseStack.last().normal().set(normal);
			
			this.mirrorRenderer = mirrorRenderer;

			this.stencilStack = new ArrayDeque<>();
			
			this.partialTick = partialTick;
			
			this.mirrorCenter = mirrorCenter;
			this.mirrorNormal = mirrorNormal;
			
			this.entityGetter = entityGetter;
			this.entityOffset = entityOffset;
		}
		
		public StencilInfo setupStencil() {
			// TODO get a better RenderTarget
			Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget());
			StencilInfo stencilInfo = StencilInfo.INVALID;
			
			if(stencil == null || !stencil.isValid()) {
				// invalid or null stencil
				stencilStack.addLast(StencilInfo.INVALID);
			} else {
				boolean stencilPushed = false;
				
				try {
					// Calculate stencil beforehand to avoid polluting the delegate's render state
					// (though most things should be covered by the state backup)
					GlStateBackup backup = new GlStateBackup();
					RenderSystem.backupGlState(backup);
					
					StencilState before = StencilState.get();
					
					// revert any changes to the stencil state (except to the stencil buffer itself) after running
					try (before) {
						GL11.glEnable(GL11.GL_STENCIL_TEST);
						
						if(stencilType == StencilType.STENCIL_IS_KEPT) {
							stencil.setAllZeros();
							// every time a pixel is drawn in stencilRenderer, the stencil bit will be set to 1
							stencil.func(GL11.GL_ALWAYS, true);
						} else {
							stencil.setAllOnes();
							// every time a pixel is drawn in stencilRenderer, the stencil bit will be set to 0
							stencil.func(GL11.GL_ALWAYS, false);
						}
						
						stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
						
						// draw primitives etc that get converted into the stencil mask
						mirrorRenderer.renderTriangleAsStencil();
					}
					
					// undo (most) changes that could be done by stencilRenderer
					RenderSystem.restoreGlState(backup);

					// push stencil info to stack so it can be reverted in clearRenderState()
					stencilInfo = new StencilInfo(stencil, before, null);
					stencilStack.addLast(stencilInfo);
					stencilPushed = true;
					
				} catch(Exception e) {
					// **NOT** a try-with-resources
					// if there is no exception, we expect the stencil to be closed in clearRenderState()
					stencil.close();
					
					// remove this last element off the stencil stack if we got to the point of adding one
					if(stencilPushed) {
						stencilStack.pollLast();
					}
					
					// because it's not impossible for the error to be handled and for setupRenderState()/clearRenderState()
					// to be called anyways, we have to keep the stack balanced to not lose the stencils
					stencilInfo = StencilInfo.INVALID;
					stencilStack.addLast(StencilInfo.INVALID);

					stencil = Stencil.INVALID;
					throw e;
				}
			}
			
			return stencilInfo;
		}

		public boolean applyStencil(Stencil stencil) {
			if(stencil == null || !stencil.isValid()) {
				return false;
			}
			
			StencilState currentState = StencilState.get();
			
			// enable stenciling
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			// if previous stencil state was compatible (e.g. multiple StencilledRenderType have been applied), 
			// then we require that both stencils pass
			if(currentState.stencilTestEnabled() && currentState.stencilFunc() == GL11.GL_EQUAL) {
				@SuppressWarnings("removal")
				int mask = stencil.getMask();
				RenderSystem.stencilFunc(GL11.GL_EQUAL, mask | currentState.stencilRef(), currentState.stencilMask() | mask);
			} else {
				stencil.func(GL11.GL_EQUAL, true);
			}
			
			// don't change stencils when drawing
			stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			
			return true;
		}
		
		public void setupDepth() {
			final List<? extends Entity> entities = entityGetter.get();
			
			GlStateBackup backup = new GlStateBackup();
			
			RenderSystem.backupGlState(backup);
			
			this.poseStack.pushPose();

			mirrorTransform(this.poseStack, this.mirrorCenter, this.mirrorNormal);

			GL11.glCullFace(GL11.GL_FRONT);

			this.poseStack.translate(-0.5F, -2.5F, -0.5F);
			
//			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.setupRenderState();
			
			BeamOriginRenderer.this.renderEntireMirrorWorldImmediately(entities, this.partialTick, this.poseStack, this.entityOffset, () -> {
				RenderSystem.enableDepthTest();
				RenderSystem.colorMask(false, false, false, false);
				RenderSystem.depthMask(true);

				RenderSystem.depthFunc(GL11.GL_GREATER);
				
				// minor time save here, should only be applied to this backside pass though
				RenderType.POSITION_SHADER.setupRenderState();
			});

			BeamOriginRenderer.this.renderEntireMirrorWorldImmediately(entities, this.partialTick, this.poseStack, this.entityOffset, () -> {
				RenderSystem.enableDepthTest();
				RenderSystem.colorMask(false, false, false, false);
				RenderSystem.depthMask(true);

				RenderSystem.depthFunc(GL11.GL_LEQUAL);
			});

			
//			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.clearRenderState();
			
			this.poseStack.popPose();

			RenderSystem.restoreGlState(backup);
		}
		
		public void resetDepth() {

			GlStateBackup backup = new GlStateBackup();
			
			RenderSystem.backupGlState(backup);
			
			this.poseStack.pushPose();

			this.poseStack.popPose();

			RenderSystem.restoreGlState(backup);
		}
		
		@Override
		public void setupRenderState() {
			RenderSystem.assertOnRenderThread();
			
			RenderSystem.backupGlState(backup);
			
			StencilInfo stencilInfo = this.setupStencil();
			
			try {
				if(!stencilInfo.isInvalid()) {
					this.applyStencil(stencilInfo.stencil());
					this.setupDepth();
					// don't want to actually change the stencil state
					stencilInfo.before().apply();
				}
				
				super.setupRenderState();

				StencilState afterState = StencilState.get();

				boolean pushed = false;
				
				try {
					stencilInfo = stencilInfo.withAfter(afterState);
					
					this.stencilStack.pollLast();
					
					this.stencilStack.addLast(stencilInfo);
					pushed = true;
				} catch(Exception e) {
					if(!pushed) {
						this.stencilStack.addLast(StencilInfo.INVALID);
					}
					
					throw e;
				}
				
				this.applyStencil(stencilInfo.stencil());
			} catch(Exception e) {
				// **NOT** a try-with-resources
				// if there is no exception, we expect the stencil to be closed in clearRenderState()
				stencilInfo.stencil().close();
				
				this.stencilStack.pollLast();
				this.stencilStack.addLast(StencilInfo.INVALID);
				
				throw e;
			}
			
			RenderSystem.depthFunc(GL11.GL_LEQUAL);
			
			RenderSystem.enablePolygonOffset();
			RenderSystem.polygonOffset(0.0f, -10.0f);
			
			GL11.glCullFace(GL11.GL_FRONT);
		}
		
		@Override
		public void draw(MeshData meshData) {
			this.setupRenderState();
	        BufferUploader.drawWithShader(meshData);
	        this.clearRenderState();
		}

		@Override
		public void clearRenderState() {

			GL11.glCullFace(GL11.GL_BACK);
			
			StencilInfo stencilInfo = this.stencilStack.pollLast();

			// close stencil
			stencilInfo.stencil().close();

			// reverse order because working backwards
			if(stencilInfo.after() != null) {
				stencilInfo.after().close();
			}
			
			RenderSystem.polygonOffset(backup.polyOffsetFactor, backup.polyOffsetUnits);
			if(!backup.polyOffsetFillEnabled) {
				RenderSystem.disablePolygonOffset();
			}
			
			super.clearRenderState();

			// reset depth
			RenderSystem.backupGlState(backup);
			mirrorRenderer.renderTriangleAsDepth();
			RenderSystem.restoreGlState(backup);
			
			// reverse order because working backwards
			if(stencilInfo.before() != null) {
				stencilInfo.before().close();
			}
			
		}
	}
	
	@FunctionalInterface
	public static interface MirrorTriRenderer {
		public void renderTriangle(Runnable renderStatePatch);

		public default void renderTriangle() {
			this.renderTriangle(() -> {});
		}

		public default void renderTriangleAsStencil() {
			this.renderTriangle(() -> {
				// prevent depth writes, but still require LEQUAL
				RenderSystem.depthMask(false);
				RenderSystem.depthFunc(GL11.GL_LEQUAL);
				// shift slightly towards the camera so the stencil renders in front of the 
				// actual mirror triangle, but behind any blocks in front of it
				RenderSystem.enablePolygonOffset();
				RenderSystem.polygonOffset(0.0f, -10.0f);
			});
		}

		public default void renderTriangleAsDepth() {
			this.renderTriangle(() -> {
				// allow depth writes
				RenderSystem.depthMask(true);
				RenderSystem.depthFunc(GL11.GL_LEQUAL);
			});
		}
	}
	
}
