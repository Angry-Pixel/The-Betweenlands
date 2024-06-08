package net.minecraft.client.model.geom.builders;

import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CubeDefinition {
   @Nullable
   private final String comment;
   private final Vector3f origin;
   private final Vector3f dimensions;
   private final CubeDeformation grow;
   private final boolean mirror;
   private final UVPair texCoord;
   private final UVPair texScale;

   protected CubeDefinition(@Nullable String p_171442_, float p_171443_, float p_171444_, float p_171445_, float p_171446_, float p_171447_, float p_171448_, float p_171449_, float p_171450_, CubeDeformation p_171451_, boolean p_171452_, float p_171453_, float p_171454_) {
      this.comment = p_171442_;
      this.texCoord = new UVPair(p_171443_, p_171444_);
      this.origin = new Vector3f(p_171445_, p_171446_, p_171447_);
      this.dimensions = new Vector3f(p_171448_, p_171449_, p_171450_);
      this.grow = p_171451_;
      this.mirror = p_171452_;
      this.texScale = new UVPair(p_171453_, p_171454_);
   }

   public ModelPart.Cube bake(int p_171456_, int p_171457_) {
      return new ModelPart.Cube((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)p_171456_ * this.texScale.u(), (float)p_171457_ * this.texScale.v());
   }
}