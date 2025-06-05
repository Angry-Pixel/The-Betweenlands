package thebetweenlands.client.renderer.block;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import thebetweenlands.client.renderer.util.StencilType;
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
//	                .setDepthTestState(RenderType.NO_DEPTH_TEST) // doesn't work
//	                .setWriteMaskState(RenderType.COLOR_WRITE)
	                .createCompositeState(true)
	            );
    
    public static final StencilModifier MIRROR_WORLD_STENCIL = new StencilModifier(TheBetweenlands.prefix("beam_origin_mirror_world"));

    public static final RenderModifiers BEAM_ORIGIN_MIRROR_MODIFIERS = RenderModifiers.build()
    		.add(StencilModifier.INSTANCE) // main stencil, locks to the mirror tri
    		.add(MIRROR_WORLD_STENCIL) // secondary stencil, makes sure the depth buffer is only reset where things will be rendered
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
	
	// I don't think vertex buffers are supported in block entities like they were in 1.12.2
	protected BeamOriginMirrorWorld mirrorWorld;
//	protected VertexBuffer mirrorWorldBuffer;
	
	public BeamOriginRenderer(BlockEntityRendererProvider.Context context) {
		this.blockRenderDispatcher = context.getBlockRenderDispatcher();
		this.entityRenderer = context.getEntityRenderer();
	}
	
	@Override
	public void render(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

		Minecraft.getInstance().getProfiler().push("thebetweenlands:beam_origin");
		
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

		Vec3 normal = vertex1.subtract(vertex0).cross(vertex2.subtract(vertex0)).normalize();
//		normal = normal.multiply(1, 0, 1).normalize(); // DEBUG - render on the xz axis
		Vector3f normalF = normal.toVector3f();

//		Vec3 mirrorCenter = vertex0.add(vertex1).add(vertex2).scale(1d / 3d);
		Vec3 mirrorCenter = vertex0;
		
		Tesselator tessellator = Tesselator.getInstance();
		
		Pose mirrorPose = poseStack.last();
		
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
//		BiConsumer<Boolean, Boolean> mirrorRenderer = (Boolean resetDepth, Boolean offsetDepth) -> {
		BooleanConsumer mirrorRenderer = (boolean resetDepth) -> {
			
			BufferBuilder builder = tessellator.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION);

			builder.addVertex(mirrorPose, (float)vertex0.x, (float)vertex0.y, (float)vertex0.z);
			builder.addVertex(mirrorPose, (float)vertex1.x, (float)vertex1.y, (float)vertex1.z);
			builder.addVertex(mirrorPose, (float)vertex2.x, (float)vertex2.y, (float)vertex2.z);
			
			MeshData mesh = builder.buildOrThrow();
			
			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.setupRenderState();
			GlStateBackup backup = new GlStateBackup();
			RenderSystem.backupGlState(backup);
			
			// prevent drawing colours
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.depthMask(resetDepth);
			
			if(resetDepth) {
//				if(offsetDepth) {
					// shift very far away from camera to "reset" the depth buffer inside the stencil
					RenderSystem.enablePolygonOffset();
					RenderSystem.polygonOffset(1.0f, 100000000.0f);
//				}
				RenderSystem.enableDepthTest();
				RenderSystem.depthFunc(GL11.GL_ALWAYS);
			} else //if(offsetDepth)
			{
				// shift slightly towards the camera so the stencil renders in front of the 
				// actual mirror triangle, but behind any blocks in front of it
				RenderSystem.enablePolygonOffset();
				RenderSystem.polygonOffset(-1.0f, -10.0f);
			}
			
			BufferUploader.drawWithShader(mesh);

			RenderSystem.restoreGlState(backup);
			BEAM_ORIGIN_MIRROR_STENCIL_LAYER.clearRenderState();
		};

