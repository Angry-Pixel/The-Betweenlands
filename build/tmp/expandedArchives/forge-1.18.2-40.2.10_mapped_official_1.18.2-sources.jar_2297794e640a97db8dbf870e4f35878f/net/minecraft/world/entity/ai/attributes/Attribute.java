package net.minecraft.world.entity.ai.attributes;

public class Attribute extends net.minecraftforge.registries.ForgeRegistryEntry<Attribute> {
   public static final int MAX_NAME_LENGTH = 64;
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;

   protected Attribute(String p_22080_, double p_22081_) {
      this.defaultValue = p_22081_;
      this.descriptionId = p_22080_;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public Attribute setSyncable(boolean p_22085_) {
      this.syncable = p_22085_;
      return this;
   }

   public double sanitizeValue(double p_22083_) {
      return p_22083_;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }
}
