package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelChiromawEgg;
import thebetweenlands.client.render.model.entity.ModelChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderChiromawHatchling extends RenderLiving<EntityChiromawHatchling> {
	private static final ResourceLocation TEXTURE_HATCHLING = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_hatchling.png");
	private static final ResourceLocation TEXTURE_HATCHLING_BLINK_1 = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_hatchling_blink_1.png");
	private static final ResourceLocation TEXTURE_HATCHLING_BLINK_2 = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_hatchling_blink_2.png");
	private static final ResourceLocation TEXTURE_EGG = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_egg.png");
	private static final ModelChiromawHatchling MODEL_HATCHLING = new ModelChiromawHatchling();
	private static final ModelChiromawEgg MODEL_EGG = new ModelChiromawEgg();

	public RenderChiromawHatchling(RenderManager renderManager) {
		super(renderManager, MODEL_HATCHLING, 0.2F);
	}

	@Override
	public void doRender(EntityChiromawHatchling entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		if(entity.getIsHungry() && entity.getRiseCount() > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			
        	float size = MathHelper.sin((entity.ticksExisted + partialTicks) * 0.125F) * 0.0625F;
        	float smoothRise = entity.prevRise + (entity.getRiseCount() - entity.prevRise) * partialTicks;
        	renderFoodCraved(entity.getFoodCraved(), 0, 0.5f + smoothRise * 0.025F, 0, (0.25F + size) * smoothRise / EntityChiromawHatchling.MAX_RISE);
        	
        	GlStateManager.popMatrix();
        }
	}
	
	@Override
	protected void applyRotations(EntityChiromawHatchling entity, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entity, ageInTicks, entity.rotationYaw, partialTicks);
	}
	
	@Override
	protected void renderModel(EntityChiromawHatchling entity, float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean isVisible = this.isVisible(entity);
        boolean isTransparent = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

        if (isVisible || isTransparent) {
            if (!this.bindEntityTexture(entity)) {
                return;
            }

            if (isTransparent) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            float partialTicks = ageInTicks - entity.ticksExisted;
    		
    		if (entity.getHasHatched()) {
    			float eggFade = entity.getTransformCount() + (entity.prevTransformTick - entity.getTransformCount()) * partialTicks;
    			
    			GlStateManager.color(1F, 1F, 1F, 1F - eggFade * 0.02F);
    			MODEL_HATCHLING.renderEgg(entity, partialTicks, 0.0625F);
    			
    			GlStateManager.color(1F, 1F, 1F, 1F);
    			MODEL_HATCHLING.renderBaby(entity, partialTicks, 0.0625F);
    		} else {
    			MODEL_EGG.renderEgg(entity, partialTicks, 0.0625F);
    		}
            
            if (isTransparent) {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
	}

	private void renderFoodCraved(ItemStack foodCraved, double x, double y, double z, float scale) {
		if (!foodCraved.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			Minecraft.getMinecraft().getRenderItem().renderItem(foodCraved, TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawHatchling entity) {
		if(entity.getHasHatched()) {
			if(entity.blinkCount <= 10 && entity.blinkCount > 8 || entity.blinkCount <= 4 && entity.blinkCount > 2)
				return TEXTURE_HATCHLING_BLINK_1;
			if(entity.blinkCount <= 8 && entity.blinkCount > 6 || entity.blinkCount <= 2 && entity.blinkCount > 0)
				return TEXTURE_HATCHLING_BLINK_2;
			return TEXTURE_HATCHLING;
		}
		return TEXTURE_EGG;
	}
}
