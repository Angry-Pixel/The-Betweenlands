package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelBerserkerGuardian;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;

@SideOnly(Side.CLIENT)
public class RenderBerserkerGuardian extends RenderLiving {
	private static final ResourceLocation activeTexture = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardianActive.png");
	private static final ResourceLocation inactiveTexture = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardian.png");

	public RenderBerserkerGuardian() {
		super(new ModelBerserkerGuardian(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityBerserkerGuardian)entity).getActive() ? activeTexture : inactiveTexture;
	}
}