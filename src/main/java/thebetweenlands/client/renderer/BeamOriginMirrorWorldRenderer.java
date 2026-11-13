package thebetweenlands.client.renderer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.joml.Matrix4f;
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
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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
import net.neoforged.neoforge.client.GlStateBackup;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.handler.BeamOriginMirrorWorldRenderHandler.BeamOriginInstance;
import thebetweenlands.client.renderer.util.BeamOriginMirrorWorld;
import thebetweenlands.client.renderer.util.Stencil;
import thebetweenlands.common.block.structure.BrazierBlock;
import thebetweenlands.common.registries.BlockRegistry;

public final class BeamOriginMirrorWorldRenderer implements ResourceManagerReloadListener {
	public static final BeamOriginMirrorWorldRenderer INSTANCE = new BeamOriginMirrorWorldRenderer();

	// #region Triangle Dimensions
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
	// #endregion
	
	
	// #region Class fields
	protected BlockRenderDispatcher blockRenderDispatcher = null;
	
	// Mirror world, to build the models
	protected BeamOriginMirrorWorld mirrorWorld;
	// Only used for immediate rendering
	protected final Map<RenderType, VertexBuffer> mirrorWorldVbos = new Object2ObjectArrayMap<>();
	// Buffer used to construct VBOs
	protected final ByteBufferBuilder sharedBuffer = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
	// #endregion
	
	// Singleton class constructor
	private BeamOriginMirrorWorldRenderer() {
		// Unit
	}
	
	
	// #region Mirror World Rendering
	/**
	 * Context used so we don't have to pass around a million arguments
	 */
	private static record Context(
			Matrix4f modelViewMatrix, Matrix4f projectionMatrix,
			Stencil stencil, Tesselator tessellator,
			boolean renderDebugNormals
	) {

		/**
		 * Renders a vertex buffer immediately with a modified view matrix
		 * @param pose
		 * @param vertexBuffer
		 */
		public void renderVboWithPose(Matrix4f pose, VertexBuffer vertexBuffer) {
			if(!Objects.requireNonNull(vertexBuffer).isInvalid()) {
				Matrix4f modelViewMatrix = new Matrix4f(this.modelViewMatrix());
				modelViewMatrix.mul(pose);

				vertexBuffer.bind();
				vertexBuffer.drawWithShader(modelViewMatrix, this.projectionMatrix(), RenderSystem.getShader());
				VertexBuffer.unbind();
			}
		}
	}
	
