package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.CommandStorage;

public class StorageDataAccessor implements DataAccessor {
   static final SuggestionProvider<CommandSourceStack> SUGGEST_STORAGE = (p_139547_, p_139548_) -> {
      return SharedSuggestionProvider.suggestResource(getGlobalTags(p_139547_).keys(), p_139548_);
   };
   public static final Function<String, DataCommands.DataProvider> PROVIDER = (p_139554_) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext<CommandSourceStack> p_139570_) {
            return new StorageDataAccessor(StorageDataAccessor.getGlobalTags(p_139570_), ResourceLocationArgument.getId(p_139570_, p_139554_));
         }

         public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> p_139567_, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> p_139568_) {
            return p_139567_.then(Commands.literal("storage").then(p_139568_.apply(Commands.argument(p_139554_, ResourceLocationArgument.id()).suggests(StorageDataAccessor.SUGGEST_STORAGE))));
         }
      };
   };
   private final CommandStorage storage;
   private final ResourceLocation id;

   static CommandStorage getGlobalTags(CommandContext<CommandSourceStack> p_139561_) {
      return p_139561_.getSource().getServer().getCommandStorage();
   }

   StorageDataAccessor(CommandStorage p_139537_, ResourceLocation p_139538_) {
      this.storage = p_139537_;
      this.id = p_139538_;
   }

   public void setData(CompoundTag p_139556_) {
      this.storage.set(this.id, p_139556_);
   }

   public CompoundTag getData() {
      return this.storage.get(this.id);
   }

   public Component getModifiedSuccess() {
      return new TranslatableComponent("commands.data.storage.modified", this.id);
   }

   public Component getPrintSuccess(Tag p_139558_) {
      return new TranslatableComponent("commands.data.storage.query", this.id, NbtUtils.toPrettyComponent(p_139558_));
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath p_139550_, double p_139551_, int p_139552_) {
      return new TranslatableComponent("commands.data.storage.get", p_139550_, this.id, String.format(Locale.ROOT, "%.2f", p_139551_), p_139552_);
   }
}