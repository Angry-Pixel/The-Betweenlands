package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Path {
   private final List<Node> nodes;
   private Node[] openSet = new Node[0];
   private Node[] closedSet = new Node[0];
   @Nullable
   private Set<Target> targetNodes;
   private int nextNodeIndex;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(List<Node> p_77371_, BlockPos p_77372_, boolean p_77373_) {
      this.nodes = p_77371_;
      this.target = p_77372_;
      this.distToTarget = p_77371_.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).distanceManhattan(this.target);
      this.reached = p_77373_;
   }

   public void advance() {
      ++this.nextNodeIndex;
   }

   public boolean notStarted() {
      return this.nextNodeIndex <= 0;
   }

   public boolean isDone() {
      return this.nextNodeIndex >= this.nodes.size();
   }

   @Nullable
   public Node getEndNode() {
      return !this.nodes.isEmpty() ? this.nodes.get(this.nodes.size() - 1) : null;
   }

   public Node getNode(int p_77376_) {
      return this.nodes.get(p_77376_);
   }

   public void truncateNodes(int p_77389_) {
      if (this.nodes.size() > p_77389_) {
         this.nodes.subList(p_77389_, this.nodes.size()).clear();
      }

   }

   public void replaceNode(int p_77378_, Node p_77379_) {
      this.nodes.set(p_77378_, p_77379_);
   }

   public int getNodeCount() {
      return this.nodes.size();
   }

   public int getNextNodeIndex() {
      return this.nextNodeIndex;
   }

   public void setNextNodeIndex(int p_77394_) {
      this.nextNodeIndex = p_77394_;
   }

   public Vec3 getEntityPosAtNode(Entity p_77383_, int p_77384_) {
      Node node = this.nodes.get(p_77384_);
      double d0 = (double)node.x + (double)((int)(p_77383_.getBbWidth() + 1.0F)) * 0.5D;
      double d1 = (double)node.y;
      double d2 = (double)node.z + (double)((int)(p_77383_.getBbWidth() + 1.0F)) * 0.5D;
      return new Vec3(d0, d1, d2);
   }

   public BlockPos getNodePos(int p_77397_) {
      return this.nodes.get(p_77397_).asBlockPos();
   }

   public Vec3 getNextEntityPos(Entity p_77381_) {
      return this.getEntityPosAtNode(p_77381_, this.nextNodeIndex);
   }

   public BlockPos getNextNodePos() {
      return this.nodes.get(this.nextNodeIndex).asBlockPos();
   }

   public Node getNextNode() {
      return this.nodes.get(this.nextNodeIndex);
   }

   @Nullable
   public Node getPreviousNode() {
      return this.nextNodeIndex > 0 ? this.nodes.get(this.nextNodeIndex - 1) : null;
   }

   public boolean sameAs(@Nullable Path p_77386_) {
      if (p_77386_ == null) {
         return false;
      } else if (p_77386_.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int i = 0; i < this.nodes.size(); ++i) {
            Node node = this.nodes.get(i);
            Node node1 = p_77386_.nodes.get(i);
            if (node.x != node1.x || node.y != node1.y || node.z != node1.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canReach() {
      return this.reached;
   }

   @VisibleForDebug
   void setDebug(Node[] p_164710_, Node[] p_164711_, Set<Target> p_164712_) {
      this.openSet = p_164710_;
      this.closedSet = p_164711_;
      this.targetNodes = p_164712_;
   }

   @VisibleForDebug
   public Node[] getOpenSet() {
      return this.openSet;
   }

   @VisibleForDebug
   public Node[] getClosedSet() {
      return this.closedSet;
   }

   public void writeToStream(FriendlyByteBuf p_164705_) {
      if (this.targetNodes != null && !this.targetNodes.isEmpty()) {
         p_164705_.writeBoolean(this.reached);
         p_164705_.writeInt(this.nextNodeIndex);
         p_164705_.writeInt(this.targetNodes.size());
         this.targetNodes.forEach((p_164708_) -> {
            p_164708_.writeToStream(p_164705_);
         });
         p_164705_.writeInt(this.target.getX());
         p_164705_.writeInt(this.target.getY());
         p_164705_.writeInt(this.target.getZ());
         p_164705_.writeInt(this.nodes.size());

         for(Node node : this.nodes) {
            node.writeToStream(p_164705_);
         }

         p_164705_.writeInt(this.openSet.length);

         for(Node node1 : this.openSet) {
            node1.writeToStream(p_164705_);
         }

         p_164705_.writeInt(this.closedSet.length);

         for(Node node2 : this.closedSet) {
            node2.writeToStream(p_164705_);
         }

      }
   }

   public static Path createFromStream(FriendlyByteBuf p_77391_) {
      boolean flag = p_77391_.readBoolean();
      int i = p_77391_.readInt();
      int j = p_77391_.readInt();
      Set<Target> set = Sets.newHashSet();

      for(int k = 0; k < j; ++k) {
         set.add(Target.createFromStream(p_77391_));
      }

      BlockPos blockpos = new BlockPos(p_77391_.readInt(), p_77391_.readInt(), p_77391_.readInt());
      List<Node> list = Lists.newArrayList();
      int l = p_77391_.readInt();

      for(int i1 = 0; i1 < l; ++i1) {
         list.add(Node.createFromStream(p_77391_));
      }

      Node[] anode = new Node[p_77391_.readInt()];

      for(int j1 = 0; j1 < anode.length; ++j1) {
         anode[j1] = Node.createFromStream(p_77391_);
      }

      Node[] anode1 = new Node[p_77391_.readInt()];

      for(int k1 = 0; k1 < anode1.length; ++k1) {
         anode1[k1] = Node.createFromStream(p_77391_);
      }

      Path path = new Path(list, blockpos, flag);
      path.openSet = anode;
      path.closedSet = anode1;
      path.targetNodes = set;
      path.nextNodeIndex = i;
      return path;
   }

   public String toString() {
      return "Path(length=" + this.nodes.size() + ")";
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public float getDistToTarget() {
      return this.distToTarget;
   }
}