	/**
	 * Renders multiple mirror worlds directly to the framebuffer
	 * @param poseStack
	 * @param partialTicks
	 * @param beamOrigins
	 * @param renderDebugNormals
	 */
	public void render(
			PoseStack poseStack, DeltaTracker partialTicks,
			Matrix4f modelViewMatrix, Matrix4f projectionMatrix,
			List<BeamOriginInstance> beamOrigins,
			boolean renderDebugNormals) {
		this.createMissingMirrorWorldVbos();
		
		// We can reuse one stencil bit for the entire bulk render
		try (Stencil stencil = Stencil.reserve(Minecraft.getInstance().getMainRenderTarget())) {
			if (!stencil.isValid()) {
				return;
			}
			
			// Create context
			Context context = new Context(
					modelViewMatrix, projectionMatrix,
					stencil, Tesselator.getInstance(),
					renderDebugNormals
			);
			
			// Quaternion for rendering
			Quaternionf quat = new Quaternionf();
			
			Vector3f[] triOut = new Vector3f[] {new Vector3f(), new Vector3f(), new Vector3f()};

			// Enable stenciling
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			
			for (int i = 0; i < beamOrigins.size(); ++i) {
				BeamOriginInstance beamOrigin = beamOrigins.get(i);
				
				quat.identity().rotationY(beamOrigin.rotation());
				
				// Render the beam origin mirrors
				renderBeamOriginMirror(context, beamOrigin, transformVec(quat, TRIANGLE_VERTICES[0][0], TRIANGLE_ORIGIN, triOut[0]), transformVec(quat, TRIANGLE_VERTICES[0][1], TRIANGLE_ORIGIN, triOut[1]), transformVec(quat, TRIANGLE_VERTICES[0][2], TRIANGLE_ORIGIN, triOut[2]));
				renderBeamOriginMirror(context, beamOrigin, transformVec(quat, TRIANGLE_VERTICES[1][0], TRIANGLE_ORIGIN, triOut[0]), transformVec(quat, TRIANGLE_VERTICES[1][1], TRIANGLE_ORIGIN, triOut[1]), transformVec(quat, TRIANGLE_VERTICES[1][2], TRIANGLE_ORIGIN, triOut[2]));
				renderBeamOriginMirror(context, beamOrigin, transformVec(quat, TRIANGLE_VERTICES[2][0], TRIANGLE_ORIGIN, triOut[0]), transformVec(quat, TRIANGLE_VERTICES[2][1], TRIANGLE_ORIGIN, triOut[1]), transformVec(quat, TRIANGLE_VERTICES[2][2], TRIANGLE_ORIGIN, triOut[2]));
				renderBeamOriginMirror(context, beamOrigin, transformVec(quat, TRIANGLE_VERTICES[3][0], TRIANGLE_ORIGIN, triOut[0]), transformVec(quat, TRIANGLE_VERTICES[3][1], TRIANGLE_ORIGIN, triOut[1]), transformVec(quat, TRIANGLE_VERTICES[3][2], TRIANGLE_ORIGIN, triOut[2]));
			}
			
			// Disable stencilling
			// TODO finally block
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}
	
	/**
	 * Renders a beam origin mirror immediately to framebuffer
	 * @param context    the context that this mirror will be rendered in
	 * @param beamOrigin the beam origin instance this mirror is for
	 * @param vertex0    triangle vertex 0
	 * @param vertex1    triangle vertex 1
	 * @param vertex2    triangle vertex 2
	 */
	private void renderBeamOriginMirror(
			Context context, BeamOriginInstance beamOrigin,
			Vector3f vertex0, Vector3f vertex1, Vector3f vertex2
	) {
		// GL state backup
		GlStateBackup backup = new GlStateBackup();
		RenderSystem.backupGlState(backup);

		// Create our mirror triangle mesh
		MeshData mirrorTriMesh = createMirrorMesh(context, beamOrigin, vertex0, vertex1, vertex2);
		
		Stencil stencil = context.stencil();
		
		// Set up our stencil mask
		drawMirrorAsStencil(stencil, mirrorTriMesh);

		// Set the depth in the mirror to a really large value
		drawMirrorAsDepth(stencil, mirrorTriMesh, true, GL11.GL_ALWAYS, 1000000.0f, 1000000.0f);
		
		// Draw our mirror world
		drawMirrorWorld(context, beamOrigin, vertex0, vertex1, vertex2);

		// Reset the depth to the original prism triangle depth
		drawMirrorAsDepth(stencil, mirrorTriMesh, true, GL11.GL_ALWAYS, 0.0f, 0.0f);
		
		// Reset GL state
		RenderSystem.restoreGlState(backup);
	}

	/**
	 * Draws the beam origin mirror world to the stencilled area
	 * @param context    the context that the mirror world will be rendered in
	 * @param beamOrigin the beam origin instance this mirror world is being rendered for
	 * @param vertex0    triangle vertex 0
	 * @param vertex1    triangle vertex 1
	 * @param vertex2    triangle vertex 2
	 */
	private void drawMirrorWorld(Context context, BeamOriginInstance beamOrigin, Vector3f vertex0, Vector3f vertex1, Vector3f vertex2) {
		
	}

	/**
	 * Creates the mesh for a mirror triangle with the specified context and instance
	 * @param context    the context that this mesh will be rendered in
	 * @param beamOrigin the beam origin instance this mesh is for
	 * @param vertex0    triangle vertex 0
	 * @param vertex1    triangle vertex 1
	 * @param vertex2    triangle vertex 2
	 */
	private MeshData createMirrorMesh(Context context, BeamOriginInstance beamOrigin, Vector3f vertex0, Vector3f vertex1, Vector3f vertex2) {
		Tesselator tessellator = context.tessellator();
		Pose mirrorPose = beamOrigin.pose();
		
		BufferBuilder builder = tessellator.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION);

		builder.addVertex(mirrorPose, vertex0);
		builder.addVertex(mirrorPose, vertex1);
		builder.addVertex(mirrorPose, vertex2);
		
		MeshData mesh = builder.buildOrThrow();
		
		return mesh;
	}
	
