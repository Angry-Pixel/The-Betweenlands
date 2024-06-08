package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.slf4j.Logger;

public class Pack implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String id;
   private final Supplier<PackResources> supplier;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final Pack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final boolean hidden; // Forge: Allow packs to be hidden from the UI entirely
   private final PackSource packSource;

   @Nullable
   public static Pack create(String p_10431_, boolean p_10432_, Supplier<PackResources> p_10433_, Pack.PackConstructor p_10434_, Pack.Position p_10435_, PackSource p_10436_) {
      try {
         PackResources packresources = p_10433_.get();

         Pack pack;
         label54: {
            try {
               PackMetadataSection packmetadatasection = packresources.getMetadataSection(PackMetadataSection.SERIALIZER);
               if (packmetadatasection != null) {
                  pack = p_10434_.create(p_10431_, new TextComponent(packresources.getName()), p_10432_, p_10433_, packmetadatasection, p_10435_, p_10436_, packresources.isHidden());
                  break label54;
               }

               LOGGER.warn("Couldn't find pack meta for pack {}", (Object)p_10431_);
            } catch (Throwable throwable1) {
               if (packresources != null) {
                  try {
                     packresources.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (packresources != null) {
               packresources.close();
            }

            return null;
         }

         if (packresources != null) {
            packresources.close();
         }

         return pack;
      } catch (IOException ioexception) {
         LOGGER.warn("Couldn't get pack info for: {}", (Object)ioexception.toString());
         return null;
      }
   }

   @Deprecated
   public Pack(String p_10420_, boolean p_10421_, Supplier<PackResources> p_10422_, Component p_10423_, Component p_10424_, PackCompatibility p_10425_, Pack.Position p_10426_, boolean p_10427_, PackSource p_10428_) {
       this(p_10420_, p_10421_, p_10422_, p_10423_, p_10424_, p_10425_, p_10426_, p_10427_, p_10428_, false);
   }

   public Pack(String p_10420_, boolean p_10421_, Supplier<PackResources> p_10422_, Component p_10423_, Component p_10424_, PackCompatibility p_10425_, Pack.Position p_10426_, boolean p_10427_, PackSource p_10428_, boolean hidden) {
      this.id = p_10420_;
      this.supplier = p_10422_;
      this.title = p_10423_;
      this.description = p_10424_;
      this.compatibility = p_10425_;
      this.required = p_10421_;
      this.defaultPosition = p_10426_;
      this.fixedPosition = p_10427_;
      this.packSource = p_10428_;
      this.hidden = hidden;
   }

   @Deprecated
   public Pack(String p_143865_, Component p_143866_, boolean p_143867_, Supplier<PackResources> p_143868_, PackMetadataSection p_143869_, PackType p_143870_, Pack.Position p_143871_, PackSource p_143872_) {
      this(p_143865_, p_143867_, p_143868_, p_143866_, p_143869_.getDescription(), PackCompatibility.forMetadata(p_143869_, p_143870_), p_143871_, false, p_143872_, false);
   }

   public Pack(String p_143865_, Component p_143866_, boolean p_143867_, Supplier<PackResources> p_143868_, PackMetadataSection p_143869_, PackType p_143870_, Pack.Position p_143871_, PackSource p_143872_, boolean hidden) {
      this(p_143865_, p_143867_, p_143868_, p_143866_, p_143869_.getDescription(), PackCompatibility.forMetadata(p_143869_, p_143870_), p_143871_, false, p_143872_, hidden);
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean p_10438_) {
      return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(new TextComponent(this.id))).withStyle((p_10441_) -> {
         return p_10441_.withColor(p_10438_ ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new TextComponent("")).append(this.title).append("\n").append(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public PackResources open() {
      return this.supplier.get();
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public Pack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public PackSource getPackSource() {
      return this.packSource;
   }

   public boolean isHidden() { return hidden; }

   public boolean equals(Object p_10448_) {
      if (this == p_10448_) {
         return true;
      } else if (!(p_10448_ instanceof Pack)) {
         return false;
      } else {
         Pack pack = (Pack)p_10448_;
         return this.id.equals(pack.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public void close() {
   }

   @FunctionalInterface
   public interface PackConstructor {
      @Deprecated
      @Nullable
      default Pack create(String p_143874_, Component p_143875_, boolean p_143876_, Supplier<PackResources> p_143877_, PackMetadataSection p_143878_, Pack.Position p_143879_, PackSource p_143880_)
      {
         return create(p_143874_, p_143875_, p_143876_, p_143877_, p_143878_, p_143879_, p_143880_, false);
      }

      @Nullable
      Pack create(String p_143874_, Component p_143875_, boolean p_143876_, Supplier<PackResources> p_143877_, PackMetadataSection p_143878_, Pack.Position p_143879_, PackSource p_143880_, boolean hidden);
   }

   public static enum Position {
      TOP,
      BOTTOM;

      public <T> int insert(List<T> p_10471_, T p_10472_, Function<T, Pack> p_10473_, boolean p_10474_) {
         Pack.Position pack$position = p_10474_ ? this.opposite() : this;
         if (pack$position == BOTTOM) {
            int j;
            for(j = 0; j < p_10471_.size(); ++j) {
               Pack pack1 = p_10473_.apply(p_10471_.get(j));
               if (!pack1.isFixedPosition() || pack1.getDefaultPosition() != this) {
                  break;
               }
            }

            p_10471_.add(j, p_10472_);
            return j;
         } else {
            int i;
            for(i = p_10471_.size() - 1; i >= 0; --i) {
               Pack pack = p_10473_.apply(p_10471_.get(i));
               if (!pack.isFixedPosition() || pack.getDefaultPosition() != this) {
                  break;
               }
            }

            p_10471_.add(i + 1, p_10472_);
            return i + 1;
         }
      }

      public Pack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }
}
