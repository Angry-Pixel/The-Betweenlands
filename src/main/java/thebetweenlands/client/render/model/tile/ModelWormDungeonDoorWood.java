package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWormDungeonDoorWood extends ModelBase {
	ModelRenderer door;

	public ModelWormDungeonDoorWood() {
		textureWidth = 128;
		textureHeight = 64;
		door = new ModelRenderer(this, 0, 0);
		door.setRotationPoint(0.0F, 0.0F, 0.0F);
		door.addBox(-24.0F, -24.0F, -8.0F, 48, 48, 16, 0.0F);
	}

	public void render() {
		door.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
