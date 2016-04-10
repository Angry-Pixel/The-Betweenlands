package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelFrog;
import thebetweenlands.entities.mobs.EntityFrog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFrog extends RenderLiving {
	private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
		new ResourceLocation("thebetweenlands:textures/entity/frog0.png"),
		new ResourceLocation("thebetweenlands:textures/entity/frog1.png"),
		new ResourceLocation("thebetweenlands:textures/entity/frog2.png"),
		new ResourceLocation("thebetweenlands:textures/entity/frog3.png"),
		new ResourceLocation("thebetweenlands:textures/entity/frogPoison.png")
		};

	public RenderFrog() {
		super(new ModelFrog(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		EntityFrog frog = (EntityFrog) entity;
		switch (frog.getSkin()) {
		case 0:
			return TEXTURES[0];
		case 1:
			return TEXTURES[1];
		case 2:
			return TEXTURES[2];
		case 3:
			return TEXTURES[3];
		case 4:
			return TEXTURES[4];
		default:
			return TEXTURES[0];
		}
	}
}