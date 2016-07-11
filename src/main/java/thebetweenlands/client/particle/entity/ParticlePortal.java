package thebetweenlands.client.particle.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticlePortal extends Particle {
    private ResourceLocation particleTexture;
    private float scale;
    private int color;
    private int textures;
    private double relativeTextureHeight;
    private int currentTexture = 0;
    private int textureCounter = 0;

    public ParticlePortal(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, int color, ResourceLocation texture, int textures) {
        super(world, x, y, z, 0, 0, 0);
        this.posX = this.prevPosX = x;
        this.posY = this.prevPosY = y;
        this.posZ = this.prevPosZ = z;
        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
        this.particleMaxAge = maxAge;
        //this.noClip = false;
        this.color = color;
        this.scale = scale;
        this.textures = textures;
        this.relativeTextureHeight = 1.0D / this.textures;
        this.particleTexture = texture;
    }

    @Override
    public void renderParticle(VertexBuffer buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
        float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
        float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
        float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.particleTexture);

        float a = (float)(this.color >> 24 & 0xff) / 255F;
        float r = (float)(this.color >> 16 & 0xff) / 255F;
        float g = (float)(this.color >> 8 & 0xff) / 255F;
        float b = (float)(this.color & 0xff) / 255F;

        //par1Tessellator.startDrawingQuads();
        //par1Tessellator.setBrightness(this.getBrightnessForRender(partialTicks));

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        //buff.color(r, g, b, a);

        //par1Tessellator.setColorRGBA_F(r, g, b, a);
        this.draw(buff, ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 1.0D, (this.currentTexture + 1) * this.relativeTextureHeight);
        this.draw(buff, ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 1.0D, this.currentTexture * this.relativeTextureHeight);
        this.draw(buff, ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 0.0D, this.currentTexture * this.relativeTextureHeight);
        this.draw(buff, ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 0.0D, (this.currentTexture + 1) * this.relativeTextureHeight);
        tessellator.draw();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }


    private static void draw(VertexBuffer buff, double x, double y, double z, double U, double V){
        buff.pos(x, y, z);
        buff.tex(U, V);
        buff.color(1.0f, 1.0f, 1.0f, 1.0f);
        buff.endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(!this.isCollided) {
            this.textureCounter++;
            if(this.textureCounter >= 3) {
                this.textureCounter = 0;
                this.currentTexture++;
                if(this.currentTexture >= this.textures) {
                    this.currentTexture = 0;
                }
            }
        }
    }

}
