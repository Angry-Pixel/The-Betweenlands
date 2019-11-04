package thebetweenlands.client.render.model.entity.rowboat;

import java.util.EnumMap;
import java.util.Iterator;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.entity.ModelBoxCustomizable;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.util.MathUtils;

public class ModelBipedRower extends ModelBiped {
    private EnumMap<ShipSide, ModelBipedLimb> arms;

    public ModelRenderer leftForearm, rightForearm;

    public ModelBipedRower(float expand) {
        this(expand, false);
    }

    public ModelBipedRower(float expand, boolean slimArms) {
        this(expand, false, slimArms, 64, 32, new BipedTextureUVs(40, 16, 40, 16, 0, 16, 0, 16));
    }

    public ModelBipedRower(float expand, boolean expandJointed, boolean slimArms, int textureWidth, int textureHeight, BipedTextureUVs uvs) {
        super(expand, 0, textureWidth, textureHeight);
        removeCuboids(bipedBody);
        removeCuboids(bipedHead);
        removeCuboids(bipedHeadwear);
        removeCuboids(bipedLeftLeg);
        removeCuboids(bipedRightLeg);
        bipedBody = new AdvancedModelRenderer(this, uvs.bodyU, uvs.bodyV);
        bipedBody.setRotationPoint(0, 12, 0);
        bipedBody.addBox(-4, -12, -2, 8, 12, 4, expand);
        bipedHead.setRotationPoint(0, -12, 0);
        // additional expand to prevent head z-fighting with body
        bipedHead.addBox(-4, -8, -4, 8, 8, 8, expand + 0.025F);
        bipedBody.addChild(bipedHead);
        bipedHeadwear.setRotationPoint(0, 0, 0);
        bipedHeadwear.addBox(-4, -8, -4, 8, 8, 8, expand + 0.5F);
        bipedHead.addChild(bipedHeadwear);
        bipedLeftArm.rotationPointY = -10;
        bipedRightArm.rotationPointY = bipedLeftArm.rotationPointY;
        if (expand == 0 || expandJointed) {
            arms = ShipSide.newEnumMap(ModelBipedLimb.class);
            ModelBipedLimb left = createReplacementArm(bipedLeftArm, uvs.armLeftU, uvs.armLeftV, slimArms, expand);
            bipedLeftArm = left;
            arms.put(ShipSide.STARBOARD, left);
            ModelBipedLimb right = createReplacementArm(bipedRightArm, uvs.armRightU, uvs.armRightV, slimArms, expand);
            bipedRightArm = right;
            arms.put(ShipSide.PORT, right);
        } else {
            bipedLeftArm = createExpandReplacementArm(bipedLeftArm, uvs.armLeftU, uvs.armLeftV, slimArms, expand);
            bipedRightArm = createExpandReplacementArm(bipedRightArm, uvs.armRightU, uvs.armRightV, slimArms, expand);
        }
        if (slimArms) {
            bipedRightArm.rotationPointX++;
        }
        bipedLeftLeg = new ModelRenderer(this, uvs.legLeftU, uvs.legLeftV);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-2, 0, -2, 4, 12, 4, expand);
        bipedLeftLeg.setRotationPoint(1.9F, 12, 0);
        bipedLeftLeg.rotateAngleX = -1.25F;
        bipedLeftLeg.rotateAngleY = -0.314F;
        bipedRightLeg = new ModelRenderer(this, uvs.legRightU, uvs.legRightV);
        bipedRightLeg.addBox(-2, 0, -2, 4, 12, 4, expand);
        bipedRightLeg.setRotationPoint(-1.9F, 12, 0);
        bipedRightLeg.rotateAngleX = -1.25F;
        bipedRightLeg.rotateAngleY = 0.314F;
    }

    private void removeCuboids(ModelRenderer renderer) {
        renderer.cubeList.clear();
        boxList.remove(renderer);
    }

    private ModelBipedLimb createReplacementArm(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, boolean slimArms, float expand) {
        ModelBipedLimb limb = new ModelBipedLimb(this, textureOffsetX, textureOffsetY, slimArms ? 3 : 4, 4, expand);
        limb.setRotationPoint(Math.signum(oldLimb.rotationPointX) * 6, oldLimb.rotationPointY, oldLimb.rotationPointZ);
        removeCuboids(oldLimb);
        limb.offsetX = -2;
        limb.offsetY = -2;
        limb.offsetZ = -2;
        bipedBody.addChild(limb);
        return limb;
    }

    private ModelRenderer createExpandReplacementArm(ModelRenderer oldLimb, int textureOffsetX, int textureOffsetY, boolean slimArms, float expand) {
        ModelRenderer limb = new ModelRenderer(this, textureOffsetX, textureOffsetY);
        bipedBody.addChild(limb);
        limb.mirror = oldLimb.mirror;
        ModelBox box = oldLimb.cubeList.get(0);
        removeCuboids(oldLimb);
        ModelBoxCustomizable arm = new ModelBoxCustomizable(limb, textureOffsetX, textureOffsetY, -2, -2, -2, slimArms ? 3 : 4, 6, 4, expand);
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
        ModelBoxCustomizable forearm = new ModelBoxCustomizable(lowerLimb, textureOffsetX, textureOffsetY + 6, -2, 0, -2, slimArms ? 3 : 4, 6, 4, expand * 0.75F, -6);
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
    public void render(Entity entity, float swing, float speed, float age, float yaw, float pitch, float scale) {
        bipedBody.render(scale);
        bipedRightLeg.render(scale);
        bipedLeftLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float swing, float speed, float age, float yaw, float pitch, float scale, Entity entity) {}

    public static class BipedTextureUVs {
        int armLeftU, armLeftV;
        int armRightU, armRightV;
        int legLeftU, legLeftV;
        int legRightU, legRightV;
        int bodyU, bodyV;

        public BipedTextureUVs(int armLeftU, int armLeftV, int armRightU, int armRightV, int legLeftU, int legLeftV, int legRightU, int legRightV) {
            this(armLeftU, armLeftV, armRightU, armRightV, legLeftU, legLeftV, legRightU, legRightV, 16, 16);
        }

        public BipedTextureUVs(int armLeftU, int armLeftV, int armRightU, int armRightV, int legLeftU, int legLeftV, int legRightU, int legRightV, int bodyU, int bodyV) {
            this.armLeftU = armLeftU;
            this.armLeftV = armLeftV;
            this.armRightU = armRightU;
            this.armRightV = armRightV;
            this.legLeftU = legLeftU;
            this.legLeftV = legLeftV;
            this.legRightU = legRightU;
            this.legRightV = legRightV;
            this.bodyU = bodyU;
            this.bodyV = bodyV;
        }
    }
}