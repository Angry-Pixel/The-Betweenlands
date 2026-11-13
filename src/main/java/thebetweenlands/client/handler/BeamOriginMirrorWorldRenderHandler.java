package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.client.renderer.BeamOriginMirrorWorldRenderer;

/**
 * Handles rendering all beam origin mirror worlds.
 * 
 * <p>Put simply: stencils are not well supported, so directly rendering the mirror world in an event is our best option...</p>
 */
public class BeamOriginMirrorWorldRenderHandler {

	/**
	 * Thread-Local instance of a {@link MirrorWorldContext}, for each render thread.
	 * 
	 */
	private static final ThreadLocal<MirrorWorldContext> MIRROR_WORLD_CONTEXT = ThreadLocal.withInitial(MirrorWorldContext::new);
	
	/**
	 * Context that keeps a list of mirror world instances.
	 * Each context should only be accessed from a single thread.
	 * <p>The expected use of the context is as follows:</p>
	 * <ol>
	 *     <li>Before block entity rendering:
	 *         <ol>
	 *             <li>Beam Origin list is cleared</li>
	 *             <li>Context is marked as "active"</li>
	 *         </ol>
	 *     </li>
	 *     <li>During block entity rendering:
	 *         <ol>
	 *             <li>Beam Origins are submitted to the context</li>
	 *         </ol>
	 *     </li>
	 *     <li>After block entity rendering:
	 *         <ol>
	 *             <li>All Beam Origins have their mirror worlds rendered</li>
	 *             <li>Beam Origin list is cleared</li>
	 *             <li>Context is marked as "inactive"</li>
	 *         </ol>
	 *     </li>
	 * </ol>
	 * <p>Any attempts to submit Beam Origins to an inactive context are ignored</p>
	 */
	private static class MirrorWorldContext {
		private final List<BeamOriginInstance> instances;
		private boolean isActive;
		
		public MirrorWorldContext() {
			this.instances = new ArrayList<BeamOriginInstance>();
			this.isActive = false;
		}
		
		/**
		 * Tries to submit a Beam Origin instance to this context.
		 * Will reject an instance if this context is marked "inactive".
		 * @param instance the beam origin instance to submit
		 * @return whether or not the beam origin instance was submitted
		 */
		public boolean submit(BeamOriginInstance instance) {
			if (this.isActive) {
				return this.instances.add(instance);
			}
			return false;
		}

		/**
		 * {@return the list of all submitted instances}
		 */
		public List<BeamOriginInstance> getInstances() {
			return this.instances;
		}

		/**
		 * Clears the instance list
		 */
		public void clear() {
			this.instances.clear();
		}
		
		/**
		 * {@return whether this instance is active}
		 */
		public boolean isActive() {
			return this.isActive;
		}
		
		/**
		 * Sets this instance as "active" or "inactive"
		 * @param active the new flag
		 */
		public void setActive(boolean active) {
			this.isActive = active;
		}
	}
	
	/**
	 * Represents a Beam Origin instance
	 */
	public static record BeamOriginInstance(
			PoseStack.Pose pose, float visibility, float rotation,
			int packedLight, int packedOverlay,
			List<? extends Entity> mirrorEntities
	) {
		
	}
	
	/**
	 * Attempts to submit a beam origin to mirror world rendering.
	 * @param pose           the pose that the beam origin should be rendered with
	 * @param visibility     the beam origin's visibility
	 * @param rotation       the beam origin's rotation
	 * @param packedLight    packed light at the beam origin
	 * @param packedOverlay  packed overlay at the beam origin
	 * @param mirrorEntities entities to render in the mirror world
	 * @return {@code true} if the beam origin will be rendered by the render handler
	 */
	public static boolean submitBeamOrigin(
			PoseStack.Pose pose, float visibility, float rotation,
			int packedLight, int packedOverlay,
			List<? extends Entity> mirrorEntities) {
		// Get the context
		MirrorWorldContext context = MIRROR_WORLD_CONTEXT.get();
		
		if (!context.isActive()) {
			return false;
		}
		
		BeamOriginInstance instance = new BeamOriginInstance(
				pose.copy(), visibility, rotation,
				packedLight, packedOverlay,
				mirrorEntities);
		
		return context.submit(instance);
	}
	
	/**
	 * Registers event listeners for beam origin rendering
	 */
	public static void init() {
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW,  BeamOriginMirrorWorldRenderHandler::onBeforeRenderBlockEntities);
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, BeamOriginMirrorWorldRenderHandler::onAfterRenderBlockEntities);
	}
	
	/**
	 * Called before block entities are rendered
	 * @param event
	 */
	private static void onBeforeRenderBlockEntities(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_ENTITIES) {
			// Get the context
			MirrorWorldContext context = MIRROR_WORLD_CONTEXT.get();
			
			// Clear and mark active
			context.clear();
			context.setActive(true);
		}
	}

	/**
	 * Called after block entities are rendered
	 * @param event
	 */
	private static void onAfterRenderBlockEntities(RenderLevelStageEvent event) {
		if (event.getStage() == Stage.AFTER_BLOCK_ENTITIES) {
			// Get the context
			MirrorWorldContext context = MIRROR_WORLD_CONTEXT.get();
			
			try {
				// Render mirror worlds
				renderMirrorWorlds(event, context.getInstances());
			} finally {
				// Clear and mark inactive
				context.clear();
				context.setActive(false);
			}
		}
	}
	
	/**
	 * Renders all the mirror worlds that were queued
	 * @param beamOrigins
	 */
	private static void renderMirrorWorlds(RenderLevelStageEvent event, List<BeamOriginInstance> beamOrigins) {
		final EntityRenderDispatcher entityRenderDispatcher = event.getLevelRenderer().entityRenderDispatcher;
		final boolean renderDebugNormals = entityRenderDispatcher != null && entityRenderDispatcher.shouldRenderHitBoxes();
		
		BeamOriginMirrorWorldRenderer.INSTANCE.render(
				event.getPoseStack(),
				event.getPartialTick(),
				beamOrigins,
				renderDebugNormals);
	}
}
