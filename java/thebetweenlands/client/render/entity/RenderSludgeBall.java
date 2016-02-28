package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelSludgeBall;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSludgeBall extends Render {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
	private final ModelSludgeBall model;

	public RenderSludgeBall() {
		model = new ModelSludgeBall();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		model.render(0.0625F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
