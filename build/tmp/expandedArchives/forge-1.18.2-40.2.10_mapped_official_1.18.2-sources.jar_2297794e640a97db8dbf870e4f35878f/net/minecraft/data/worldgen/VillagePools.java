package net.minecraft.data.worldgen;

public class VillagePools {
   public static void bootstrap() {
      PlainVillagePools.bootstrap();
      SnowyVillagePools.bootstrap();
      SavannaVillagePools.bootstrap();
      DesertVillagePools.bootstrap();
      TaigaVillagePools.bootstrap();
   }
}