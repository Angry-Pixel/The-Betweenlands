package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelFrog;
import thebetweenlands.common.entity.mobs.EntityFrog;

@SideOnly(Side.CLIENT)
public class RenderFrog extends RenderLiving<EntityFrog> {
	public static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
			new ResourceLocation("thebetweenlands:textures/entity/frog_0.png"),
			new ResourceLocation("thebetweenlands:textures/entity/frog_1.png"),
			new ResourceLocation("thebetweenlands:textures/entity/frog_2.png"),
			new ResourceLocation("thebetweenlands:textures/entity/frog_3.png"),
			new ResourceLocation("thebetweenlands:textures/entity/frog_poison.png")
	};

	public RenderFrog(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelFrog(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFrog entity) {
		EntityFrog frog = entity;
		if(frog.getSkin() < 0 || frog.getSkin() >= TEXTURES.length) {
			return TEXTURES[0];
		}
		return TEXTURES[frog.getSkin()];
	}
}
