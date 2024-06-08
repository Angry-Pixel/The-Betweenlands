package net.minecraft.world.inventory;

public enum RecipeBookType implements net.minecraftforge.common.IExtensibleEnum {
   CRAFTING,
   FURNACE,
   BLAST_FURNACE,
   SMOKER;

   public static RecipeBookType create(String name) {
      throw new IllegalStateException("Enum not extended!");
   }

   @Override
   public void init() {
      String name = this.name().toLowerCase(java.util.Locale.ROOT).replace("_","");
      net.minecraft.stats.RecipeBookSettings.addTagsForType(this, "is" + name + "GuiOpen", "is" + name + "FilteringCraftable");
   }
}
