package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelMeleeGuardian;

@SideOnly(Side.CLIENT)
public class RenderTempleGuardian extends RenderLiving {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianNA.png");

	public RenderTempleGuardian() {
		super(new ModelMeleeGuardian(), 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}