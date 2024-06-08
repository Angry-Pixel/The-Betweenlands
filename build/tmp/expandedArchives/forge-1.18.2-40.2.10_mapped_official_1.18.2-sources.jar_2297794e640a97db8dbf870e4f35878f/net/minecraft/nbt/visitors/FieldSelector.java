package net.minecraft.nbt.visitors;

import java.util.List;
import net.minecraft.nbt.TagType;

public record FieldSelector(List<String> path, TagType<?> type, String name) {
   public FieldSelector(TagType<?> p_202514_, String p_202515_) {
      this(List.of(), p_202514_, p_202515_);
   }

   public FieldSelector(String p_202506_, TagType<?> p_202507_, String p_202508_) {
      this(List.of(p_202506_), p_202507_, p_202508_);
   }

   public FieldSelector(String p_202501_, String p_202502_, TagType<?> p_202503_, String p_202504_) {
      this(List.of(p_202501_, p_202502_), p_202503_, p_202504_);
   }
}