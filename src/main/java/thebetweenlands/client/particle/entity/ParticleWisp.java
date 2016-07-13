package thebetweenlands.client.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.util.MathUtils;


public class ParticleWisp extends Particle {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/wisp.png");

    private float prevFlameScale;
    private float flameScale;
    private int color;
    private int brightness;

    public ParticleWisp(World world, double x, double y, double z, double mx, double my, double mz, float size, int bright, int col) {
        super(world, x, y, z, mx, my, mz);
        motionX = motionX * 0.01D + mx;
        motionY = motionY * 0.01D + my;
        motionZ = motionZ * 0.01D + mz;
        x += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
        y += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
        z += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
        posX = prevPosX = x;
        posY = prevPosY = y;
        posZ = prevPosZ = z;
        flameScale = size;
        particleMaxAge = (int) (8 / (Math.random() * 0.8 + 0.2)) + 1000;
        color = col;
        brightness = bright;
    }

    @Override
    public void renderParticle(VertexBuffer buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
            float currentX = (float) (prevPosX + (posX - prevPosX) * partialTicks);
            float currentY = (float) (prevPosY + (posY - prevPosY) * partialTicks);
            float currentZ = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks);

            float ipx = (float) (currentX - interpPosX);
            float ipy = (float) (currentY - interpPosY);
            float ipz = (float) (currentZ - interpPosZ);
            float scale = (prevFlameScale + (flameScale - prevFlameScale) * partialTicks) / 10;

            //par1Tessellator.setBrightness(brightness);

            float distance = 0.0F;
            if(!BlockWisp.canSee(this.worldObj)) {
                distance = MathHelper.clamp_float(getDistanceToViewer(currentX, currentY, currentZ, partialTicks), 10, 20);
            }
            float a = (color >>> 24 & 0xFF) / 255F - MathHelper.sin(MathUtils.PI / 20 * distance);
            float r = (color >> 16 & 0xFF) / 255F;
            float g = (color >> 8 & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;


        buff.color(r, g, b, a);
        this.draw(buff, ipx - rx * scale - ryz * scale, ipy - rxz * scale, ipz - rz * scale - rxy * scale, 0.0D, 1.0D);
        this.draw(buff,ipx - rx * scale + ryz * scale, ipy + rxz * scale, ipz - rz * scale + rxy * scale, 1.0D, 1.0D);
        this.draw(buff, ipx + rx * scale + ryz * scale, ipy + rxz * scale, ipz + rz * scale + rxy * scale, 1.0D, 0.0D);
        this.draw(buff, ipx + rx * scale - ryz * scale, ipy - rxz * scale, ipz + rz * scale - rxy * scale, 0.0D, 0.0D);
    }

    private static void draw(VertexBuffer buff, double x, double y, double z, double U, double V){
        buff.pos(x, y, z);
        buff.tex(U, V);
        buff.color(1.0f, 1.0f, 1.0f, 1.0f);
        buff.endVertex();
    }

    public static float getDistanceToViewer(double x, double y, double z, float partialRenderTicks) {
            Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
            double dx = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialRenderTicks) - x;
            double dy = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialRenderTicks) - y;
            double dz = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialRenderTicks) - z;
            return MathHelper.sqrt_float((float) (dx * dx + dy * dy + dz * dz));
    }

    @Override
    public void onUpdate() {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            prevFlameScale = flameScale;

            moveEntity(motionX, motionY, motionZ);

            motionX *= 0.96;
            motionZ *= 0.96;

            if (particleAge++ >= particleMaxAge || flameScale <= 0) {
                this.setExpired();
            }
            if (particleAge != 0) {
                if (flameScale > 0) {
                    flameScale -= 0.025;
                }
                motionY += 0.00008;
            }

            moveEntity(motionX, motionY, motionZ);
    }
}

