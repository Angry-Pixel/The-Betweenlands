package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import thebetweenlands.client.render.model.entity.ModelPlayerVolarkite;

public class RenderPlayerVolarkite extends RenderPlayer {
	public RenderPlayerVolarkite(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.mainModel = new ModelPlayerVolarkite(0.0F, useSmallArms);
	}
}
