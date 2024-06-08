package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMaterialPredicate implements Predicate<BlockState> {
   private static final BlockMaterialPredicate AIR = new BlockMaterialPredicate(Material.AIR) {
      public boolean test(@Nullable BlockState p_61269_) {
         return p_61269_ != null && p_61269_.isAir();
      }
   };
   private final Material material;

   BlockMaterialPredicate(Material p_61256_) {
      this.material = p_61256_;
   }

   public static BlockMaterialPredicate forMaterial(Material p_61263_) {
      return p_61263_ == Material.AIR ? AIR : new BlockMaterialPredicate(p_61263_);
   }

   public boolean test(@Nullable BlockState p_61261_) {
      return p_61261_ != null && p_61261_.getMaterial() == this.material;
   }
}