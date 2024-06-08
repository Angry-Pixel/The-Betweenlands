package net.minecraft.world.level.border;

public interface BorderChangeListener {
   void onBorderSizeSet(WorldBorder p_61847_, double p_61848_);

   void onBorderSizeLerping(WorldBorder p_61852_, double p_61853_, double p_61854_, long p_61855_);

   void onBorderCenterSet(WorldBorder p_61849_, double p_61850_, double p_61851_);

   void onBorderSetWarningTime(WorldBorder p_61856_, int p_61857_);

   void onBorderSetWarningBlocks(WorldBorder p_61860_, int p_61861_);

   void onBorderSetDamagePerBlock(WorldBorder p_61858_, double p_61859_);

   void onBorderSetDamageSafeZOne(WorldBorder p_61862_, double p_61863_);

   public static class DelegateBorderChangeListener implements BorderChangeListener {
      private final WorldBorder worldBorder;

      public DelegateBorderChangeListener(WorldBorder p_61866_) {
         this.worldBorder = p_61866_;
      }

      public void onBorderSizeSet(WorldBorder p_61868_, double p_61869_) {
         this.worldBorder.setSize(p_61869_);
      }

      public void onBorderSizeLerping(WorldBorder p_61875_, double p_61876_, double p_61877_, long p_61878_) {
         this.worldBorder.lerpSizeBetween(p_61876_, p_61877_, p_61878_);
      }

      public void onBorderCenterSet(WorldBorder p_61871_, double p_61872_, double p_61873_) {
         this.worldBorder.setCenter(p_61872_, p_61873_);
      }

      public void onBorderSetWarningTime(WorldBorder p_61880_, int p_61881_) {
         this.worldBorder.setWarningTime(p_61881_);
      }

      public void onBorderSetWarningBlocks(WorldBorder p_61886_, int p_61887_) {
         this.worldBorder.setWarningBlocks(p_61887_);
      }

      public void onBorderSetDamagePerBlock(WorldBorder p_61883_, double p_61884_) {
         this.worldBorder.setDamagePerBlock(p_61884_);
      }

      public void onBorderSetDamageSafeZOne(WorldBorder p_61889_, double p_61890_) {
         this.worldBorder.setDamageSafeZone(p_61890_);
      }
   }
}