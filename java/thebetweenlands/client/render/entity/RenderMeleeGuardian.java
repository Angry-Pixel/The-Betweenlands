package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelMeleeGuardian;
import thebetweenlands.entities.mobs.EntityMeleeGuardian;

@SideOnly(Side.CLIENT)
public class RenderMeleeGuardian extends RenderLiving {
	private static final ResourceLocation activeTexture = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianActive.png");
	private static final ResourceLocation inactiveTexture = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardian.png");

	public RenderMeleeGuardian() {
		 super(new ModelMeleeGuardian(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityMeleeGuardian)entity).getActive() ? activeTexture : inactiveTexture;
	}
}