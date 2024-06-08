package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;

public abstract class NodeEvaluator {
   protected PathNavigationRegion level;
   protected Mob mob;
   protected final Int2ObjectMap<Node> nodes = new Int2ObjectOpenHashMap<>();
   protected int entityWidth;
   protected int entityHeight;
   protected int entityDepth;
   protected boolean canPassDoors;
   protected boolean canOpenDoors;
   protected boolean canFloat;

   public void prepare(PathNavigationRegion p_77347_, Mob p_77348_) {
      this.level = p_77347_;
      this.mob = p_77348_;
      this.nodes.clear();
      this.entityWidth = Mth.floor(p_77348_.getBbWidth() + 1.0F);
      this.entityHeight = Mth.floor(p_77348_.getBbHeight() + 1.0F);
      this.entityDepth = Mth.floor(p_77348_.getBbWidth() + 1.0F);
   }

   public void done() {
      this.level = null;
      this.mob = null;
   }

   protected Node getNode(BlockPos p_77350_) {
      return this.getNode(p_77350_.getX(), p_77350_.getY(), p_77350_.getZ());
   }

   protected Node getNode(int p_77325_, int p_77326_, int p_77327_) {
      return this.nodes.computeIfAbsent(Node.createHash(p_77325_, p_77326_, p_77327_), (p_77332_) -> {
         return new Node(p_77325_, p_77326_, p_77327_);
      });
   }

   public abstract Node getStart();

   public abstract Target getGoal(double p_77322_, double p_77323_, double p_77324_);

   public abstract int getNeighbors(Node[] p_77353_, Node p_77354_);

   public abstract BlockPathTypes getBlockPathType(BlockGetter p_77337_, int p_77338_, int p_77339_, int p_77340_, Mob p_77341_, int p_77342_, int p_77343_, int p_77344_, boolean p_77345_, boolean p_77346_);

   public abstract BlockPathTypes getBlockPathType(BlockGetter p_77333_, int p_77334_, int p_77335_, int p_77336_);

   public void setCanPassDoors(boolean p_77352_) {
      this.canPassDoors = p_77352_;
   }

   public void setCanOpenDoors(boolean p_77356_) {
      this.canOpenDoors = p_77356_;
   }

   public void setCanFloat(boolean p_77359_) {
      this.canFloat = p_77359_;
   }

   public boolean canPassDoors() {
      return this.canPassDoors;
   }

   public boolean canOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean canFloat() {
      return this.canFloat;
   }
}