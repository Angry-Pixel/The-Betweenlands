package thebetweenlands.client.render.entity;

import java.util.EnumMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import thebetweenlands.client.render.model.entity.rowboat.ModelWeedwoodRowboat;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.CubicBezier;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.Quat;

public class RenderWeedwoodRowboat extends Render<EntityWeedwoodRowboat> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/entity/weedwood_rowboat.png");

    private static final CubicBezier PULL_CURVE = new CubicBezier(1, 0, 1, 0.25F);

    private RenderPlayerRower rowerDefaultRender;

    private RenderPlayerRower rowerSlimRender;

    private ModelWeedwoodRowboat model = new ModelWeedwoodRowboat();

    private int maskId = -1;

    private Matrix matrix = new Matrix();

    private EnumMap<ShipSide, Vec3d> grips = ShipSide.newEnumMap(Vec3d.class, Vec3d.ZERO, Vec3d.ZERO);

    private EnumMap<ShipSide, ArmArticulation> arms = ShipSide.newEnumMap(ArmArticulation.class, new ArmArticulation(), new ArmArticulation());

    private EnumMap<ShipSide, Float> shoulderZ = ShipSide.newEnumMap(float.class);

    private Vec3d arm = Vec3d.ZERO;

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
        model.animateOar(rowboat, ShipSide.STARBOARD, delta);
        model.animateOar(rowboat, ShipSide.PORT, delta);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 1.5F + rowboat.getWaveHeight(delta), z);
        roll(rowboat, yaw, delta);
        bindEntityTexture(rowboat);
        GlStateManager.scale(-1, -1, 1);
        model.render(rowboat, 0.0625F, delta);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderMultipass(EntityWeedwoodRowboat rowboat, double x, double y, double z, float yaw, float delta) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 1.5F + rowboat.getWaveHeight(delta), z);
        roll(rowboat, yaw, delta);
        renderWaterMask();
        GlStateManager.popMatrix();

        /* proof of concept
        double bpx = rowboat.prevPosX + (rowboat.posX - rowboat.prevPosX) * delta;
        double bpy = rowboat.prevPosY + (rowboat.posY - rowboat.prevPosY) * delta;
        double bpz = rowboat.prevPosZ + (rowboat.posZ - rowboat.prevPosZ) * delta;
        int bx = MathHelper.floor_double(bpx);
        int by = MathHelper.floor_double(bpy);
        int bz = MathHelper.floor_double(bpz);
        OpenSimplexNoise waveRng = new OpenSimplexNoise(6354);
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite sprite = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        float fracX = MathHelper.positiveModulo((float) bpx, 1);
        float fracZ = MathHelper.positiveModulo((float) bpz, 1);
        GlStateManager.translate(x - fracX, y - MathHelper.positiveModulo((float) bpy, 1) + 14.22 / 16, z - fracZ);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        float roughness = 0.15F, scale = 0.5F;
        double t = (rowboat.worldObj.getTotalWorldTime() + delta) * 0.03;
        int range = 4;
        // scale up waves to normal level from number of ticks the rowboat has existed
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                float d0 = scale(dx, dz, fracX, fracZ, range);
                float d1 = scale(dx, dz + 1, fracX, fracZ, range);
                float d2 = scale(dx + 1, dz, fracX, fracZ, range);
                float d3 = scale(dx + 1, dz + 1, fracX, fracZ, range);
                float sy0 = (float) waveRng.eval((bx + dx) * scale, t, (bz + dz) * scale) * roughness * d0;
                float sy1 = (float) waveRng.eval((bx + dx) * scale, t, (bz + dz + 1) * scale) * roughness * d1;
                float sy2 = (float) waveRng.eval((bx + dx + 1) * scale, t, (bz + dz) * scale) * roughness * d2;
                float sy3 = (float) waveRng.eval((bx + dx + 1) * scale, t, (bz + dz + 1) * scale) * roughness * d3;
                float b0 = MathUtils.linearTransformf(sy0, -roughness, roughness, 0, 1);
                float b1 = MathUtils.linearTransformf(sy1, -roughness, roughness, 0, 1);
                float b2 = MathUtils.linearTransformf(sy2, -roughness, roughness, 0, 1);
                float b3 = MathUtils.linearTransformf(sy3, -roughness, roughness, 0, 1);
                float u0 = sprite.getInterpolatedU(0);
                float u1 = sprite.getInterpolatedU(16);
                float v0 = sprite.getInterpolatedV(0);
                float v1 = sprite.getInterpolatedV(16);
                Vec3d s0 = new Vec3d(0, sy0, 0);
                Vec3d s1 = new Vec3d(0, sy1, 1);
                Vec3d s2 = new Vec3d(1, sy2, 0);
                Vec3d s3 = new Vec3d(1, sy3, 1);
                Vec3d normal0 = s3.subtract(s1).crossProduct(s0.subtract(s1)).normalize();
                GlStateManager.glNormal3f((float) normal0.xCoord, (float) normal0.yCoord, (float) normal0.zCoord);
                GlStateManager.glTexCoord2f(u0, v0);
                GlStateManager.glVertex3f(dx, sy0, dz);
                GlStateManager.glTexCoord2f(u0, v1);
                GlStateManager.glVertex3f(dx, sy1, dz + 1);
                GlStateManager.glTexCoord2f(u1, v1);
                GlStateManager.glVertex3f(dx + 1, sy3, dz + 1);
                Vec3d normal1 = s3.subtract(s1).crossProduct(s0.subtract(s1)).normalize();
                GlStateManager.glNormal3f((float) normal1.xCoord, (float) normal1.yCoord, (float) normal1.zCoord);
                GlStateManager.glTexCoord2f(u0, v0);
                GlStateManager.glVertex3f(dx, sy0, dz);
                GlStateManager.glTexCoord2f(u1, v1);
                GlStateManager.glVertex3f(dx + 1, sy3, dz + 1);
                GlStateManager.glTexCoord2f(u1, v0);
                GlStateManager.glVertex3f(dx + 1, sy2, dz);
            }
        }
        GlStateManager.glEnd();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(-range, 0, -range);
        GlStateManager.glVertex3f(-range, 0, range + 1);
        GlStateManager.glVertex3f(range + 1, 0, range + 1);
        GlStateManager.glVertex3f(range + 1, 0, -range);
        GlStateManager.glEnd();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();//*/
    }

    private static float scale(int x, int z, float originX, float originZ, int range) {
        float dx = x - originX;
        float dz = z - originZ;
        float dist = MathHelper.sqrt_float(dx * dx + dz * dz);
        if (dist > range) {
            return 0;
        }
        dist /= range;
        return MathHelper.sqrt_double(1 - dist * dist);
    }

    private void roll(EntityWeedwoodRowboat rowboat, float yaw, float delta) {
        Quat rot = rowboat.getRotation(delta);
        GlStateManager.translate(0, -1.5F, 0);
        GlStateManager.rotate(new Quaternion((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w));
        GlStateManager.translate(0, 1.5F, 0);
        GlStateManager.rotate(-yaw, 0, 1, 0);
        float timeSinceHit = rowboat.getTimeSinceHit() - delta;
        float damageTaken = rowboat.getDamageTaken() - delta;
        if (damageTaken < 0) {
            damageTaken = 0;
        }
        if (timeSinceHit > 0) {
            GlStateManager.translate(0, -1, 0);
            GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * rowboat.getForwardDirection(), 0, 0, 1);
            GlStateManager.translate(0, 1, 0);
        }
    }

    @SubscribeEvent
    public void onLivingRender(RenderLivingEvent.Pre<EntityPlayer> event) {
        Entity e = event.getEntity();
        if (!(e instanceof AbstractClientPlayer)) {
            return;
        }
        Entity riding = e.getRidingEntity();
        if (riding instanceof EntityWeedwoodRowboat && riding.getControllingPassenger() == e && !(((Render<?>) event.getRenderer()) instanceof RenderPlayerRower)) {
            event.setCanceled(true);
            EntityWeedwoodRowboat rowboat = (EntityWeedwoodRowboat) riding;
            float delta = Minecraft.getMinecraft().getRenderPartialTicks();
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
        float leftZ = (float) grips.get(ShipSide.STARBOARD).zCoord;
        float rightZ = (float) grips.get(ShipSide.PORT).zCoord;
        float generalX = 0, generalY = 0, powerX = 0;
        if (pow != 0) {
            float port = rowboat.getRowProgress(ShipSide.PORT, delta);
            powerX = MathHelper.sin((port + 0.03F) * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.32F + 0.05F;
        }
        if (pow != 1) {
            float tilt = (float) Math.atan2(leftZ, rightZ) + 3 * MathUtils.PI / 4;
            generalY = tilt * 0.75F;
            float z = (leftZ + rightZ) / 2;
            float y = (float) (grips.get(ShipSide.STARBOARD).yCoord + grips.get(ShipSide.PORT).yCoord) / 2;
            float forward = -z * MathUtils.linearTransformf(Math.abs(leftZ + rightZ), 0, 0.05F, 1.1F, 1);
            float downward = MathHelper.clamp_float((-y - 0.3F) / 0.35F, 0, 1);
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
        shoulderZ.put(ShipSide.STARBOARD, MathHelper.clamp_float((leftZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
        shoulderZ.put(ShipSide.PORT,MathHelper.clamp_float((rightZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
    }

    private void articulateArm(ShipSide side, float yaw) {
        int dir = side == ShipSide.PORT ? 1 : -1;
        // the player is scaled by this amount
        final float ps = 0.9375F;
        createBodyTransformationMatrix();
        // move to shoulder joint
        matrix.translate(-6 / 16F * dir, -10 / 16F, shoulderZ.get(side));
        arm = matrix.transform(Vec3d.ZERO);
        Vec3d grip = grips.get(side);
        float targetX = (float) (grip.xCoord - arm.xCoord);
        float targetY = (float) (grip.yCoord - arm.yCoord);
        float targetZ = (float) (grip.zCoord - arm.zCoord);
        float horizontalDistSq = targetX * targetX + targetZ * targetZ;
        float targetPitch = (float) Math.atan2(targetY, MathHelper.sqrt_float(horizontalDistSq));
        float targetLen = MathHelper.sqrt_float(horizontalDistSq + targetY * targetY);
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
            VertexBuffer vb = tessellator.getBuffer();
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            double y = -0.687;
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
    protected ResourceLocation getEntityTexture(EntityWeedwoodRowboat entity) {
        return TEXTURE;
    }

    public static class ArmArticulation {
        public float shoulderAngleX;

        public float shoulderAngleY;

        public float flexionAngle;

        public float shoulderZ;
    }
}