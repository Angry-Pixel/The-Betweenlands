package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelMovingWall extends ModelBase {
	private ModelRenderer wall;
	
	public ModelMovingWall() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		
		this.wall = new ModelRenderer(this, 0, 0);
		this.wall.addBox(-24, -24, -8, 48, 48, 16);
		this.wall.setRotationPoint(0, 0, 0);
	}
	
	public void render() {
		this.wall.render(0.0625f);
	}
}
