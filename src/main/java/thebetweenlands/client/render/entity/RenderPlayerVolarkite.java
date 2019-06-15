package thebetweenlands.client.render.entity;

import java.util.Iterator;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import thebetweenlands.client.render.entity.layer.LayerBipedArmorVolarkite;
import thebetweenlands.client.render.model.entity.ModelPlayerVolarkite;

public class RenderPlayerVolarkite extends RenderPlayer {
	public RenderPlayerVolarkite(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		this.mainModel = new ModelPlayerVolarkite(0.0F, useSmallArms);
		
		Iterator<LayerRenderer<AbstractClientPlayer>> it = this.layerRenderers.iterator();
		while(it.hasNext()) {
			LayerRenderer<AbstractClientPlayer> layer = it.next();
			if(layer.getClass() == LayerBipedArmor.class) {
				it.remove();
			}
		}
		
		this.addLayer(new LayerBipedArmorVolarkite(this));
	}
}
