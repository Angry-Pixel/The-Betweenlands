package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FluidState extends StateHolder<Fluid, FluidState> implements net.minecraftforge.common.extensions.IForgeFluidState {
   public static final Codec<FluidState> CODEC = codec(Registry.FLUID.byNameCodec(), Fluid::defaultFluidState).stable();
   public static final int AMOUNT_MAX = 9;
   public static final int AMOUNT_FULL = 8;

   public FluidState(Fluid p_76149_, ImmutableMap<Property<?>, Comparable<?>> p_76150_, MapCodec<FluidState> p_76151_) {
      super(p_76149_, p_76150_, p_76151_);
   }

   public Fluid getType() {
      return this.owner;
   }

   public boolean isSource() {
      return this.getType().isSource(this);
   }

   public boolean isSourceOfType(Fluid p_164513_) {
      return this.owner == p_164513_ && this.owner.isSource(this);
   }

   public boolean isEmpty() {
      return this.getType().isEmpty();
   }

   public float getHeight(BlockGetter p_76156_, BlockPos p_76157_) {
      return this.getType().getHeight(this, p_76156_, p_76157_);
   }

   public float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   public int getAmount() {
      return this.getType().getAmount(this);
   }

   public boolean shouldRenderBackwardUpFace(BlockGetter p_76172_, BlockPos p_76173_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = p_76173_.offset(i, 0, j);
            FluidState fluidstate = p_76172_.getFluidState(blockpos);
            if (!fluidstate.getType().isSame(this.getType()) && !p_76172_.getBlockState(blockpos).isSolidRender(p_76172_, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   public void tick(Level p_76164_, BlockPos p_76165_) {
      this.getType().tick(p_76164_, p_76165_, this);
   }

   public void animateTick(Level p_76167_, BlockPos p_76168_, Random p_76169_) {
      this.getType().animateTick(p_76167_, p_76168_, this, p_76169_);
   }

   public boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   public void randomTick(Level p_76175_, BlockPos p_76176_, Random p_76177_) {
      this.getType().randomTick(p_76175_, p_76176_, this, p_76177_);
   }

   public Vec3 getFlow(BlockGetter p_76180_, BlockPos p_76181_) {
      return this.getType().getFlow(p_76180_, p_76181_, this);
   }

   public BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return this.getType().getDripParticle();
   }

   public boolean is(TagKey<Fluid> p_205071_) {
      return this.getType().builtInRegistryHolder().is(p_205071_);
   }

   public boolean is(HolderSet<Fluid> p_205073_) {
      return p_205073_.contains(this.getType().builtInRegistryHolder());
   }

   public boolean is(Fluid p_192918_) {
      return this.getType() == p_192918_;
   }

   @Deprecated //Forge: Use more sensitive version
   public float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   public boolean canBeReplacedWith(BlockGetter p_76159_, BlockPos p_76160_, Fluid p_76161_, Direction p_76162_) {
      return this.getType().canBeReplacedWith(this, p_76159_, p_76160_, p_76161_, p_76162_);
   }

   public VoxelShape getShape(BlockGetter p_76184_, BlockPos p_76185_) {
      return this.getType().getShape(this, p_76184_, p_76185_);
   }

   public Holder<Fluid> holder() {
      return this.owner.builtInRegistryHolder();
   }

   public Stream<TagKey<Fluid>> getTags() {
      return this.owner.builtInRegistryHolder().tags();
   }
}
