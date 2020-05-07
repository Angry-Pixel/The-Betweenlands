package thebetweenlands.client.render.entity;

import java.util.EnumMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import thebetweenlands.client.render.model.entity.rowboat.ModelLantern;
import thebetweenlands.client.render.model.entity.rowboat.ModelWeedwoodRowboat;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.entity.rowboat.Lantern;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.CubicBezier;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.Quat;

public class RenderWeedwoodRowboat extends Render<EntityWeedwoodRowboat> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/weedwood_rowboat.png");

    private static final ResourceLocation TEXTURE_TARRED = new ResourceLocation(ModInfo.ID, "textures/entity/weedwood_tarred.png");

    private static final ResourceLocation TEXTURE_TAR_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/entity/weedwood_rowboat_tar_overlay.png");

    private static final CubicBezier PULL_CURVE = new CubicBezier(1, 0, 1, 0.25F);

    private RenderPlayerRower rowerDefaultRender;

    private RenderPlayerRower rowerSlimRender;

    private ModelWeedwoodRowboat model = new ModelWeedwoodRowboat();

    private ModelLantern lanternModel = new ModelLantern();

    private int maskId = -1;

    private Matrix matrix = new Matrix();

    private EnumMap<ShipSide, Vec3d> grips = ShipSide.newEnumMap(Vec3d.class, Vec3d.ZERO, Vec3d.ZERO);

    private EnumMap<ShipSide, ArmArticulation> arms = ShipSide.newEnumMap(ArmArticulation.class, new ArmArticulation(), new ArmArticulation());

    private EnumMap<ShipSide, Float> shoulderZ = ShipSide.newEnumMap(float.class);

    private float bodyRotateAngleX;

    private float bodyRotateAngleY;

    public RenderWeedwoodRowboat(RenderManager mgr) {
        super(mgr);
        rowerDefaultRender = new RenderPlayerRower(mgr, false);
        rowerSlimRender = new RenderPlayerRower(mgr, true);
    }

    @Override
    public boolean isMultipass() {
        return true;
    }

    @Override
    public void doRender(EntityWeedwoodRowboat rowboat, double x, double y, double z, float yaw, float delta) {
        double wave = rowboat.getWaveHeight(delta);
        Lantern lantern = rowboat.getLantern();
        Vec3d anchor = Vec3d.ZERO, light = Vec3d.ZERO;
        if (lantern != null) {
            anchor = rowboat.getLocalLanternPosition(delta);
            Matrix m = new Matrix();
            m.translate(
                rowboat.lastTickPosX + (rowboat.posX - rowboat.lastTickPosX) * delta + anchor.x,
                rowboat.lastTickPosY + (rowboat.posY - rowboat.lastTickPosY) * delta + anchor.y + wave,
                rowboat.lastTickPosZ + (rowboat.posZ - rowboat.lastTickPosZ) * delta + anchor.z
            );
            m.rotate(lantern.getAngle(delta), 1, 0, 0);
            m.translate(0, -3.5 / 16, 0);
            light = m.transform(Vec3d.ZERO);
        }
        float scale = 0.6F;
        if (MinecraftForgeClient.getRenderPass() == 0) {
            model.animateOar(rowboat, ShipSide.STARBOARD, delta);
            model.animateOar(rowboat, ShipSide.PORT, delta);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + wave, z);
            GlStateManager.pushMatrix();
            roll(rowboat, yaw, delta);
            bindEntityTexture(rowboat);
            GlStateManager.scale(-1, -1, 1);
            model.render(rowboat, lantern != null, 0.0625F, delta);
            GlStateManager.popMatrix();
            if (lantern != null) {
                GlStateManager.translate(anchor.x, anchor.y, anchor.z);
                GlStateManager.rotate(-yaw, 0, 1, 0);
                GlStateManager.scale(-1, -1, 1);
                lanternModel.render(lantern, 0.0625F, delta);
                if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
                    RenderFirefly.addFireflyLight(light.x, light.y, light.z, scale * 7.0F);
                }
            }
            GlStateManager.popMatrix();
        } else if (lantern != null) {
            GlStateManager.disableLighting();
            RenderFirefly.renderFireflyGlow(light.x - renderManager.renderPosX, light.y - renderManager.renderPosY, light.z - renderManager.renderPosZ, scale);
            GlStateManager.enableLighting();
        }
    }

    @Override
    public void renderMultipass(EntityWeedwoodRowboat rowboat, double x, double y, double z, float yaw, float delta) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + rowboat.getWaveHeight(delta), z);
            roll(rowboat, yaw, delta);
            renderWaterMask();
            GlStateManager.popMatrix();
        }
    }

    private void roll(EntityWeedwoodRowboat rowboat, float yaw, float delta) {
        Quat rot = rowboat.getRotation(delta);
        GlStateManager.rotate(new Quaternion((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w));
        GlStateManager.rotate(-yaw, 0, 1, 0);
        float roll = rowboat.getRoll(delta);
        if (roll != 0) {
            GlStateManager.rotate(roll, 0, 0, 1);
        }
    }

    @SubscribeEvent
    public void onLivingRender(RenderPlayerEvent.Pre event) {
        EntityPlayer e = event.getEntityPlayer();
        Entity riding = e.getRidingEntity();
        if (riding instanceof EntityWeedwoodRowboat && riding.getControllingPassenger() == e) {
            event.setCanceled(true);
            EntityWeedwoodRowboat rowboat = (EntityWeedwoodRowboat) riding;
            float delta = event.getPartialRenderTick();
            model.animateOar(rowboat, ShipSide.STARBOARD, delta);
            model.animateOar(rowboat, ShipSide.PORT, delta);
            calculateGrip(rowboat, ShipSide.STARBOARD, delta);
            calculateGrip(rowboat, ShipSide.PORT, delta);
            articulateBody(rowboat, delta);
            float yaw = rowboat.prevRotationYaw + (rowboat.rotationYaw - rowboat.prevRotationYaw) * delta;
            articulateArm(ShipSide.STARBOARD, yaw);
            articulateArm(ShipSide.PORT, yaw);
            AbstractClientPlayer player = (AbstractClientPlayer) e;
            RenderPlayerRower render = "slim".equals(player.getSkinType()) ? rowerSlimRender : rowerDefaultRender;
            render.renderPilot(player, arms.get(ShipSide.STARBOARD), arms.get(ShipSide.PORT), bodyRotateAngleX, bodyRotateAngleY, event.getX(), event.getY(), event.getZ(), delta);
        }
    }

    private void calculateGrip(EntityWeedwoodRowboat rowboat, ShipSide side, float delta) {
        int dir = side == ShipSide.PORT ? 1 : -1;
        matrix.setIdentity();
        float yaw = (rowboat.prevRotationYaw + (rowboat.rotationYaw - rowboat.prevRotationYaw) * delta) * MathUtils.DEG_TO_RAD;
        double pelvis = 0.75;
        matrix.translate(0, pelvis - 1.5, 0);
        matrix.rotate(yaw , 0, 1, 0);
        matrix.rotate(rowboat.getRotation(delta));
        matrix.rotate(-yaw, 0, 1, 0);
        matrix.translate(0, 1.5, 0);
        matrix.scale(-1, -1, 1);
        matrix.translate(-9 / 16D * dir, pelvis + 10 / 16D, -7 / 16D);
        createOarTransformationMatrix(side);
        grips.put(side, matrix.transform(new Vec3d(0, -6 / 16D, 0)));
    }

    private void createOarTransformationMatrix(ShipSide side) {
        ModelRenderer oar = model.getOar(side);
        matrix.rotate(oar.rotateAngleZ, 0, 0, 1);
        matrix.rotate(oar.rotateAngleX, 1, 0, 0);
        matrix.rotate(oar.rotateAngleY, 0, 1, 0);
    }

    private void articulateBody(EntityWeedwoodRowboat rowboat, float delta) {
        float pow = rowboat.getPilotPower(delta);
        float leftZ = (float) grips.get(ShipSide.STARBOARD).z;
        float rightZ = (float) grips.get(ShipSide.PORT).z;
        float generalX = 0, generalY = 0, powerX = 0;
        if (pow != 0) {
            float port = rowboat.getRowProgress(ShipSide.PORT, delta);
            powerX = MathHelper.sin((port + 0.03F) * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.32F + 0.05F;
        }
        if (pow != 1) {
            float tilt = (float) Math.atan2(leftZ, rightZ) + 3 * MathUtils.PI / 4;
            generalY = tilt * 0.75F;
            float z = (leftZ + rightZ) / 2;
            float y = (float) (grips.get(ShipSide.STARBOARD).y + grips.get(ShipSide.PORT).y) / 2;
            float forward = -z * MathUtils.linearTransformf(Math.abs(leftZ + rightZ), 0, 0.05F, 1.1F, 1);
            float downward = MathHelper.clamp((-y - 0.3F) / 0.35F, 0, 1);
            float upward;
            if (downward < 0.6F) {
                upward = MathUtils.linearTransformf(downward, 0, 0.6F, 1, 0);
                upward = PULL_CURVE.eval(upward) * 0.6F;
            } else {
                upward = 0;
            }
            float lean = (forward + (downward * 0.1F)) * (1 - upward) + upward * 0.2F;
            generalX = MathUtils.linearTransformf(lean, 0.2F, 0.72F, -0.45F, 0.5F);
        }
        bodyRotateAngleX = generalX + (powerX - generalX) * pow;
        bodyRotateAngleY = generalY * (1 - pow);
        shoulderZ.put(ShipSide.STARBOARD, MathHelper.clamp((leftZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
        shoulderZ.put(ShipSide.PORT,MathHelper.clamp((rightZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
    }

    private void articulateArm(ShipSide side, float yaw) {
        int dir = side == ShipSide.PORT ? 1 : -1;
        createBodyTransformationMatrix();
        // move to shoulder joint
        matrix.translate(-6 / 16F * dir, -10 / 16F, shoulderZ.get(side));
        Vec3d arm = matrix.transform(Vec3d.ZERO);
        Vec3d grip = grips.get(side);
        float targetX = (float) (grip.x - arm.x);
        float targetY = (float) (grip.y - arm.y);
        float targetZ = (float) (grip.z - arm.z);
        float horizontalDistSq = targetX * targetX + targetZ * targetZ;
        float targetPitch = (float) Math.atan2(targetY, MathHelper.sqrt(horizontalDistSq));
        float targetLen = MathHelper.sqrt(horizontalDistSq + targetY * targetY);
        float upperArmLen = 4 / 16F, lowerArmLen = 4.25F / 16;
        float shoulderAngle = (float) Math.acos((upperArmLen * upperArmLen + targetLen * targetLen - lowerArmLen * lowerArmLen) / (2 * upperArmLen * targetLen));
        float flexionAngle = (float) Math.acos((upperArmLen * upperArmLen + lowerArmLen * lowerArmLen - targetLen * targetLen) / (2 * upperArmLen * lowerArmLen));
        ArmArticulation armArt = arms.get(side);
        // If shoulderAngle is NaN then the target is out of reach so the arm should simply point in that direction.
        armArt.shoulderAngleX = (shoulderAngle != shoulderAngle ? -MathUtils.PI / 2 - targetPitch : (shoulderAngle - MathUtils.PI / 2 - targetPitch)) - bodyRotateAngleX;
        armArt.shoulderAngleY = (float) Math.atan2(targetZ, targetX) + MathUtils.PI / 2 - bodyRotateAngleY;
        armArt.flexionAngle = shoulderAngle != shoulderAngle ? 0 : (flexionAngle - MathUtils.PI) * MathUtils.RAD_TO_DEG;
        armArt.shoulderZ = shoulderZ.get(side);
    }

    private void createBodyTransformationMatrix() {
        matrix.setIdentity();
        // player yOffset
        matrix.translate(0, -1.62, 0);
        // regular scale
        matrix.scale(-1, -1, 1);
        // player scale
        matrix.scale(15 / 16F, 15 / 16F, 15 / 16F);
        // regular translate
        matrix.translate(0, -24 / 16F - 0.0078125, 0);
        // body rotation point
        matrix.translate(0, 12 / 16F, 0);
        matrix.rotate(bodyRotateAngleY, 0, 1, 0);
        matrix.rotate(bodyRotateAngleX, 1, 0, 0);
    }

    private void renderWaterMask() {
        if (maskId == -1) {
            maskId = GLAllocation.generateDisplayLists(1);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vb = tessellator.getBuffer();
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            double y = 0.813;
            double midWidth = 0.55;
            double midDepth = 0.65;
            double endWidth = 0.4;
            double endDepth = 0.16;
            double endOffset = 0.81;
            vb.pos(-midWidth, y, midDepth).endVertex();
            vb.pos(midWidth, y, midDepth).endVertex();
            vb.pos(midWidth, y, -midDepth).endVertex();
            vb.pos(-midWidth, y, -midDepth).endVertex();
            vb.pos(-endWidth, y, endDepth - endOffset).endVertex();
            vb.pos(endWidth, y, endDepth - endOffset).endVertex();
            vb.pos(endWidth, y, -endDepth - endOffset).endVertex();
            vb.pos(-endWidth, y, -endDepth - endOffset).endVertex();
            vb.pos(-endWidth, y, endDepth + endOffset).endVertex();
            vb.pos(endWidth, y, endDepth + endOffset).endVertex();
            vb.pos(endWidth, y, -endDepth + endOffset).endVertex();
            vb.pos(-endWidth, y, -endDepth + endOffset).endVertex();
            GlStateManager.glNewList(maskId, GL11.GL_COMPILE_AND_EXECUTE);
            GlStateManager.disableTexture2D();
            GlStateManager.colorMask(false, false, false, false);
            tessellator.draw();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableTexture2D();
            GlStateManager.glEndList();
        } else {
            GlStateManager.callList(maskId);
        }
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityWeedwoodRowboat rowboat) {
        if (rowboat.isTarred()) {
            TextureManager mgr = Minecraft.getMinecraft().getTextureManager();
            //noinspection ConstantConditions
            if (mgr.getTexture(TEXTURE_TARRED) == null) {
                mgr.loadTexture(TEXTURE_TARRED, new LayeredTexture(TEXTURE.toString(), TEXTURE_TAR_OVERLAY.toString()));
            }
            return TEXTURE_TARRED;
        }
        return TEXTURE;
    }

    public static class ArmArticulation {
        public float shoulderAngleX;

        public float shoulderAngleY;

        public float flexionAngle;

        public float shoulderZ;
    }
}
