package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CubeListBuilder {
   private final List<CubeDefinition> cubes = Lists.newArrayList();
   private int xTexOffs;
   private int yTexOffs;
   private boolean mirror;

   public CubeListBuilder texOffs(int p_171515_, int p_171516_) {
      this.xTexOffs = p_171515_;
      this.yTexOffs = p_171516_;
      return this;
   }

   public CubeListBuilder mirror() {
      return this.mirror(true);
   }

   public CubeListBuilder mirror(boolean p_171556_) {
      this.mirror = p_171556_;
      return this;
   }

   public CubeListBuilder addBox(String p_171545_, float p_171546_, float p_171547_, float p_171548_, int p_171549_, int p_171550_, int p_171551_, CubeDeformation p_171552_, int p_171553_, int p_171554_) {
      this.texOffs(p_171553_, p_171554_);
      this.cubes.add(new CubeDefinition(p_171545_, (float)this.xTexOffs, (float)this.yTexOffs, p_171546_, p_171547_, p_171548_, (float)p_171549_, (float)p_171550_, (float)p_171551_, p_171552_, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(String p_171535_, float p_171536_, float p_171537_, float p_171538_, int p_171539_, int p_171540_, int p_171541_, int p_171542_, int p_171543_) {
      this.texOffs(p_171542_, p_171543_);
      this.cubes.add(new CubeDefinition(p_171535_, (float)this.xTexOffs, (float)this.yTexOffs, p_171536_, p_171537_, p_171538_, (float)p_171539_, (float)p_171540_, (float)p_171541_, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(float p_171482_, float p_171483_, float p_171484_, float p_171485_, float p_171486_, float p_171487_) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, p_171482_, p_171483_, p_171484_, p_171485_, p_171486_, p_171487_, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(String p_171518_, float p_171519_, float p_171520_, float p_171521_, float p_171522_, float p_171523_, float p_171524_) {
      this.cubes.add(new CubeDefinition(p_171518_, (float)this.xTexOffs, (float)this.yTexOffs, p_171519_, p_171520_, p_171521_, p_171522_, p_171523_, p_171524_, CubeDeformation.NONE, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(String p_171526_, float p_171527_, float p_171528_, float p_171529_, float p_171530_, float p_171531_, float p_171532_, CubeDeformation p_171533_) {
      this.cubes.add(new CubeDefinition(p_171526_, (float)this.xTexOffs, (float)this.yTexOffs, p_171527_, p_171528_, p_171529_, p_171530_, p_171531_, p_171532_, p_171533_, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(float p_171507_, float p_171508_, float p_171509_, float p_171510_, float p_171511_, float p_171512_, boolean p_171513_) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, p_171507_, p_171508_, p_171509_, p_171510_, p_171511_, p_171512_, CubeDeformation.NONE, p_171513_, 1.0F, 1.0F));
      return this;
   }

   public CubeListBuilder addBox(float p_171497_, float p_171498_, float p_171499_, float p_171500_, float p_171501_, float p_171502_, CubeDeformation p_171503_, float p_171504_, float p_171505_) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, p_171497_, p_171498_, p_171499_, p_171500_, p_171501_, p_171502_, p_171503_, this.mirror, p_171504_, p_171505_));
      return this;
   }

   public CubeListBuilder addBox(float p_171489_, float p_171490_, float p_171491_, float p_171492_, float p_171493_, float p_171494_, CubeDeformation p_171495_) {
      this.cubes.add(new CubeDefinition((String)null, (float)this.xTexOffs, (float)this.yTexOffs, p_171489_, p_171490_, p_171491_, p_171492_, p_171493_, p_171494_, p_171495_, this.mirror, 1.0F, 1.0F));
      return this;
   }

   public List<CubeDefinition> getCubes() {
      return ImmutableList.copyOf(this.cubes);
   }

   public static CubeListBuilder create() {
      return new CubeListBuilder();
   }
}