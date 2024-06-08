package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class PathFinder {
   private static final float FUDGING = 1.5F;
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private final NodeEvaluator nodeEvaluator;
   private static final boolean DEBUG = false;
   private final BinaryHeap openSet = new BinaryHeap();

   public PathFinder(NodeEvaluator p_77425_, int p_77426_) {
      this.nodeEvaluator = p_77425_;
      this.maxVisitedNodes = p_77426_;
   }

   @Nullable
   public Path findPath(PathNavigationRegion p_77428_, Mob p_77429_, Set<BlockPos> p_77430_, float p_77431_, int p_77432_, float p_77433_) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(p_77428_, p_77429_);
      Node node = this.nodeEvaluator.getStart();
      Map<Target, BlockPos> map = p_77430_.stream().collect(Collectors.toMap((p_77448_) -> {
         return this.nodeEvaluator.getGoal((double)p_77448_.getX(), (double)p_77448_.getY(), (double)p_77448_.getZ());
      }, Function.identity()));
      Path path = this.findPath(p_77428_.getProfiler(), node, map, p_77431_, p_77432_, p_77433_);
      this.nodeEvaluator.done();
      return path;
   }

   @Nullable
   private Path findPath(ProfilerFiller p_164717_, Node p_164718_, Map<Target, BlockPos> p_164719_, float p_164720_, int p_164721_, float p_164722_) {
      p_164717_.push("find_path");
      p_164717_.markForCharting(MetricCategory.PATH_FINDING);
      Set<Target> set = p_164719_.keySet();
      p_164718_.g = 0.0F;
      p_164718_.h = this.getBestH(p_164718_, set);
      p_164718_.f = p_164718_.h;
      this.openSet.clear();
      this.openSet.insert(p_164718_);
      Set<Node> set1 = ImmutableSet.of();
      int i = 0;
      Set<Target> set2 = Sets.newHashSetWithExpectedSize(set.size());
      int j = (int)((float)this.maxVisitedNodes * p_164722_);

      while(!this.openSet.isEmpty()) {
         ++i;
         if (i >= j) {
            break;
         }

         Node node = this.openSet.pop();
         node.closed = true;

         for(Target target : set) {
            if (node.distanceManhattan(target) <= (float)p_164721_) {
               target.setReached();
               set2.add(target);
            }
         }

         if (!set2.isEmpty()) {
            break;
         }

         if (!(node.distanceTo(p_164718_) >= p_164720_)) {
            int k = this.nodeEvaluator.getNeighbors(this.neighbors, node);

            for(int l = 0; l < k; ++l) {
               Node node1 = this.neighbors[l];
               float f = node.distanceTo(node1);
               node1.walkedDistance = node.walkedDistance + f;
               float f1 = node.g + f + node1.costMalus;
               if (node1.walkedDistance < p_164720_ && (!node1.inOpenSet() || f1 < node1.g)) {
                  node1.cameFrom = node;
                  node1.g = f1;
                  node1.h = this.getBestH(node1, set) * 1.5F;
                  if (node1.inOpenSet()) {
                     this.openSet.changeCost(node1, node1.g + node1.h);
                  } else {
                     node1.f = node1.g + node1.h;
                     this.openSet.insert(node1);
                  }
               }
            }
         }
      }

      Optional<Path> optional = !set2.isEmpty() ? set2.stream().map((p_77454_) -> {
         return this.reconstructPath(p_77454_.getBestNode(), p_164719_.get(p_77454_), true);
      }).min(Comparator.comparingInt(Path::getNodeCount)) : set.stream().map((p_77451_) -> {
         return this.reconstructPath(p_77451_.getBestNode(), p_164719_.get(p_77451_), false);
      }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
      p_164717_.pop();
      return !optional.isPresent() ? null : optional.get();
   }

   private float getBestH(Node p_77445_, Set<Target> p_77446_) {
      float f = Float.MAX_VALUE;

      for(Target target : p_77446_) {
         float f1 = p_77445_.distanceTo(target);
         target.updateBest(f1, p_77445_);
         f = Math.min(f1, f);
      }

      return f;
   }

   private Path reconstructPath(Node p_77435_, BlockPos p_77436_, boolean p_77437_) {
      List<Node> list = Lists.newArrayList();
      Node node = p_77435_;
      list.add(0, p_77435_);

      while(node.cameFrom != null) {
         node = node.cameFrom;
         list.add(0, node);
      }

      return new Path(list, p_77436_, p_77437_);
   }
}