package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final MerchantOffers offers;
   private final int villagerLevel;
   private final int villagerXp;
   private final boolean showProgress;
   private final boolean canRestock;

   public ClientboundMerchantOffersPacket(int p_132456_, MerchantOffers p_132457_, int p_132458_, int p_132459_, boolean p_132460_, boolean p_132461_) {
      this.containerId = p_132456_;
      this.offers = p_132457_;
      this.villagerLevel = p_132458_;
      this.villagerXp = p_132459_;
      this.showProgress = p_132460_;
      this.canRestock = p_132461_;
   }

   public ClientboundMerchantOffersPacket(FriendlyByteBuf p_178985_) {
      this.containerId = p_178985_.readVarInt();
      this.offers = MerchantOffers.createFromStream(p_178985_);
      this.villagerLevel = p_178985_.readVarInt();
      this.villagerXp = p_178985_.readVarInt();
      this.showProgress = p_178985_.readBoolean();
      this.canRestock = p_178985_.readBoolean();
   }

   public void write(FriendlyByteBuf p_132470_) {
      p_132470_.writeVarInt(this.containerId);
      this.offers.writeToStream(p_132470_);
      p_132470_.writeVarInt(this.villagerLevel);
      p_132470_.writeVarInt(this.villagerXp);
      p_132470_.writeBoolean(this.showProgress);
      p_132470_.writeBoolean(this.canRestock);
   }

   public void handle(ClientGamePacketListener p_132467_) {
      p_132467_.handleMerchantOffers(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public int getVillagerLevel() {
      return this.villagerLevel;
   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public boolean showProgress() {
      return this.showProgress;
   }

   public boolean canRestock() {
      return this.canRestock;
   }
}