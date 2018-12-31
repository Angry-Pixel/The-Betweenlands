package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.common.entity.mobs.EntityShambler;

@SideOnly(Side.CLIENT)
public class RenderShambler extends RenderLiving<EntityShambler> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");

	public RenderShambler(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelShambler(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShambler entity) {
		return TEXTURE;
	}
}
