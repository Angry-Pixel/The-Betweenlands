package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TerrainParticle extends TextureSheetParticle {
   private final BlockPos pos;
   private final float uo;
   private final float vo;

   public TerrainParticle(ClientLevel p_108282_, double p_108283_, double p_108284_, double p_108285_, double p_108286_, double p_108287_, double p_108288_, BlockState p_108289_) {
      this(p_108282_, p_108283_, p_108284_, p_108285_, p_108286_, p_108287_, p_108288_, p_108289_, new BlockPos(p_108283_, p_108284_, p_108285_));
   }

   public TerrainParticle(ClientLevel p_172451_, double p_172452_, double p_172453_, double p_172454_, double p_172455_, double p_172456_, double p_172457_, BlockState p_172458_, BlockPos p_172459_) {
      super(p_172451_, p_172452_, p_172453_, p_172454_, p_172455_, p_172456_, p_172457_);
      this.pos = p_172459_;
      this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(p_172458_));
      this.gravity = 1.0F;
      this.rCol = 0.6F;
      this.gCol = 0.6F;
      this.bCol = 0.6F;
      if (!p_172458_.is(Blocks.GRASS_BLOCK)) {
         int i = Minecraft.getInstance().getBlockColors().getColor(p_172458_, p_172451_, p_172459_, 0);
         this.rCol *= (float)(i >> 16 & 255) / 255.0F;
         this.gCol *= (float)(i >> 8 & 255) / 255.0F;
         this.bCol *= (float)(i & 255) / 255.0F;
      }

      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   protected float getU0() {
      return this.sprite.getU((double)((this.uo + 1.0F) / 4.0F * 16.0F));
   }

   protected float getU1() {
      return this.sprite.getU((double)(this.uo / 4.0F * 16.0F));
   }

   protected float getV0() {
      return this.sprite.getV((double)(this.vo / 4.0F * 16.0F));
   }

   protected float getV1() {
      return this.sprite.getV((double)((this.vo + 1.0F) / 4.0F * 16.0F));
   }

   public int getLightColor(float p_108291_) {
      int i = super.getLightColor(p_108291_);
      return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Particle createParticle(BlockParticleOption p_108304_, ClientLevel p_108305_, double p_108306_, double p_108307_, double p_108308_, double p_108309_, double p_108310_, double p_108311_) {
         BlockState blockstate = p_108304_.getState();
         return !blockstate.isAir() && !blockstate.is(Blocks.MOVING_PISTON) ? (new TerrainParticle(p_108305_, p_108306_, p_108307_, p_108308_, p_108309_, p_108310_, p_108311_, blockstate)).updateSprite(blockstate, p_108304_.getPos()) : null;
      }
   }

   public Particle updateSprite(BlockState state, BlockPos pos) { //FORGE: we cannot assume that the x y z of the particles match the block pos of the block.
      if (pos != null) // There are cases where we are not able to obtain the correct source pos, and need to fallback to the non-model data version
         this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, level, pos));
      return this;
   }
}
