package thebetweenlands.client.handler;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.DiffBlitDepth;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.io.IOException;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_LEVEL;
import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_WEATHER;

/**
 *	Handles shader events such as registration, render stages and resizing.<br>
 *	Also removes the translucent element batch from our worldShader depth buffer during fast & fancy rendering modes.
 */
public class ShaderHandler {

    public static final ResourceLocation DIFFBLITDEPTH_LOCAL = ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/diffblitdepth.json");

	@Nullable
	public static DiffBlitDepth diffBlitDepth;

	/**
	 * Initializes depth composite helpers
	 */
    public static void loadWorldShader(ResourceProvider resourceProvider) {
        try {
            diffBlitDepth = new DiffBlitDepth(Minecraft.getInstance().getTextureManager(), resourceProvider, Minecraft.getInstance().getMainRenderTarget());
		} catch (IOException ioexception) {
            TheBetweenlands.LOGGER.warn("Failed to load shader: {}", DIFFBLITDEPTH_LOCAL, ioexception);
        } catch (JsonSyntaxException jsonsyntaxexception) {
            TheBetweenlands.LOGGER.warn("Failed to parse shader: {}", DIFFBLITDEPTH_LOCAL, jsonsyntaxexception);
        }
    }

	/**
	 * - Copies depth buffer data before renderItemInHand clears main depth buffer (only on Fantastic graphics).<br>
	 * - Collects viewport matrices for WorldShader.
	 */
	public static void onRenderWeather(final RenderLevelStageEvent event) {

		// Fetch depth and matrix data before debug elements render
		if (event.getStage() == AFTER_WEATHER) {
			// Fabulous graphics depth collector
			if (Minecraft.getInstance().levelRenderer.transparencyChain != null) {
				ShaderHelper.INSTANCE.getWorldShader().getDepthBuffer().copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
				Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
			}
			ShaderHelper.INSTANCE.getWorldShader().updateMatrices(event);
		}
	}

    /**
     * Renders the main shader to the screen.
     */
    public static void renderWorldShader(float partialTick) {
		if (!ShaderHelper.INSTANCE.canUseShaders()) return;
		ShaderHelper.INSTANCE.getWorldShader().uploadUniforms(partialTick);
		ShaderHelper.INSTANCE.getWorldShader().process(partialTick);
		ShaderHelper.INSTANCE.getWorldShader().cleanUp();
    }

	/**
	 * Composite new depth buffer for worldShader
	 */
	public static void onPreRenderDebug(PoseStack poseStack, MultiBufferSource buffer, Camera camera) {
		// Fast & Fancy only
		if (!ShaderHelper.INSTANCE.canUseShaders() || Minecraft.getInstance().levelRenderer.transparencyChain != null) return;

		// Composite changes after translucent batch on top of base buffer
		ShaderHandler.diffBlitDepth.AfterTarget.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
		RenderSystem.enableDepthTest();
		ShaderHandler.diffBlitDepth.process(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
		// Set worldShader depth to diffBlitDepth output
		ShaderHelper.INSTANCE.getWorldShader().getDepthBuffer().copyDepthFrom(ShaderHandler.diffBlitDepth.Output);
		// Clean up
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	/**
	 * Sets base buffer for cutting out translucent render batch
	 */
	public static void onPreTranslucentBatch() {
		// Set base buffer and cleanup
		ShaderHandler.diffBlitDepth.Base.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	/**
	 * Sets before buffer for cutting out translucent render batch
	 */
	public static void onPostTranslucentBatch() {
		ShaderHandler.diffBlitDepth.BeforeTarget.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}

	/**
	 * On render level stage AFTER_LEVEL call updateShaders
	 */
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_LEVEL) {
            updateShaders(event.getPartialTick().getRealtimeDeltaTicks());
        }
    }

	/**
	 * Calls shader instance to update and redraw shader textures
	 */
    private static void updateShaders(float partialTick) {
        if(ShaderHelper.INSTANCE.canUseShaders()) {
            //Initialize and update shaders and textures
            ShaderHelper.INSTANCE.updateShaders(partialTick);
        }
    }

	/**
	 *  On GameRender.resize
	 */
	public static void resize(int width, int height) {
		// Depth differential blit targets
		if (ShaderHandler.diffBlitDepth != null) {
			ShaderHandler.diffBlitDepth.resize(width, height);
		}

		// World shader targets
		if (ShaderHelper.INSTANCE.getWorldShader() != null) {
			ShaderHelper.INSTANCE.getWorldShader().resize(width, height);
		}
	}
}
