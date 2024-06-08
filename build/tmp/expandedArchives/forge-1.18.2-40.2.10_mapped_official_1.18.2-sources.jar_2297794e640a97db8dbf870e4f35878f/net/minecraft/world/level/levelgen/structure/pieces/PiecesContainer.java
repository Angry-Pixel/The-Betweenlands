package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.slf4j.Logger;

public record PiecesContainer(List<StructurePiece> pieces) {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
   private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.<ResourceLocation, ResourceLocation>builder().put(new ResourceLocation("nvi"), JIGSAW_RENAME).put(new ResourceLocation("pcp"), JIGSAW_RENAME).put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME).put(new ResourceLocation("runtime"), JIGSAW_RENAME).build();

   public PiecesContainer(List<StructurePiece> pieces) {
      this.pieces = List.copyOf(pieces);
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public boolean isInsidePiece(BlockPos p_192752_) {
      for(StructurePiece structurepiece : this.pieces) {
         if (structurepiece.getBoundingBox().isInside(p_192752_)) {
            return true;
         }
      }

      return false;
   }

   public Tag save(StructurePieceSerializationContext p_192750_) {
      ListTag listtag = new ListTag();

      for(StructurePiece structurepiece : this.pieces) {
         listtag.add(structurepiece.createTag(p_192750_));
      }

      return listtag;
   }

   public static PiecesContainer load(ListTag p_192754_, StructurePieceSerializationContext p_192755_) {
      List<StructurePiece> list = Lists.newArrayList();

      for(int i = 0; i < p_192754_.size(); ++i) {
         CompoundTag compoundtag = p_192754_.getCompound(i);
         String s = compoundtag.getString("id").toLowerCase(Locale.ROOT);
         ResourceLocation resourcelocation = new ResourceLocation(s);
         ResourceLocation resourcelocation1 = RENAMES.getOrDefault(resourcelocation, resourcelocation);
         StructurePieceType structurepiecetype = Registry.STRUCTURE_PIECE.get(resourcelocation1);
         if (structurepiecetype == null) {
            LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
         } else {
            try {
               StructurePiece structurepiece = structurepiecetype.load(p_192755_, compoundtag);
               list.add(structurepiece);
            } catch (Exception exception) {
               LOGGER.error("Exception loading structure piece with id {}", resourcelocation1, exception);
            }
         }
      }

      return new PiecesContainer(list);
   }

   public BoundingBox calculateBoundingBox() {
      return StructurePiece.createBoundingBox(this.pieces.stream());
   }
}