	/**
	 * Renders a beam origin mirror as a stencil to the framebuffer
	 * @param stencil       the stencil to render to
	 * @param mirrorTriMesh the mesh of the mirror tri
	 */
	private void drawMirrorAsStencil(Stencil stencil, MeshData mirrorTriMesh) {
		BLRenderTypes.BEAM_ORIGIN_STENCIL_LAYER.setupRenderState();
		
		// Prevent depth writes, but still require LEQUAL
		RenderSystem.depthMask(false);
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		
		// Shift slightly towards the camera so the stencil renders in front of the
		//       exist prism, but behind any blocks that might obscure it
		RenderSystem.enablePolygonOffset();
		RenderSystem.polygonOffset(0.0f, -10.0f);
		
		// Setup stencil state
		
		// Clear stencil so everything is discarded by default
		stencil.setAllZeros();
		// Every time a pixel is drawn, the stencil bit will be set to 1
		stencil.func(GL11.GL_ALWAYS, true);

		// Only draw pixels when the stencil test and depth test succeed
		stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		
		// Render our mirror tri
		BufferUploader.drawWithShader(mirrorTriMesh);
		
		BLRenderTypes.BEAM_ORIGIN_STENCIL_LAYER.clearRenderState();
	}

	/**
	 * Renders a beam origin mirror as depth
	 * @param stencil       the stencil to render to
	 * @param mirrorTriMesh the mesh of the mirror tri
	 */
	private void drawMirrorAsDepth(Stencil stencil, MeshData mirrorTriMesh,
			boolean depthMask, int depthFunc, float offsetFactor, float offsetUnits) {
		BLRenderTypes.BEAM_ORIGIN_STENCIL_LAYER.setupRenderState();
		
		// Prevent depth writes, but still require LEQUAL
		RenderSystem.depthMask(depthMask);
		RenderSystem.depthFunc(depthFunc);
		
		// Shift slightly towards the camera so the stencil renders in front of the
		//       exist prism, but behind any blocks that might obscure it
		RenderSystem.enablePolygonOffset();
		RenderSystem.polygonOffset(offsetFactor, offsetUnits);

		// Setup stencil state
		
		// Only draw pixels where our bit is set in the stencil
		stencil.func(GL11.GL_EQUAL, true);
		
		// Never change the stencil
		stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		
		// Render our mirror tri
		BufferUploader.drawWithShader(mirrorTriMesh);
		
		BLRenderTypes.BEAM_ORIGIN_STENCIL_LAYER.clearRenderState();
	}
	// #endregion Rendering


	// #region Prism rendering
	/**
	 * Renders the beam origin prism to a {@link MultiBufferSource}
	 * @param poseStack    the pose stack
	 * @param bufferSource the buffer source to render the prism to
	 * @param rotation     the beam origin's rotation at the time of rendering
	 * @param visibility   how visible the beam origin prism is right now
	 */
	public void renderPrism(PoseStack poseStack, MultiBufferSource bufferSource, float rotation, float visibility) {
		Quaternionf quat = new Quaternionf().rotationY(rotation);

		this.renderPrism(poseStack, bufferSource, quat, visibility);
	}

