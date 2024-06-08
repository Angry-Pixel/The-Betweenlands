package net.minecraft.world.level.pathfinder;

public class BinaryHeap {
   private Node[] heap = new Node[128];
   private int size;

   public Node insert(Node p_77085_) {
      if (p_77085_.heapIdx >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.size == this.heap.length) {
            Node[] anode = new Node[this.size << 1];
            System.arraycopy(this.heap, 0, anode, 0, this.size);
            this.heap = anode;
         }

         this.heap[this.size] = p_77085_;
         p_77085_.heapIdx = this.size;
         this.upHeap(this.size++);
         return p_77085_;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public Node peek() {
      return this.heap[0];
   }

   public Node pop() {
      Node node = this.heap[0];
      this.heap[0] = this.heap[--this.size];
      this.heap[this.size] = null;
      if (this.size > 0) {
         this.downHeap(0);
      }

      node.heapIdx = -1;
      return node;
   }

   public void remove(Node p_164682_) {
      this.heap[p_164682_.heapIdx] = this.heap[--this.size];
      this.heap[this.size] = null;
      if (this.size > p_164682_.heapIdx) {
         if (this.heap[p_164682_.heapIdx].f < p_164682_.f) {
            this.upHeap(p_164682_.heapIdx);
         } else {
            this.downHeap(p_164682_.heapIdx);
         }
      }

      p_164682_.heapIdx = -1;
   }

   public void changeCost(Node p_77087_, float p_77088_) {
      float f = p_77087_.f;
      p_77087_.f = p_77088_;
      if (p_77088_ < f) {
         this.upHeap(p_77087_.heapIdx);
      } else {
         this.downHeap(p_77087_.heapIdx);
      }

   }

   public int size() {
      return this.size;
   }

   private void upHeap(int p_77083_) {
      Node node = this.heap[p_77083_];

      int i;
      for(float f = node.f; p_77083_ > 0; p_77083_ = i) {
         i = p_77083_ - 1 >> 1;
         Node node1 = this.heap[i];
         if (!(f < node1.f)) {
            break;
         }

         this.heap[p_77083_] = node1;
         node1.heapIdx = p_77083_;
      }

      this.heap[p_77083_] = node;
      node.heapIdx = p_77083_;
   }

   private void downHeap(int p_77090_) {
      Node node = this.heap[p_77090_];
      float f = node.f;

      while(true) {
         int i = 1 + (p_77090_ << 1);
         int j = i + 1;
         if (i >= this.size) {
            break;
         }

         Node node1 = this.heap[i];
         float f1 = node1.f;
         Node node2;
         float f2;
         if (j >= this.size) {
            node2 = null;
            f2 = Float.POSITIVE_INFINITY;
         } else {
            node2 = this.heap[j];
            f2 = node2.f;
         }

         if (f1 < f2) {
            if (!(f1 < f)) {
               break;
            }

            this.heap[p_77090_] = node1;
            node1.heapIdx = p_77090_;
            p_77090_ = i;
         } else {
            if (!(f2 < f)) {
               break;
            }

            this.heap[p_77090_] = node2;
            node2.heapIdx = p_77090_;
            p_77090_ = j;
         }
      }

      this.heap[p_77090_] = node;
      node.heapIdx = p_77090_;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Node[] getHeap() {
      Node[] anode = new Node[this.size()];
      System.arraycopy(this.heap, 0, anode, 0, this.size());
      return anode;
   }
}