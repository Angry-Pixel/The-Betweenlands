package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSpyEye;
import thebetweenlands.common.entity.EntitySpyEye;

@SideOnly(Side.CLIENT)
public class RenderSpyEye extends RenderLiving<EntitySpyEye> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/spy_eye.png");
	public RenderSpyEye(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSpyEye(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpyEye entity) {
		return TEXTURE;
	}
}
