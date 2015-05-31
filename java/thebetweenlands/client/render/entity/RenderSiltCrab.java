package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelSiltCrab;

@SideOnly(Side.CLIENT)
public class RenderSiltCrab extends RenderLiving {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/siltCrab.png");

	public RenderSiltCrab() {
		super(new ModelSiltCrab(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}