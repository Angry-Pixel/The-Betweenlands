package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelTarBeast;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTarBeast extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/tarBeast.png");

	public RenderTarBeast() {
		super(new ModelTarBeast(), 0.7F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}