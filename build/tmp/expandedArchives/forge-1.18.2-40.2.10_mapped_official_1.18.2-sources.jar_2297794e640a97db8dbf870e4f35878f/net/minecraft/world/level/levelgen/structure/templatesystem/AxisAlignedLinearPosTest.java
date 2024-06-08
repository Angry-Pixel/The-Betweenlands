package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class AxisAlignedLinearPosTest extends PosRuleTest {
   public static final Codec<AxisAlignedLinearPosTest> CODEC = RecordCodecBuilder.create((p_73977_) -> {
      return p_73977_.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((p_163719_) -> {
         return p_163719_.minChance;
      }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((p_163717_) -> {
         return p_163717_.maxChance;
      }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((p_163715_) -> {
         return p_163715_.minDist;
      }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((p_163713_) -> {
         return p_163713_.maxDist;
      }), Direction.Axis.CODEC.fieldOf("axis").orElse(Direction.Axis.Y).forGetter((p_163711_) -> {
         return p_163711_.axis;
      })).apply(p_73977_, AxisAlignedLinearPosTest::new);
   });
   private final float minChance;
   private final float maxChance;
   private final int minDist;
   private final int maxDist;
   private final Direction.Axis axis;

   public AxisAlignedLinearPosTest(float p_73970_, float p_73971_, int p_73972_, int p_73973_, Direction.Axis p_73974_) {
      if (p_73972_ >= p_73973_) {
         throw new IllegalArgumentException("Invalid range: [" + p_73972_ + "," + p_73973_ + "]");
      } else {
         this.minChance = p_73970_;
         this.maxChance = p_73971_;
         this.minDist = p_73972_;
         this.maxDist = p_73973_;
         this.axis = p_73974_;
      }
   }

   public boolean test(BlockPos p_73981_, BlockPos p_73982_, BlockPos p_73983_, Random p_73984_) {
      Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
      float f = (float)Math.abs((p_73982_.getX() - p_73983_.getX()) * direction.getStepX());
      float f1 = (float)Math.abs((p_73982_.getY() - p_73983_.getY()) * direction.getStepY());
      float f2 = (float)Math.abs((p_73982_.getZ() - p_73983_.getZ()) * direction.getStepZ());
      int i = (int)(f + f1 + f2);
      float f3 = p_73984_.nextFloat();
      return f3 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp((float)i, (float)this.minDist, (float)this.maxDist));
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS_TEST;
   }
}