//		Runnable stencilRenderer = () -> mirrorRenderer.accept(false, true);
		Runnable stencilRenderer = () -> mirrorRenderer.accept(false);
		
		// clone the current PoseStack
		PoseStack mirrorWorldPoseStack = new PoseStack();
		mirrorWorldPoseStack.last().pose().set(poseStack.last().pose());
		mirrorWorldPoseStack.last().normal().set(poseStack.last().normal());
		
		// used to make sure that we only reset depth in areas we're about to render over, should
		//     probably be turned into vertex buffers in future because we're doing an immediate render
		// if we don't do this, then we end up with things like clouds rendering in front of the mirror
		// also, the fire seems to render one frame behind/ahead here for some reason I think?
		Runnable mirrorWorldRenderer = () -> {
			GlStateBackup backup = new GlStateBackup();
			RenderSystem.backupGlState(backup);

			// TODO use a VertexBuffer for this
			MultiBufferSource.BufferSource mirrorWorldBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(786432));
			MultiBufferSource configured = BEAM_ORIGIN_MIRROR_WORLD_MODIFIERS.wrapBufferSourceAndConfigure(
					mirrorWorldBufferSource,
					(originalRenderType, modifiers) -> {
						modifiers.getContextOptional(RunnableModifier.INSTANCE)
							.ifPresent((RunnableModifier.RunnableShard ctx) -> {
								GlStateBackup writeBackup = new GlStateBackup();
								ctx.setSetupRenderState(() -> {
									RenderSystem.backupGlState(writeBackup);
									
									RenderSystem.colorMask(false, false, false, false);
									RenderSystem.disableDepthTest();
								});
								ctx.setClearRenderState(() -> {
									if(writeBackup.depthEnabled)
										RenderSystem.enableDepthTest();
									RenderSystem.colorMask(writeBackup.colorMaskRed, writeBackup.colorMaskGreen, writeBackup.colorMaskBlue, writeBackup.colorMaskAlpha);
								});
							});
					}
				);
			this.renderMirror(blockEntity, partialTick, mirrorWorldPoseStack, configured, mirrorCenter, normal);
			mirrorWorldBufferSource.endLastBatch();

			RenderSystem.restoreGlState(backup);
		};
		
		MultiBufferSource stencilledBufferSource = BEAM_ORIGIN_MIRROR_MODIFIERS.wrapBufferSourceAndConfigure(
				bufferSource,
				(originalRenderType, modifiers) -> {
					modifiers.getContextOptional(StencilModifier.INSTANCE)
						.ifPresent((StencilContext ctx) -> {
							ctx.setStencilRenderer(stencilRenderer);
							ctx.setStencilType(StencilType.STENCIL_IS_KEPT);
						});
					modifiers.getContextOptional(MIRROR_WORLD_STENCIL)
						.ifPresent((StencilContext ctx) -> {
							ctx.setStencilRenderer(mirrorWorldRenderer);
							ctx.setStencilType(StencilType.STENCIL_IS_KEPT);
						});
					modifiers.getContextOptional(RunnableModifier.INSTANCE)
						.ifPresent((RunnableModifier.RunnableShard ctx) -> {
							GlStateBackup writeBackup = new GlStateBackup();
							ctx.setSetupRenderState(() -> {
								var shader = RenderSystem.getShader();
								
								RenderSystem.backupGlState(writeBackup);
	
								// "reset" depth buffer in the stencilled area
//								mirrorRenderer.accept(true, true);
								mirrorRenderer.accept(true);
								
								RenderSystem.restoreGlState(writeBackup);
	
								// gl state backups don't save the shader
								RenderSystem.setShader(() -> shader);
							});
//							ctx.setClearRenderState(() -> {
//								var shader = RenderSystem.getShader();
//								
//								RenderSystem.backupGlState(writeBackup);
//	
//								// fix depth buffer in the stencilled area
//								// (so it's now equal to the original mirror tri)
//								mirrorRenderer.accept(true, false);
//								
//								RenderSystem.restoreGlState(writeBackup);
//	
//								// gl state backups don't save the shader
//								RenderSystem.setShader(() -> shader);
//							});
						});
				}
			);
		
		// debug normals
		if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
