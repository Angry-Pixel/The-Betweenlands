package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelFlyingFiend;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFlyingFiend extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/flyingFiend.png");

	public RenderFlyingFiend() {
		super(new ModelFlyingFiend(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
