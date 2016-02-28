package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelBerserkerGuardian;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;
import thebetweenlands.gemcircle.EntityGem;
import thebetweenlands.gemcircle.CircleGem;

@SideOnly(Side.CLIENT)
public class RenderBerserkerGuardian extends RenderLiving {
	private static final ResourceLocation activeTextureR = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardianR.png");
	private static final ResourceLocation activeTextureG = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardianG.png");
	private static final ResourceLocation activeTextureB = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardianB.png");
	private static final ResourceLocation activeTexture = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardianActive.png");
	private static final ResourceLocation inactiveTexture = new ResourceLocation("thebetweenlands:textures/entity/berserkerGuardian.png");

	public RenderBerserkerGuardian() {
		super(new ModelBerserkerGuardian(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if(((EntityBerserkerGuardian)entity).getActive()) {
			EntityGem gem = CircleGem.getGem(entity, 0);
			if(gem != null) {
				switch(gem.getGem()) {
				case CRIMSON:
					return activeTextureR;
				case GREEN:
					return activeTextureG;
				case AQUA:
					return activeTextureB;
				default:
					return activeTexture;
				}
			}
		}
		return inactiveTexture;
	}
}