package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;

public class StructureProcessorList {
   private final List<StructureProcessor> list;

   public StructureProcessorList(List<StructureProcessor> p_74424_) {
      this.list = p_74424_;
   }

   public List<StructureProcessor> list() {
      return this.list;
   }

   public String toString() {
      return "ProcessorList[" + this.list + "]";
   }
}