//			VertexConsumer lineConsumer = stencilledBufferSource.getBuffer(RenderType.LINES); // DEBUG - clip normals inside stencil mask
			VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.LINES);

			lineConsumer.addVertex(poseStack.last(), vertex0.toVector3f()).setColor(0, 0, 255, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
			lineConsumer.addVertex(poseStack.last(), vertex0.add(normal).toVector3f()).setColor(0, 0, 255, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);

			lineConsumer.addVertex(poseStack.last(), mirrorCenter.toVector3f()).setColor(255, 0, 0, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
			lineConsumer.addVertex(poseStack.last(), mirrorCenter.add(normal).toVector3f()).setColor(255, 0, 0, 255).setNormal(poseStack.last(), normalF.x, normalF.y, normalF.z);
		}

//		renderMirror(blockEntity, partialTick, poseStack, bufferSource, mirrorCenter, normal, stencilRenderer); // DEBUG - render mirror world outside the stencil
		renderMirror(blockEntity, partialTick, poseStack, stencilledBufferSource, mirrorCenter, normal);
		
		// moved this to the top of the function
//		// draw the actual mirror
//		{
//			final float alpha = 0.75f + 0.25f * (1 - visibility);
//			int color = FastColor.ARGB32.colorFromFloat(alpha, visibility, visibility, visibility);
//
//			VertexConsumer consumer = bufferSource.getBuffer(BEAM_ORIGIN_LAYER);
//			
//			consumer.addVertex(mirrorPose, (float)vertex0.x, (float)vertex0.y, (float)vertex0.z).setColor(color).setUv(0.5f, 1f).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
//			consumer.addVertex(mirrorPose, (float)vertex1.x, (float)vertex1.y, (float)vertex1.z).setColor(color).setUv(0.75f, 0).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
//			consumer.addVertex(mirrorPose, (float)vertex2.x, (float)vertex2.y, (float)vertex2.z).setColor(color).setUv(0.25f, 0).setLight(255).setNormal(mirrorPose, normalF.x(), normalF.y(), normalF.z());
//		}
		
		poseStack.popPose();
	}
	
	protected void renderMirror(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 mirrorCenter, Vec3 mirrorNormal) {
		
		poseStack.pushPose();
		
		mirrorTransform(poseStack, mirrorCenter, mirrorNormal);

		Vec3 renderCenter = Vec3.atLowerCornerWithOffset(blockEntity.getBlockPos(), 0.5, -1.5, 0.5);

		// this is good
		if(blockEntity.hasLevel()) {
			Level level = blockEntity.getLevel();
			for(Player entity : level.getEntitiesOfClass(Player.class, new AABB(blockEntity.getBlockPos()).inflate(20))) {
				EntityRenderer<? super Player> renderer = entityRenderer.getRenderer(entity);
				if(renderer != null) {
					poseStack.pushPose();

			        double renderX = Mth.lerp((double)partialTick, entity.xOld, entity.getX()) - renderCenter.x;
			        double renderY = Mth.lerp((double)partialTick, entity.yOld, entity.getY()) - renderCenter.y;
			        double renderZ = Mth.lerp((double)partialTick, entity.zOld, entity.getZ()) - renderCenter.z;
			        
			        float rotationYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
			        
			        entityRenderer.render(
			        		entity,
			        		renderX, renderY, renderZ,
			        		rotationYaw,
			        		partialTick,
			        		poseStack,
			        		bufferSource,
			        		255
		        		);
			        
					poseStack.popPose();
				}
			}
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
			// we could use a java.lang.reflect.Proxy to make a vertex consumer tee to help with generating the vertex buffer
			VertexConsumer builder = bufferSource.getBuffer(renderType);
			
			this.renderMirrorWorld(blockEntity, partialTick, poseStack, builder, renderType);
		}
		
		poseStack.popPose();
		
	}
	
	protected ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY);
	}
	
	protected boolean canRenderState(BlockState state, RandomSource random, RenderType renderType) {
		random.setSeed(42); // 42 is the "default" random seed used for rendering block models, apparently
		return this.blockRenderDispatcher.getBlockModel(state).getRenderTypes(state, random, ModelData.EMPTY).contains(renderType);
	}
	
	protected void createMirrorWorld() {
		if(this.mirrorWorld == null) {
			this.mirrorWorld = new BeamOriginMirrorWorld();
			this.mirrorWorld.setModelLightValue(220);
			this.mirrorWorld.setAOLightValue(0.9f);
		}
	}
	
	// a lot of this could probably be redone, we also need a vertex buffer
	
//	protected void regenerateMirrorWorldVBO(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource stencilledBufferSource) {
	protected void renderMirrorWorld(BeamOriginBlockEntity blockEntity, float partialTick, PoseStack poseStack, VertexConsumer builder, RenderType renderType) {
		poseStack.pushPose();
		
		// magic translations to make sure we're at (0, 0, 0) in the fake world
		poseStack.translate(-1.0F, -3.0F, -1.0F);
		
//		VertexBuffer buffer = new VertexBuffer(Usage.STATIC);

		final BlockState brazierBottomState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.LOWER);
		final BlockState brazierTopState = BlockRegistry.BRAZIER.get().defaultBlockState().setValue(BrazierBlock.HALF, DoubleBlockHalf.UPPER);
		final BlockState fireState = Blocks.FIRE.defaultBlockState();
		
//		this.blockRenderDispatcher.getBlockModel(brazierBottomState).getRenderTypes(state, rand, ModelData.EMPTY)

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
					Vec3 offset = Vec3.atCenterOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(brazierBottomState, pos, this.mirrorWorld, poseStack, builder, false, random, ModelData.EMPTY, renderType);
//					this.blockRenderDispatcher.renderSingleBlock(brazierBottomState, poseStack, stencilledBufferSource, 220, 0, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();

				poseStack.pushPose();
				if(renderBrazierTop) {
					random.setSeed(42);
					BlockPos pos = new BlockPos(x, 2, z);
					Vec3 offset = Vec3.atCenterOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(brazierTopState, pos, this.mirrorWorld, poseStack, builder, false, random, ModelData.EMPTY, renderType);
//					this.blockRenderDispatcher.renderSingleBlock(brazierTopState, poseStack, stencilledBufferSource, 220, 0, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();

				poseStack.pushPose();
				if(renderFire) {
					random.setSeed(42);
					BlockPos pos = new BlockPos(x, 3, z);
					Vec3 offset = Vec3.atCenterOf(pos);
					poseStack.translate(offset.x, offset.y, offset.z);
					this.blockRenderDispatcher.renderBatched(fireState, pos, this.mirrorWorld, poseStack, builder, false, random, ModelData.EMPTY, renderType);
//					this.blockRenderDispatcher.renderSingleBlock(brazierTopState, poseStack, stencilledBufferSource, 220, 0, ModelData.EMPTY, renderType);
				}
				poseStack.popPose();
			}
		}
		
//		buffer.upload(builder.buildOrThrow());
		
//		this.mirrorWorldBuffer = buffer;
		
		poseStack.popPose();
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
	
}
