package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelPuffin;
import thebetweenlands.common.entity.mobs.EntityPuffin;

@SideOnly(Side.CLIENT)
public class RenderPuffin extends RenderLiving<EntityPuffin> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/puffin.png");

	public RenderPuffin(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelPuffin(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPuffin entity) {
		return TEXTURE;
	}
}
