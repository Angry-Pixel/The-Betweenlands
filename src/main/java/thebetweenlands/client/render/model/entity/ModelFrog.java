package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityFrog;

@SideOnly(Side.CLIENT)
public class ModelFrog extends ModelBase {
    ModelRenderer torso;
    ModelRenderer head;
    ModelRenderer legfrontleft1;
    ModelRenderer legfrontright1;
    ModelRenderer legfrontleft2;
    ModelRenderer legfrontright2;
    ModelRenderer legbackleft1;
    ModelRenderer legbackright1;
    ModelRenderer legbackleft2;
    ModelRenderer legbackright2;

    public ModelFrog() {
        textureWidth = 64;
        textureHeight = 32;

        head = new ModelRenderer(this, 0, 10);
        head.setRotationPoint(0.0F, 19.0F, 0.0F);
        head.addBox(-1.5F, -1.0F, -3.0F, 3, 2, 3, 0.0F);
        setRotation(head, -0.07435719668865202F, 0.0F, 0.0F);
        legfrontleft2 = new ModelRenderer(this, 25, 6);
        legfrontleft2.setRotationPoint(2.0F, 20.0F, 0.0F);
        legfrontleft2.addBox(1.0F, 0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(legfrontleft2, -0.40896469354629517F, 0.0F, 0.5948578119277954F);
        torso = new ModelRenderer(this, 0, 0);
        torso.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.addBox(-2.5F, -1.0F, 0.0F, 5, 3, 6, 0.0F);
        setRotation(torso, -0.5948578119277954F, 0.0F, 0.0F);
        legbackleft1 = new ModelRenderer(this, 25, 11);
        legbackleft1.setRotationPoint(2.0F, 22.0F, 3.0F);
        legbackleft1.addBox(-0.5F, -1.5F, -0.5F, 2, 2, 3, 0.0F);
        setRotation(legbackleft1, -0.296705972839036F, -0.15707963267948966F, -0.19198621771937624F);
        legbackleft2 = new ModelRenderer(this, 25, 17);
        legbackleft2.setRotationPoint(1.0F, 0.3F, 2.0F);
        legbackleft2.addBox(-0.5F, 0.1F, -3.9F, 1, 1, 4, 0.0F);
        setRotation(legbackleft2, 0.45378560551852565F, 0.0F, 0.19198621771937624F);
        legbackright2 = new ModelRenderer(this, 36, 17);
        legbackright2.setRotationPoint(-1.0F, 0.3F, 2.0F);
        legbackright2.addBox(-0.5F, 0.1F, -3.9F, 1, 1, 4, 0.0F);
        setRotation(legbackright2, 0.45378560551852565F, 0.0F, -0.19198621771937624F);
        legbackright1 = new ModelRenderer(this, 36, 11);
        legbackright1.setRotationPoint(-2.0F, 22.0F, 3.0F);
        legbackright1.addBox(-1.5F, -1.5F, -0.5F, 2, 2, 3, 0.0F);
        setRotation(legbackright1, -0.296705972839036F, 0.15707963267948966F, 0.19198621771937624F);
        legfrontright2 = new ModelRenderer(this, 30, 6);
        legfrontright2.setRotationPoint(-2.0F, 20.0F, 0.0F);
        legfrontright2.addBox(-2.0F, 0.5F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(legfrontright2, -0.40896469354629517F, 0.0F, -0.5948606133460999F);
        legfrontright1 = new ModelRenderer(this, 32, 0);
        legfrontright1.setRotationPoint(-2.0F, 20.0F, 0.0F);
        legfrontright1.addBox(-1.0F, -1.0F, -0.5F, 1, 3, 2, 0.0F);
        setRotation(legfrontright1, -0.0743509978055954F, -0.18589499592781067F, 0.37178999185562134F);
        legfrontleft1 = new ModelRenderer(this, 25, 0);
        legfrontleft1.setRotationPoint(2.0F, 20.0F, 0.0F);
        legfrontleft1.addBox(0.0F, -1.0F, -0.5F, 1, 3, 2, 0.0F);
        setRotation(legfrontleft1, -0.07435719668865202F, 0.18589310348033902F, -0.3717860877513886F);
        legbackleft1.addChild(legbackleft2);
        legbackright1.addChild(legbackright2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        torso.render(unitPixel);
        head.render(unitPixel);
        legfrontleft1.render(unitPixel);
        legfrontright1.render(unitPixel);
        legfrontleft2.render(unitPixel);
        legfrontright2.render(unitPixel);
        legbackleft1.render(unitPixel);
        legbackright1.render(unitPixel);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        head.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
        head.rotateAngleX = rotationPitch / (180F / (float) Math.PI) - 0.07435719668865202F + (float) Math.sin(entityTickTime / 8.0F) / 15.0F;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        EntityFrog frog = (EntityFrog) entity;
        float leapProgress = frog.prevJumpAnimationTicks + (frog.jumpAnimationTicks - frog.prevJumpAnimationTicks) * partialRenderTicks;

        //Reset legs to default
        legbackleft1.rotateAngleX = -0.296705972839036F;
        legbackright1.rotateAngleX = -0.296705972839036F;
        legbackleft2.rotateAngleX = 0.45378560551852565F;
        legbackright2.rotateAngleX = 0.45378560551852565F;
        legfrontleft1.rotateAngleZ = -0.3717860877513886F;
        legfrontright1.rotateAngleZ = 0.37178999185562134F;
        legfrontleft2.rotateAngleZ = 0.5948578119277954F;
        legfrontright2.rotateAngleZ = -0.5948606133460999F;
        
        if (!frog.isInWater()) {
            //Idle animation
            this.torso.rotateAngleX = -0.55F - (float) (Math.sin((entity.ticksExisted + partialRenderTicks) / 10.0F) + 1) / 35.0F;
            this.torso.rotationPointY = 19 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 15.0F;

            this.legbackleft1.rotationPointY = 22;
            this.legbackright1.rotationPointY = 22;

            this.head.rotationPointY = 19;
            this.legfrontleft1.rotationPointY = 20;
            this.legfrontleft2.rotationPointY = 20;
            this.legfrontright1.rotationPointY = 20;
            this.legfrontright2.rotationPointY = 20;

            this.legbackleft2.rotateAngleX = 0.5F;
            this.legbackright2.rotateAngleX = 0.5F;

            if (frog.onGround) {
                legbackleft1.rotateAngleX = -0.296705972839036F;
                legbackright1.rotateAngleX = -0.296705972839036F;
                legbackleft2.rotateAngleX = 0.45378560551852565F;
                legbackright2.rotateAngleX = 0.45378560551852565F;

                legfrontleft1.rotateAngleZ = -0.3717860877513886F;
                legfrontright1.rotateAngleZ = 0.37178999185562134F;
                legfrontleft2.rotateAngleZ = 0.5948578119277954F;
                legfrontright2.rotateAngleZ = -0.5948606133460999F;
            }
        } else {
            //Idle animation
            this.torso.rotateAngleX = -0.1F - (float) (Math.sin((entity.ticksExisted + partialRenderTicks) / 10.0F) + 1) / 35.0F;

            //Water bobbing animation
            this.torso.rotationPointY = 19 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legbackleft1.rotationPointY = 21 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legbackright1.rotationPointY = 21 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.head.rotationPointY = 19 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legfrontleft1.rotationPointY = 20 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legfrontleft2.rotationPointY = 20 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legfrontright1.rotationPointY = 20 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;
            this.legfrontright2.rotationPointY = 20 + (float) Math.sin((entity.ticksExisted + partialRenderTicks) / 8.0F) / 2.0F;

            this.legbackleft2.rotateAngleX = 0.5F + 0.6F;
            this.legbackright2.rotateAngleX = 0.5F + 0.6F;
        }

        if (!frog.onGround || frog.jumpAnimationTicks > 0) {
            if (frog.jumpAnimationTicks > 0 && frog.jumpAnimationTicks <= 7) {
                legbackleft1.rotateAngleX = -0.296705972839036F + 0.15F * leapProgress;
                legbackright1.rotateAngleX = -0.296705972839036F + 0.15F * leapProgress;
                legbackleft2.rotateAngleX = 0.45378560551852565F + 0.2F * leapProgress;
                legbackright2.rotateAngleX = 0.45378560551852565F + 0.2F * leapProgress;

                legfrontleft1.rotateAngleZ = -0.3717860877513886F - 0.15F * leapProgress;
                legfrontright1.rotateAngleZ = 0.37178999185562134F + 0.15F * leapProgress;
                legfrontleft2.rotateAngleZ = 0.5948578119277954F - 0.15F * leapProgress;
                legfrontright2.rotateAngleZ = -0.5948606133460999F + 0.15F * leapProgress;
            }
            if (frog.jumpAnimationTicks > 7 && frog.jumpAnimationTicks <= 14) {
                legbackleft1.rotateAngleX = -0.296705972839036F + 1.05F - 0.075F * leapProgress;
                legbackright1.rotateAngleX = -0.296705972839036F + 1.05F - 0.075F * leapProgress;
                legbackleft2.rotateAngleX = 0.45378560551852565F + 1.4F - 0.1F * leapProgress;
                legbackright2.rotateAngleX = 0.45378560551852565F + 1.4F - 0.1F * leapProgress;

                legfrontleft1.rotateAngleZ = -0.3717860877513886F - 1.05F + 0.075F * leapProgress;
                legfrontright1.rotateAngleZ = 0.37178999185562134F + 1.05F - 0.075F * leapProgress;
                legfrontleft2.rotateAngleZ = 0.5948578119277954F - 1.05F + 0.075F * leapProgress;
                legfrontright2.rotateAngleZ = -0.5948606133460999F + 1.05F - 0.075F * leapProgress;
            }
        }
    }
}
