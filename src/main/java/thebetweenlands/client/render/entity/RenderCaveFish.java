package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelCaveFish;
import thebetweenlands.common.entity.mobs.EntityCaveFish;

@SideOnly(Side.CLIENT)
public class RenderCaveFish extends RenderLiving<EntityCaveFish> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/cave_fish.png");
    public final static ModelCaveFish CAVE_FISH_MODEL = new ModelCaveFish();

    public RenderCaveFish(RenderManager rendermanagerIn) {
        super(rendermanagerIn, CAVE_FISH_MODEL, 0.5f);
    }

	@Override
	public void doRender(EntityCaveFish cave_fish, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(cave_fish, x, y, z, entityYaw, partialTicks);
		float smoothedYaw = cave_fish.prevRotationYaw + (cave_fish.rotationYaw - cave_fish.prevRotationYaw) * partialTicks;
		float smoothedPitch = cave_fish.prevRotationPitch + (cave_fish.rotationPitch - cave_fish.prevRotationPitch) * partialTicks;
		GlStateManager.pushMatrix();
		float scale = cave_fish.isLeader() ? 1F : 0.5F;
		GlStateManager.translate(x, y + scale, z);
		GlStateManager.scale(scale, -scale, -scale);
		GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
		GlStateManager.rotate(smoothedPitch, 1F, 0F, 0F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		CAVE_FISH_MODEL.render(0.0625F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		CAVE_FISH_MODEL.setLivingAnimations(cave_fish, cave_fish.limbSwing, cave_fish.limbSwingAmount, partialTicks);
	}

    @Override
    protected ResourceLocation getEntityTexture(EntityCaveFish entity) {
        return TEXTURE;
    }
}
