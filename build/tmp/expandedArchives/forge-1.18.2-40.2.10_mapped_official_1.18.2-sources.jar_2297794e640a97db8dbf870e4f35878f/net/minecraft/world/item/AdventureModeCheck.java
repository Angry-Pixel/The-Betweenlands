package net.minecraft.world.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModeCheck {
   public static final BlockPredicateArgument PREDICATE_PARSER = BlockPredicateArgument.blockPredicate();
   private final String tagName;
   @Nullable
   private BlockInWorld lastCheckedBlock;
   private boolean lastResult;
   private boolean checksBlockEntity;

   public AdventureModeCheck(String p_186327_) {
      this.tagName = p_186327_;
   }

   private static boolean areSameBlocks(BlockInWorld p_186333_, @Nullable BlockInWorld p_186334_, boolean p_186335_) {
      if (p_186334_ != null && p_186333_.getState() == p_186334_.getState()) {
         if (!p_186335_) {
            return true;
         } else if (p_186333_.getEntity() == null && p_186334_.getEntity() == null) {
            return true;
         } else {
            return p_186333_.getEntity() != null && p_186334_.getEntity() != null ? Objects.equals(p_186333_.getEntity().saveWithId(), p_186334_.getEntity().saveWithId()) : false;
         }
      } else {
         return false;
      }
   }

   public boolean test(ItemStack p_204086_, Registry<Block> p_204087_, BlockInWorld p_204088_) {
      if (areSameBlocks(p_204088_, this.lastCheckedBlock, this.checksBlockEntity)) {
         return this.lastResult;
      } else {
         this.lastCheckedBlock = p_204088_;
         this.checksBlockEntity = false;
         CompoundTag compoundtag = p_204086_.getTag();
         if (compoundtag != null && compoundtag.contains(this.tagName, 9)) {
            ListTag listtag = compoundtag.getList(this.tagName, 8);

            for(int i = 0; i < listtag.size(); ++i) {
               String s = listtag.getString(i);

               try {
                  BlockPredicateArgument.Result blockpredicateargument$result = PREDICATE_PARSER.parse(new StringReader(s));
                  this.checksBlockEntity |= blockpredicateargument$result.requiresNbt();
                  Predicate<BlockInWorld> predicate = blockpredicateargument$result.create(p_204087_);
                  if (predicate.test(p_204088_)) {
                     this.lastResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException commandsyntaxexception) {
               }
            }
         }

         this.lastResult = false;
         return false;
      }
   }
}