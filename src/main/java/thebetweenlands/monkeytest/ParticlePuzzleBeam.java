package thebetweenlands.monkeytest;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class ParticlePuzzleBeam extends Particle implements IParticleTracked {

  public float colorR = 0;
  public float colorG = 0;
  public float colorB = 0;
  public float initScale = 0;
  public float initAlpha = 0;
  public static ResourceLocation texture = new ResourceLocation(ModInfo.ID, "particle/particle_beam");

  public ParticlePuzzleBeam(World worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime) {
    super(worldIn, x, y, z, 0, 0, 0);
    this.colorR = r;
    this.colorG = g;
    this.colorB = b;
    if (this.colorR > 1.0) {
      this.colorR = this.colorR / 255.0f;
    }
    if (this.colorG > 1.0) {
      this.colorG = this.colorG / 255.0f;
    }
    if (this.colorB > 1.0) {
      this.colorB = this.colorB / 255.0f;
    }
    this.setRBGColorF(colorR, colorG, colorB);
    this.particleMaxAge = (int) ((float) lifetime * 0.5f);
    this.particleScale = scale;
    this.initScale = scale;
    this.motionX = vx * 2.0f;
    this.motionY = vy * 2.0f;
    this.motionZ = vz * 2.0f;
    this.initAlpha = a;
    this.particleAngle = 2.0f * (float) Math.PI;
    TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
    this.setParticleTexture(sprite);
  }

  @Override
  public int getBrightnessForRender(float pTicks) {
    return 255;
  }

  @Override
  public boolean shouldDisableDepth() {
    return true;
  }

  @Override
  public int getFXLayer() {
    return 1;
  }

  public static Random random = new Random();

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (random.nextInt(6) == 0) {
      this.particleAge++;
    }
    float lifeCoeff = (float) this.particleAge / (float) this.particleMaxAge;
    this.particleScale = initScale - initScale * lifeCoeff;
    this.particleAlpha = initAlpha * (1.0f - lifeCoeff);
    this.prevParticleAngle = particleAngle;
    particleAngle += 1.0f;
  }

  @Override
  public boolean alive() {
    return this.particleAge < this.particleMaxAge;
  }

  @Override
  public boolean isAdditive() {
    return true;
  }

  @Override
  public boolean renderThroughBlocks() {
    return false;
  }

  @Override
  public void renderParticle(BufferBuilder buffer, EntityPlayer player, float partialTicks, float f, float f4, float f1, float f2, float f3) {
    super.renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
  }

  @Override
  public boolean ignoreDepth() {
    return false;
  }
}