	/**
	 * Renders the beam origin prism to a {@link MultiBufferSource}
	 * @param poseStack    the pose stack
	 * @param bufferSource the buffer source to render the prism to
	 * @param quat         a quaternion representing the beam origin's rotation
	 * @param visibility   how visible the beam origin prism is right now
	 */
	public void renderPrism(PoseStack poseStack, MultiBufferSource bufferSource, Quaternionf quat, float visibility) {
		renderPrismTop(poseStack, bufferSource, quat, visibility);
		
		VertexConsumer triVertexConsumer = bufferSource.getBuffer(BLRenderTypes.BEAM_ORIGIN_PRISM_LAYER);

		renderPrismTri(poseStack, triVertexConsumer, transformVec(quat, TRIANGLE_VERTICES[0][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[0][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[0][2], TRIANGLE_ORIGIN), visibility);
		renderPrismTri(poseStack, triVertexConsumer, transformVec(quat, TRIANGLE_VERTICES[1][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[1][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[1][2], TRIANGLE_ORIGIN), visibility);
		renderPrismTri(poseStack, triVertexConsumer, transformVec(quat, TRIANGLE_VERTICES[2][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[2][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[2][2], TRIANGLE_ORIGIN), visibility);
		renderPrismTri(poseStack, triVertexConsumer, transformVec(quat, TRIANGLE_VERTICES[3][0], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[3][1], TRIANGLE_ORIGIN), transformVec(quat, TRIANGLE_VERTICES[3][2], TRIANGLE_ORIGIN), visibility);
	}

	protected void renderPrismTop(PoseStack poseStack, MultiBufferSource bufferSource, Quaternionf quat, float visibility) {
		Vector3f p0 = transformVec(quat, TRIANGLE_VERTICES[0][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p1 = transformVec(quat, TRIANGLE_VERTICES[1][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p2 = transformVec(quat, TRIANGLE_VERTICES[2][2], TRIANGLE_ORIGIN).toVector3f();
		Vector3f p3 = transformVec(quat, TRIANGLE_VERTICES[3][2], TRIANGLE_ORIGIN).toVector3f();


		VertexConsumer bufferBuilder = bufferSource.getBuffer(BLRenderTypes.BEAM_ORIGIN_TOP_LAYER);

		Pose pose = poseStack.last();

		bufferBuilder.addVertex(pose, p3).setColor(visibility, visibility, visibility, visibility).setUv(0.25f, 0.25f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p2).setColor(visibility, visibility, visibility, visibility).setUv(0.25f, 0.75f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p1).setColor(visibility, visibility, visibility, visibility).setUv(0.75f, 0.75f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
		bufferBuilder.addVertex(pose, p0).setColor(visibility, visibility, visibility, visibility).setUv(0.75f, 0.25f).setNormal(pose, 0.0f, 1.0f, 0.0f).setLight(255);
	}

	protected void renderPrismTri(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, float visibility) {
		renderPrismTri(poseStack, bufferSource.getBuffer(BLRenderTypes.BEAM_ORIGIN_PRISM_LAYER), vertex0, vertex1, vertex2, visibility);
	}
	
	protected void renderPrismTri(PoseStack poseStack, VertexConsumer consumer, Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, float visibility) {
		final Vec3 mirrorNormal;
		{
			Vec3 normal = vertex1.subtract(vertex0).cross(vertex2.subtract(vertex0)).normalize();
//			normal = normal.multiply(1, 0, 1).normalize(); // DEBUG - render on the xz axis
			mirrorNormal = normal;
		}
		Vector3f normalF = mirrorNormal.toVector3f();

		Pose pose = poseStack.last();
		
		renderPrismTri(pose, consumer, vertex0, vertex1, vertex2, normalF, visibility);
	}

	protected void renderPrismTri(Pose pose, VertexConsumer consumer, Vec3 vertex0, Vec3 vertex1, Vec3 vertex2, Vector3f normalF, float visibility) {		
		consumer.addVertex(pose, (float)vertex0.x, (float)vertex0.y, (float)vertex0.z).setColor(visibility, visibility, visibility, visibility).setUv(0.5f, 1f).setLight(255).setNormal(pose, normalF.x(), normalF.y(), normalF.z());
		consumer.addVertex(pose, (float)vertex1.x, (float)vertex1.y, (float)vertex1.z).setColor(visibility, visibility, visibility, visibility).setUv(0.75f, 0).setLight(255).setNormal(pose, normalF.x(), normalF.y(), normalF.z());
		consumer.addVertex(pose, (float)vertex2.x, (float)vertex2.y, (float)vertex2.z).setColor(visibility, visibility, visibility, visibility).setUv(0.25f, 0).setLight(255).setNormal(pose, normalF.x(), normalF.y(), normalF.z());
	}
	// #endregion Prism Rendering

	
	
	// #region Mirror Transformations

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
	 * @param quat        Quaternion to use for the transform
	 * @param point       Point to rotate
	 * @param centerPoint Rotation center
	 * @return
	 */
	public static Vec3 transformVec(Quaternionf quat, Vec3 point, Vec3 centerPoint) {
		return new Vec3(transformVec(quat, point.toVector3f(), centerPoint.toVector3f()));
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result
	 * @param quat        Quaternion to use for the transform
	 * @param point       Point to rotate
	 * @param centerPoint Rotation center
	 * @param out         The output vector
	 * @return
	 */
	public static Vector3f transformVec(Quaternionf quat, Vec3 point, Vec3 centerPoint, Vector3f out) {
		return transformVec(quat, point.toVector3f(), centerPoint.toVector3f(), out);
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result
	 * @param quat        Quaternion to use for the transform
	 * @param point       Point to rotate
	 * @param centerPoint Rotation center
	 * @return
	 */
	public static Vector3f transformVec(Quaternionf quat, Vector3f point, Vector3f centerPoint) {
		return transformVec(quat, point, centerPoint, new Vector3f());
	}

	/**
	 * Transforms/Rotates the given point around the given center and returns the result
	 * @param quat        Quaternion to use for the transform
	 * @param point       Point to rotate
	 * @param centerPoint Rotation center
	 * @param out         The output vector
	 * @return
	 */
	public static Vector3f transformVec(Quaternionf quat, Vector3f point, Vector3f centerPoint, Vector3f out) {
		return quat.transform(point.sub(centerPoint, out)).add(centerPoint);
	}
	// #endregion

	
	
	// #region Create mirror world VBOs
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
		
		// Reset mirror world
		this.mirrorWorld = null;
		
		// Update block render dispatcher
		this.blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
	}
}
