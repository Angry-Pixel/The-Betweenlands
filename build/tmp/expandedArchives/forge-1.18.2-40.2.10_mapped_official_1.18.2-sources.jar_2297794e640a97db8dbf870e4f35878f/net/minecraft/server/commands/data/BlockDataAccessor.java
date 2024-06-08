package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDataAccessor implements DataAccessor {
   static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.block.invalid"));
   public static final Function<String, DataCommands.DataProvider> PROVIDER = (p_139305_) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext<CommandSourceStack> p_139319_) throws CommandSyntaxException {
            BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(p_139319_, p_139305_ + "Pos");
            BlockEntity blockentity = p_139319_.getSource().getLevel().getBlockEntity(blockpos);
            if (blockentity == null) {
               throw BlockDataAccessor.ERROR_NOT_A_BLOCK_ENTITY.create();
            } else {
               return new BlockDataAccessor(blockentity, blockpos);
            }
         }

         public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> p_139316_, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> p_139317_) {
            return p_139316_.then(Commands.literal("block").then(p_139317_.apply(Commands.argument(p_139305_ + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final BlockEntity entity;
   private final BlockPos pos;

   public BlockDataAccessor(BlockEntity p_139297_, BlockPos p_139298_) {
      this.entity = p_139297_;
      this.pos = p_139298_;
   }

   public void setData(CompoundTag p_139307_) {
      BlockState blockstate = this.entity.getLevel().getBlockState(this.pos);
      this.entity.load(p_139307_);
      this.entity.setChanged();
      this.entity.getLevel().sendBlockUpdated(this.pos, blockstate, blockstate, 3);
   }

   public CompoundTag getData() {
      return this.entity.saveWithFullMetadata();
   }

   public Component getModifiedSuccess() {
      return new TranslatableComponent("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public Component getPrintSuccess(Tag p_139309_) {
      return new TranslatableComponent("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), NbtUtils.toPrettyComponent(p_139309_));
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath p_139301_, double p_139302_, int p_139303_) {
      return new TranslatableComponent("commands.data.block.get", p_139301_, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", p_139302_), p_139303_);
   }
}