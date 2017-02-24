package thebetweenlands.client.render.model.entity.rowboat;

import java.util.EnumMap;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.entity.ModelBipedLimb;
import thebetweenlands.client.render.model.entity.ModelBoxCustomizable;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.util.MathUtils;

public class ModelBipedRower extends ModelBiped {
    private EnumMap<ShipSide, ModelBipedLimb> arms;

    private ModelRenderer leftForearm, rightForearm;

    public ModelBipedRower(float expand) {
        this(expand, 64, 32);
    }

    public ModelBipedRower(float expand, int textureWidth, int textureHeight) {
        super(expand, 0, textureWidth, textureHeight);
        boxList.remove(bipedBody.cubeList.remove(0));
        bipedBody = new AdvancedModelRenderer(this, 16, 16);
        bipedBody.setRotationPoint(0, 12, 0);
        bipedBody.addBox(-4, -12, -2, 8, 12, 4, expand);
        bipedHead.setRotationPoint(0, -12, 0);
        boxList.remove(bipedHead.cubeList.remove(0));
        // additional expand to prevent head z-fighting with body
        bipedHead.addBox(-4, -8, -4, 8, 8, 8, expand + 0.025F);
        bipedBody.addChild(bipedHead);
        bipedHeadwear.setRotationPoint(0, 0, 0);
        boxList.remove(bipedHeadwear.cubeList.remove(0));
        bipedHeadwear.addBox(-4, -8, -4, 8, 8, 8, expand + 0.525F);
        bipedHead.addChild(bipedHeadwear);
        bipedLeftArm.rotationPointY = -10;
        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY;
        if (expand == 0) {
            arms = ShipSide.newEnumMap(ModelBipedLimb.class);
            ModelBipedLimb left = createReplacementLimb(bipedLeftArm, 40, 16);
            bipedLeftArm = left;
            arms.put(ShipSide.STARBOARD, left);
            ModelBipedLimb right = createReplacementLimb(bipedRightArm, 32, 48);
            bipedRightArm = right;
            arms.put(ShipSide.PORT, right);
        } else {
            bipedLeftArm = createExpandReplacementLimb(bipedLeftArm, 40, 16, expand);
            bipedRightArm = createExpandReplacementLimb(bipedRightArm, 40, 16, expand);
        }
        bipedRightLeg.rotateAngleX = -MathUtils.TAU / 5;
        bipedRightLeg.rotateAngleY = MathUtils.PI / 10;
        bipedRightLeg.rotationPointZ = 0.1F;
        bipedRightLeg.rotationPointY = 12;
        bipedLeftLeg.rotateAngleX = -MathUtils.TAU / 5;
        bipedLeftLeg.rotateAngleY = -MathUtils.PI / 10;
        bipedLeftLeg.rotationPointZ = 0.1F;
        bipedLeftLeg.rotationPointY = 12;
    }

    private ModelBipedLimb createReplacementLimb(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY) {
        ModelBipedLimb limb = new ModelBipedLimb(this, textureOffsetX, textureOffsetY);
        limb.setRotationPoint(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
        boxList.remove(oldLimb.cubeList.remove(0));
        limb.offsetX = -2;
        limb.offsetY = -2;
        limb.offsetZ = -2;
        bipedBody.addChild(limb);
        return limb;
    }

    private ModelRenderer createExpandReplacementLimb(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, float expand) {
        ModelRenderer limb = new ModelRenderer(this, textureOffsetX, textureOffsetY);
        bipedBody.addChild(limb);
        limb.mirror = oldLimb.mirror;
        ModelBox box = (ModelBox) oldLimb.cubeList.remove(0);
        boxList.remove(box);
        ModelBoxCustomizable arm = new ModelBoxCustomizable(limb, textureOffsetX, textureOffsetY, -2, -2, -2, 4, 6, 4, expand);
        arm.setVisibleSides(~ModelBoxCustomizable.SIDE_BOTTOM);
        limb.cubeList.add(arm);
        limb.setRotationPoint(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
        ModelRenderer lowerLimb = new ModelRenderer(this, textureOffsetX, textureOffsetY - 6);
        if (bipedLeftArm == oldLimb) {
            leftForearm = lowerLimb;
        } else {
            rightForearm = lowerLimb;
        }
        lowerLimb.mirror = oldLimb.mirror;
        lowerLimb.setRotationPoint(-2 + 2, box.posY1 + 6, box.posZ1 + 2);
        ModelBoxCustomizable forearm = new ModelBoxCustomizable(lowerLimb, textureOffsetX, textureOffsetY + 6, -2, 0, -2, 4, 6, 4, expand * 0.75F, -6);
        forearm.setVisibleSides(~ModelBoxCustomizable.SIDE_TOP);
        lowerLimb.cubeList.add(forearm);
        limb.addChild(lowerLimb);
        return limb;
    }

    public ModelRenderer getArm(ShipSide side) {
        return arms.get(side);
    }

    public void setLeftArmFlexionAngle(float flexionAngle) {
        if (arms == null) {
            leftForearm.rotateAngleX = flexionAngle * MathUtils.DEG_TO_RAD;
        } else {
            arms.get(ShipSide.STARBOARD).setFlexionAngle(flexionAngle);
        }
    }

    public void setRightArmFlexionAngle(float flexionAngle) {
        if (arms == null) {
            rightForearm.rotateAngleX = flexionAngle * MathUtils.DEG_TO_RAD;
        } else {
            arms.get(ShipSide.PORT).setFlexionAngle(flexionAngle);
        }
    }

    @Override
    public void render(Entity entity, float swing, float speed, float ticksExisted, float yaw, float pitch, float scale) {
        setRotationAngles(speed, swing, ticksExisted, yaw, pitch, scale, entity);
        bipedBody.render(scale);
        bipedRightLeg.render(scale);
        bipedLeftLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float speed, float swing, float ticksExisted, float yaw, float pitch, float scale, Entity entity) {}
}