package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public abstract class NbtComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final boolean interpreting;
   protected final Optional<Component> separator;
   protected final String nbtPathPattern;
   @Nullable
   protected final NbtPathArgument.NbtPath compiledNbtPath;

   @Nullable
   private static NbtPathArgument.NbtPath compileNbtPath(String p_130978_) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(p_130978_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public NbtComponent(String p_178454_, boolean p_178455_, Optional<Component> p_178456_) {
      this(p_178454_, compileNbtPath(p_178454_), p_178455_, p_178456_);
   }

   protected NbtComponent(String p_178449_, @Nullable NbtPathArgument.NbtPath p_178450_, boolean p_178451_, Optional<Component> p_178452_) {
      this.nbtPathPattern = p_178449_;
      this.compiledNbtPath = p_178450_;
      this.interpreting = p_178451_;
      this.separator = p_178452_;
   }

   protected abstract Stream<CompoundTag> getData(CommandSourceStack p_130962_) throws CommandSyntaxException;

   public String getNbtPath() {
      return this.nbtPathPattern;
   }

   public boolean isInterpreting() {
      return this.interpreting;
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_130964_, @Nullable Entity p_130965_, int p_130966_) throws CommandSyntaxException {
      if (p_130964_ != null && this.compiledNbtPath != null) {
         Stream<String> stream = this.getData(p_130964_).flatMap((p_130973_) -> {
            try {
               return this.compiledNbtPath.get(p_130973_).stream();
            } catch (CommandSyntaxException commandsyntaxexception) {
               return Stream.empty();
            }
         }).map(Tag::getAsString);
         if (this.interpreting) {
            Component component = DataFixUtils.orElse(ComponentUtils.updateForEntity(p_130964_, this.separator, p_130965_, p_130966_), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);
            return stream.flatMap((p_130971_) -> {
               try {
                  MutableComponent mutablecomponent = Component.Serializer.fromJson(p_130971_);
                  return Stream.of(ComponentUtils.updateForEntity(p_130964_, mutablecomponent, p_130965_, p_130966_));
               } catch (Exception exception) {
                  LOGGER.warn("Failed to parse component: {}", p_130971_, exception);
                  return Stream.of();
               }
            }).reduce((p_178464_, p_178465_) -> {
               return p_178464_.append(component).append(p_178465_);
            }).orElseGet(() -> {
               return new TextComponent("");
            });
         } else {
            return ComponentUtils.updateForEntity(p_130964_, this.separator, p_130965_, p_130966_).map((p_178461_) -> {
               return stream.map((p_178471_) -> {
                  return (net.minecraft.network.chat.MutableComponent)new TextComponent(p_178471_);
               }).reduce((p_178468_, p_178469_) -> {
                  return p_178468_.append(p_178461_).append(p_178469_);
               }).orElseGet(() -> {
                  return new TextComponent("");
               });
            }).orElseGet(() -> {
               return new TextComponent(stream.collect(Collectors.joining(", ")));
            });
         }
      } else {
         return new TextComponent("");
      }
   }

   public static class BlockNbtComponent extends NbtComponent {
      private final String posPattern;
      @Nullable
      private final Coordinates compiledPos;

      public BlockNbtComponent(String p_178482_, boolean p_178483_, String p_178484_, Optional<Component> p_178485_) {
         super(p_178482_, p_178483_, p_178485_);
         this.posPattern = p_178484_;
         this.compiledPos = this.compilePos(this.posPattern);
      }

      @Nullable
      private Coordinates compilePos(String p_130997_) {
         try {
            return BlockPosArgument.blockPos().parse(new StringReader(p_130997_));
         } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
         }
      }

      private BlockNbtComponent(String p_178475_, @Nullable NbtPathArgument.NbtPath p_178476_, boolean p_178477_, String p_178478_, @Nullable Coordinates p_178479_, Optional<Component> p_178480_) {
         super(p_178475_, p_178476_, p_178477_, p_178480_);
         this.posPattern = p_178478_;
         this.compiledPos = p_178479_;
      }

      @Nullable
      public String getPos() {
         return this.posPattern;
      }

      public NbtComponent.BlockNbtComponent plainCopy() {
         return new NbtComponent.BlockNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos, this.separator);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack p_130994_) {
         if (this.compiledPos != null) {
            ServerLevel serverlevel = p_130994_.getLevel();
            BlockPos blockpos = this.compiledPos.getBlockPos(p_130994_);
            if (serverlevel.isLoaded(blockpos)) {
               BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
               if (blockentity != null) {
                  return Stream.of(blockentity.saveWithFullMetadata());
               }
            }
         }

         return Stream.empty();
      }

      public boolean equals(Object p_130999_) {
         if (this == p_130999_) {
            return true;
         } else if (!(p_130999_ instanceof NbtComponent.BlockNbtComponent)) {
            return false;
         } else {
            NbtComponent.BlockNbtComponent nbtcomponent$blocknbtcomponent = (NbtComponent.BlockNbtComponent)p_130999_;
            return Objects.equals(this.posPattern, nbtcomponent$blocknbtcomponent.posPattern) && Objects.equals(this.nbtPathPattern, nbtcomponent$blocknbtcomponent.nbtPathPattern) && super.equals(p_130999_);
         }
      }

      public String toString() {
         return "BlockPosArgument{pos='" + this.posPattern + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
      }
   }

   public static class EntityNbtComponent extends NbtComponent {
      private final String selectorPattern;
      @Nullable
      private final EntitySelector compiledSelector;

      public EntityNbtComponent(String p_178494_, boolean p_178495_, String p_178496_, Optional<Component> p_178497_) {
         super(p_178494_, p_178495_, p_178497_);
         this.selectorPattern = p_178496_;
         this.compiledSelector = compileSelector(p_178496_);
      }

      @Nullable
      private static EntitySelector compileSelector(String p_131020_) {
         try {
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(p_131020_));
            return entityselectorparser.parse();
         } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
         }
      }

      private EntityNbtComponent(String p_178487_, @Nullable NbtPathArgument.NbtPath p_178488_, boolean p_178489_, String p_178490_, @Nullable EntitySelector p_178491_, Optional<Component> p_178492_) {
         super(p_178487_, p_178488_, p_178489_, p_178492_);
         this.selectorPattern = p_178490_;
         this.compiledSelector = p_178491_;
      }

      public String getSelector() {
         return this.selectorPattern;
      }

      public NbtComponent.EntityNbtComponent plainCopy() {
         return new NbtComponent.EntityNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector, this.separator);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack p_131017_) throws CommandSyntaxException {
         if (this.compiledSelector != null) {
            List<? extends Entity> list = this.compiledSelector.findEntities(p_131017_);
            return list.stream().map(NbtPredicate::getEntityTagToCompare);
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object p_131022_) {
         if (this == p_131022_) {
            return true;
         } else if (!(p_131022_ instanceof NbtComponent.EntityNbtComponent)) {
            return false;
         } else {
            NbtComponent.EntityNbtComponent nbtcomponent$entitynbtcomponent = (NbtComponent.EntityNbtComponent)p_131022_;
            return Objects.equals(this.selectorPattern, nbtcomponent$entitynbtcomponent.selectorPattern) && Objects.equals(this.nbtPathPattern, nbtcomponent$entitynbtcomponent.nbtPathPattern) && super.equals(p_131022_);
         }
      }

      public String toString() {
         return "EntityNbtComponent{selector='" + this.selectorPattern + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
      }
   }

   public static class StorageNbtComponent extends NbtComponent {
      private final ResourceLocation id;

      public StorageNbtComponent(String p_178505_, boolean p_178506_, ResourceLocation p_178507_, Optional<Component> p_178508_) {
         super(p_178505_, p_178506_, p_178508_);
         this.id = p_178507_;
      }

      public StorageNbtComponent(String p_178499_, @Nullable NbtPathArgument.NbtPath p_178500_, boolean p_178501_, ResourceLocation p_178502_, Optional<Component> p_178503_) {
         super(p_178499_, p_178500_, p_178501_, p_178503_);
         this.id = p_178502_;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public NbtComponent.StorageNbtComponent plainCopy() {
         return new NbtComponent.StorageNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.id, this.separator);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack p_131038_) {
         CompoundTag compoundtag = p_131038_.getServer().getCommandStorage().get(this.id);
         return Stream.of(compoundtag);
      }

      public boolean equals(Object p_131041_) {
         if (this == p_131041_) {
            return true;
         } else if (!(p_131041_ instanceof NbtComponent.StorageNbtComponent)) {
            return false;
         } else {
            NbtComponent.StorageNbtComponent nbtcomponent$storagenbtcomponent = (NbtComponent.StorageNbtComponent)p_131041_;
            return Objects.equals(this.id, nbtcomponent$storagenbtcomponent.id) && Objects.equals(this.nbtPathPattern, nbtcomponent$storagenbtcomponent.nbtPathPattern) && super.equals(p_131041_);
         }
      }

      public String toString() {
         return "StorageNbtComponent{id='" + this.id + "'path='" + this.nbtPathPattern + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
      }
   }
}