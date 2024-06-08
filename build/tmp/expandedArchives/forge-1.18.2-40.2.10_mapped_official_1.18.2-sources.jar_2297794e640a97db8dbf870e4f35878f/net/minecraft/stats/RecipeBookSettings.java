package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS = new java.util.HashMap<>(ImmutableMap.of(RecipeBookType.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookType.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookType.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookType.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable")));
   private final Map<RecipeBookType, RecipeBookSettings.TypeSettings> states;

   private RecipeBookSettings(Map<RecipeBookType, RecipeBookSettings.TypeSettings> p_12730_) {
      this.states = p_12730_;
   }

   public RecipeBookSettings() {
      this(Util.make(Maps.newEnumMap(RecipeBookType.class), (p_12740_) -> {
         for(RecipeBookType recipebooktype : RecipeBookType.values()) {
            p_12740_.put(recipebooktype, new RecipeBookSettings.TypeSettings(false, false));
         }

      }));
   }

   public boolean isOpen(RecipeBookType p_12735_) {
      return (this.states.get(p_12735_)).open;
   }

   public void setOpen(RecipeBookType p_12737_, boolean p_12738_) {
      (this.states.get(p_12737_)).open = p_12738_;
   }

   public boolean isFiltering(RecipeBookType p_12755_) {
      return (this.states.get(p_12755_)).filtering;
   }

   public void setFiltering(RecipeBookType p_12757_, boolean p_12758_) {
      (this.states.get(p_12757_)).filtering = p_12758_;
   }

   public static RecipeBookSettings read(FriendlyByteBuf p_12753_) {
      Map<RecipeBookType, RecipeBookSettings.TypeSettings> map = Maps.newEnumMap(RecipeBookType.class);

      for(RecipeBookType recipebooktype : RecipeBookType.values()) {
         boolean flag = p_12753_.readBoolean();
         boolean flag1 = p_12753_.readBoolean();
         map.put(recipebooktype, new RecipeBookSettings.TypeSettings(flag, flag1));
      }

      return new RecipeBookSettings(map);
   }

   public void write(FriendlyByteBuf p_12762_) {
      for(RecipeBookType recipebooktype : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings recipebooksettings$typesettings = this.states.get(recipebooktype);
         if (recipebooksettings$typesettings == null) {
            p_12762_.writeBoolean(false);
            p_12762_.writeBoolean(false);
         } else {
            p_12762_.writeBoolean(recipebooksettings$typesettings.open);
            p_12762_.writeBoolean(recipebooksettings$typesettings.filtering);
         }
      }

   }

   public static RecipeBookSettings read(CompoundTag p_12742_) {
      Map<RecipeBookType, RecipeBookSettings.TypeSettings> map = Maps.newEnumMap(RecipeBookType.class);
      TAG_FIELDS.forEach((p_12750_, p_12751_) -> {
         boolean flag = p_12742_.getBoolean(p_12751_.getFirst());
         boolean flag1 = p_12742_.getBoolean(p_12751_.getSecond());
         map.put(p_12750_, new RecipeBookSettings.TypeSettings(flag, flag1));
      });
      return new RecipeBookSettings(map);
   }

   public void write(CompoundTag p_12760_) {
      TAG_FIELDS.forEach((p_12745_, p_12746_) -> {
         RecipeBookSettings.TypeSettings recipebooksettings$typesettings = this.states.get(p_12745_);
         p_12760_.putBoolean(p_12746_.getFirst(), recipebooksettings$typesettings.open);
         p_12760_.putBoolean(p_12746_.getSecond(), recipebooksettings$typesettings.filtering);
      });
   }

   public RecipeBookSettings copy() {
      Map<RecipeBookType, RecipeBookSettings.TypeSettings> map = Maps.newEnumMap(RecipeBookType.class);

      for(RecipeBookType recipebooktype : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings recipebooksettings$typesettings = this.states.get(recipebooktype);
         map.put(recipebooktype, recipebooksettings$typesettings.copy());
      }

      return new RecipeBookSettings(map);
   }

   public void replaceFrom(RecipeBookSettings p_12733_) {
      this.states.clear();

      for(RecipeBookType recipebooktype : RecipeBookType.values()) {
         RecipeBookSettings.TypeSettings recipebooksettings$typesettings = p_12733_.states.get(recipebooktype);
         this.states.put(recipebooktype, recipebooksettings$typesettings.copy());
      }

   }

   public boolean equals(Object p_12764_) {
      return this == p_12764_ || p_12764_ instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings)p_12764_).states);
   }

   public int hashCode() {
      return this.states.hashCode();
   }

   static final class TypeSettings {
      boolean open;
      boolean filtering;

      public TypeSettings(boolean p_12769_, boolean p_12770_) {
         this.open = p_12769_;
         this.filtering = p_12770_;
      }

      public RecipeBookSettings.TypeSettings copy() {
         return new RecipeBookSettings.TypeSettings(this.open, this.filtering);
      }

      public boolean equals(Object p_12783_) {
         if (this == p_12783_) {
            return true;
         } else if (!(p_12783_ instanceof RecipeBookSettings.TypeSettings)) {
            return false;
         } else {
            RecipeBookSettings.TypeSettings recipebooksettings$typesettings = (RecipeBookSettings.TypeSettings)p_12783_;
            return this.open == recipebooksettings$typesettings.open && this.filtering == recipebooksettings$typesettings.filtering;
         }
      }

      public int hashCode() {
         int i = this.open ? 1 : 0;
         return 31 * i + (this.filtering ? 1 : 0);
      }

      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + "]";
      }
   }
   //FORGE -- called automatically on Enum creation - used for serialization
   public static void addTagsForType(RecipeBookType type, String openTag, String filteringTag) {
      TAG_FIELDS.put(type, Pair.of(openTag, filteringTag));
   }
}
