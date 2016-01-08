package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelMeleeGuardian;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;

@SideOnly(Side.CLIENT)
public class RenderMeleeGuardian extends RenderLiving {
	private static final ResourceLocation activeTexture = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianActive.png");
	private static final ResourceLocation activeTextureR = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianR.png");
	private static final ResourceLocation activeTextureG = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianG.png");
	private static final ResourceLocation activeTextureB = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardianB.png");
	private static final ResourceLocation inactiveTexture = new ResourceLocation("thebetweenlands:textures/entity/meleeGuardian.png");

	public RenderMeleeGuardian() {
		super(new ModelMeleeGuardian(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if(((EntityBerserkerGuardian)entity).getActive()) {
			CircleGem gem = GemCircleHelper.getGem(entity);
			switch(gem) {
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
		return inactiveTexture;
	}
}