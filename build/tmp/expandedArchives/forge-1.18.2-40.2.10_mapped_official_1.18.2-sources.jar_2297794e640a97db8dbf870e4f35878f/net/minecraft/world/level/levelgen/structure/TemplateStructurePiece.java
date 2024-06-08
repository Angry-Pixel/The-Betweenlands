package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final String templateName;
   protected StructureTemplate template;
   protected StructurePlaceSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(StructurePieceType p_210083_, int p_210084_, StructureManager p_210085_, ResourceLocation p_210086_, String p_210087_, StructurePlaceSettings p_210088_, BlockPos p_210089_) {
      super(p_210083_, p_210084_, p_210085_.getOrCreate(p_210086_).getBoundingBox(p_210088_, p_210089_));
      this.setOrientation(Direction.NORTH);
      this.templateName = p_210087_;
      this.templatePosition = p_210089_;
      this.template = p_210085_.getOrCreate(p_210086_);
      this.placeSettings = p_210088_;
   }

   public TemplateStructurePiece(StructurePieceType p_210091_, CompoundTag p_210092_, StructureManager p_210093_, Function<ResourceLocation, StructurePlaceSettings> p_210094_) {
      super(p_210091_, p_210092_);
      this.setOrientation(Direction.NORTH);
      this.templateName = p_210092_.getString("Template");
      this.templatePosition = new BlockPos(p_210092_.getInt("TPX"), p_210092_.getInt("TPY"), p_210092_.getInt("TPZ"));
      ResourceLocation resourcelocation = this.makeTemplateLocation();
      this.template = p_210093_.getOrCreate(resourcelocation);
      this.placeSettings = p_210094_.apply(resourcelocation);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
   }

   protected ResourceLocation makeTemplateLocation() {
      return new ResourceLocation(this.templateName);
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_192690_, CompoundTag p_192691_) {
      p_192691_.putInt("TPX", this.templatePosition.getX());
      p_192691_.putInt("TPY", this.templatePosition.getY());
      p_192691_.putInt("TPZ", this.templatePosition.getZ());
      p_192691_.putString("Template", this.templateName);
   }

   public void postProcess(WorldGenLevel p_192682_, StructureFeatureManager p_192683_, ChunkGenerator p_192684_, Random p_192685_, BoundingBox p_192686_, ChunkPos p_192687_, BlockPos p_192688_) {
      this.placeSettings.setBoundingBox(p_192686_);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (this.template.placeInWorld(p_192682_, this.templatePosition, p_192688_, this.placeSettings, p_192685_, 2)) {
         for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
            if (structuretemplate$structureblockinfo.nbt != null) {
               StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  this.handleDataMarker(structuretemplate$structureblockinfo.nbt.getString("metadata"), structuretemplate$structureblockinfo.pos, p_192682_, p_192685_, p_192686_);
               }
            }
         }

         for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1 : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW)) {
            if (structuretemplate$structureblockinfo1.nbt != null) {
               String s = structuretemplate$structureblockinfo1.nbt.getString("final_state");
               BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);
               BlockState blockstate = Blocks.AIR.defaultBlockState();

               try {
                  blockstateparser.parse(true);
                  BlockState blockstate1 = blockstateparser.getState();
                  if (blockstate1 != null) {
                     blockstate = blockstate1;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, structuretemplate$structureblockinfo1.pos);
                  }
               } catch (CommandSyntaxException commandsyntaxexception) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, structuretemplate$structureblockinfo1.pos);
               }

               p_192682_.setBlock(structuretemplate$structureblockinfo1.pos, blockstate, 3);
            }
         }
      }

   }

   protected abstract void handleDataMarker(String p_73683_, BlockPos p_73684_, ServerLevelAccessor p_73685_, Random p_73686_, BoundingBox p_73687_);

   /** @deprecated */
   @Deprecated
   public void move(int p_73668_, int p_73669_, int p_73670_) {
      super.move(p_73668_, p_73669_, p_73670_);
      this.templatePosition = this.templatePosition.offset(p_73668_, p_73669_, p_73670_);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}