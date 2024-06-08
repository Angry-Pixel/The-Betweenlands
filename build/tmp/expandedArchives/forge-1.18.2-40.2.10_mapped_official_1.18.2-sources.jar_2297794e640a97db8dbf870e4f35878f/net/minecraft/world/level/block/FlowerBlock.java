package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock {
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
   private final MobEffect suspiciousStewEffect;
   private final int effectDuration;

   private final java.util.function.Supplier<MobEffect> suspiciousStewEffectSupplier;

   public FlowerBlock(java.util.function.Supplier<MobEffect> effectSupplier, int p_53513_, BlockBehaviour.Properties p_53514_) {
      super(p_53514_);
      this.suspiciousStewEffect = null;
      this.suspiciousStewEffectSupplier = effectSupplier;
      this.effectDuration = p_53513_;
   }

   /** @deprecated FORGE: Use supplier version instead */
   @Deprecated
   public FlowerBlock(MobEffect p_53512_, int p_53513_, BlockBehaviour.Properties p_53514_) {
      super(p_53514_);
      this.suspiciousStewEffect = p_53512_;
      if (p_53512_.isInstantenous()) {
         this.effectDuration = p_53513_;
      } else {
         this.effectDuration = p_53513_ * 20;
      }
      this.suspiciousStewEffectSupplier = net.minecraftforge.registries.RegistryObject.create(p_53512_.getRegistryName(), net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS);

   }

   public VoxelShape getShape(BlockState p_53517_, BlockGetter p_53518_, BlockPos p_53519_, CollisionContext p_53520_) {
      Vec3 vec3 = p_53517_.getOffset(p_53518_, p_53519_);
      return SHAPE.move(vec3.x, vec3.y, vec3.z);
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }

   public MobEffect getSuspiciousStewEffect() {
      if (true) return this.suspiciousStewEffectSupplier.get();
      return this.suspiciousStewEffect;
   }

   public int getEffectDuration() {
      if (!this.suspiciousStewEffectSupplier.get().isInstantenous()) return this.effectDuration * 20;
      return this.effectDuration;
   }
}
