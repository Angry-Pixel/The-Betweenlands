package thebetweenlands.common.dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import thebetweenlands.common.world.BetweenlandsClientLevel;

public class BetweenlandsLevelRenderer extends LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
	public BetweenlandsLevelRenderer(Minecraft p_109480_, RenderBuffers p_109481_) {
		super(p_109480_, p_109481_);
	}
}
