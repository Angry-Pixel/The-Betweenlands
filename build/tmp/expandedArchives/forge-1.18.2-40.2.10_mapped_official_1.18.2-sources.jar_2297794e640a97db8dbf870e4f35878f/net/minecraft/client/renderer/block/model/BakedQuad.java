package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer {
   protected final int[] vertices;
   protected final int tintIndex;
   protected final Direction direction;
   protected final TextureAtlasSprite sprite;
   private final boolean shade;

   public BakedQuad(int[] p_111298_, int p_111299_, Direction p_111300_, TextureAtlasSprite p_111301_, boolean p_111302_) {
      this.vertices = p_111298_;
      this.tintIndex = p_111299_;
      this.direction = p_111300_;
      this.sprite = p_111301_;
      this.shade = p_111302_;
   }

   public TextureAtlasSprite getSprite() {
      return this.sprite;
   }

   public int[] getVertices() {
      return this.vertices;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getDirection() {
      return this.direction;
   }

   @Override
   public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer) {
      net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
   }

   public boolean isShade() {
      return this.shade;
   }
}
