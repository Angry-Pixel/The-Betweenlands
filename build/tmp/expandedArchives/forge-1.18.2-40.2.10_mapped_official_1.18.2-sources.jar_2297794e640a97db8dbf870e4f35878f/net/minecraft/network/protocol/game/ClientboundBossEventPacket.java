package net.minecraft.network.protocol.game;

import java.util.UUID;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.BossEvent;

public class ClientboundBossEventPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_DARKEN = 1;
   private static final int FLAG_MUSIC = 2;
   private static final int FLAG_FOG = 4;
   private final UUID id;
   private final ClientboundBossEventPacket.Operation operation;
   static final ClientboundBossEventPacket.Operation REMOVE_OPERATION = new ClientboundBossEventPacket.Operation() {
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.REMOVE;
      }

      public void dispatch(UUID p_178660_, ClientboundBossEventPacket.Handler p_178661_) {
         p_178661_.remove(p_178660_);
      }

      public void write(FriendlyByteBuf p_178663_) {
      }
   };

   private ClientboundBossEventPacket(UUID p_178635_, ClientboundBossEventPacket.Operation p_178636_) {
      this.id = p_178635_;
      this.operation = p_178636_;
   }

   public ClientboundBossEventPacket(FriendlyByteBuf p_178638_) {
      this.id = p_178638_.readUUID();
      ClientboundBossEventPacket.OperationType clientboundbosseventpacket$operationtype = p_178638_.readEnum(ClientboundBossEventPacket.OperationType.class);
      this.operation = clientboundbosseventpacket$operationtype.reader.apply(p_178638_);
   }

   public static ClientboundBossEventPacket createAddPacket(BossEvent p_178640_) {
      return new ClientboundBossEventPacket(p_178640_.getId(), new ClientboundBossEventPacket.AddOperation(p_178640_));
   }

   public static ClientboundBossEventPacket createRemovePacket(UUID p_178642_) {
      return new ClientboundBossEventPacket(p_178642_, REMOVE_OPERATION);
   }

   public static ClientboundBossEventPacket createUpdateProgressPacket(BossEvent p_178650_) {
      return new ClientboundBossEventPacket(p_178650_.getId(), new ClientboundBossEventPacket.UpdateProgressOperation(p_178650_.getProgress()));
   }

   public static ClientboundBossEventPacket createUpdateNamePacket(BossEvent p_178652_) {
      return new ClientboundBossEventPacket(p_178652_.getId(), new ClientboundBossEventPacket.UpdateNameOperation(p_178652_.getName()));
   }

   public static ClientboundBossEventPacket createUpdateStylePacket(BossEvent p_178654_) {
      return new ClientboundBossEventPacket(p_178654_.getId(), new ClientboundBossEventPacket.UpdateStyleOperation(p_178654_.getColor(), p_178654_.getOverlay()));
   }

   public static ClientboundBossEventPacket createUpdatePropertiesPacket(BossEvent p_178656_) {
      return new ClientboundBossEventPacket(p_178656_.getId(), new ClientboundBossEventPacket.UpdatePropertiesOperation(p_178656_.shouldDarkenScreen(), p_178656_.shouldPlayBossMusic(), p_178656_.shouldCreateWorldFog()));
   }

   public void write(FriendlyByteBuf p_131773_) {
      p_131773_.writeUUID(this.id);
      p_131773_.writeEnum(this.operation.getType());
      this.operation.write(p_131773_);
   }

   static int encodeProperties(boolean p_178646_, boolean p_178647_, boolean p_178648_) {
      int i = 0;
      if (p_178646_) {
         i |= 1;
      }

      if (p_178647_) {
         i |= 2;
      }

      if (p_178648_) {
         i |= 4;
      }

      return i;
   }

   public void handle(ClientGamePacketListener p_131770_) {
      p_131770_.handleBossUpdate(this);
   }

   public void dispatch(ClientboundBossEventPacket.Handler p_178644_) {
      this.operation.dispatch(this.id, p_178644_);
   }

   static class AddOperation implements ClientboundBossEventPacket.Operation {
      private final Component name;
      private final float progress;
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      AddOperation(BossEvent p_178672_) {
         this.name = p_178672_.getName();
         this.progress = p_178672_.getProgress();
         this.color = p_178672_.getColor();
         this.overlay = p_178672_.getOverlay();
         this.darkenScreen = p_178672_.shouldDarkenScreen();
         this.playMusic = p_178672_.shouldPlayBossMusic();
         this.createWorldFog = p_178672_.shouldCreateWorldFog();
      }

      private AddOperation(FriendlyByteBuf p_178674_) {
         this.name = p_178674_.readComponent();
         this.progress = p_178674_.readFloat();
         this.color = p_178674_.readEnum(BossEvent.BossBarColor.class);
         this.overlay = p_178674_.readEnum(BossEvent.BossBarOverlay.class);
         int i = p_178674_.readUnsignedByte();
         this.darkenScreen = (i & 1) > 0;
         this.playMusic = (i & 2) > 0;
         this.createWorldFog = (i & 4) > 0;
      }

      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.ADD;
      }

      public void dispatch(UUID p_178677_, ClientboundBossEventPacket.Handler p_178678_) {
         p_178678_.add(p_178677_, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(FriendlyByteBuf p_178680_) {
         p_178680_.writeComponent(this.name);
         p_178680_.writeFloat(this.progress);
         p_178680_.writeEnum(this.color);
         p_178680_.writeEnum(this.overlay);
         p_178680_.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   public interface Handler {
      default void add(UUID p_178689_, Component p_178690_, float p_178691_, BossEvent.BossBarColor p_178692_, BossEvent.BossBarOverlay p_178693_, boolean p_178694_, boolean p_178695_, boolean p_178696_) {
      }

      default void remove(UUID p_178681_) {
      }

      default void updateProgress(UUID p_178682_, float p_178683_) {
      }

      default void updateName(UUID p_178687_, Component p_178688_) {
      }

      default void updateStyle(UUID p_178684_, BossEvent.BossBarColor p_178685_, BossEvent.BossBarOverlay p_178686_) {
      }

      default void updateProperties(UUID p_178697_, boolean p_178698_, boolean p_178699_, boolean p_178700_) {
      }
   }

   interface Operation {
      ClientboundBossEventPacket.OperationType getType();

      void dispatch(UUID p_178701_, ClientboundBossEventPacket.Handler p_178702_);

      void write(FriendlyByteBuf p_178703_);
   }

   static enum OperationType {
      ADD(ClientboundBossEventPacket.AddOperation::new),
      REMOVE((p_178719_) -> {
         return ClientboundBossEventPacket.REMOVE_OPERATION;
      }),
      UPDATE_PROGRESS(ClientboundBossEventPacket.UpdateProgressOperation::new),
      UPDATE_NAME(ClientboundBossEventPacket.UpdateNameOperation::new),
      UPDATE_STYLE(ClientboundBossEventPacket.UpdateStyleOperation::new),
      UPDATE_PROPERTIES(ClientboundBossEventPacket.UpdatePropertiesOperation::new);

      final Function<FriendlyByteBuf, ClientboundBossEventPacket.Operation> reader;

      private OperationType(Function<FriendlyByteBuf, ClientboundBossEventPacket.Operation> p_178716_) {
         this.reader = p_178716_;
      }
   }

   static class UpdateNameOperation implements ClientboundBossEventPacket.Operation {
      private final Component name;

      UpdateNameOperation(Component p_178727_) {
         this.name = p_178727_;
      }

      private UpdateNameOperation(FriendlyByteBuf p_178725_) {
         this.name = p_178725_.readComponent();
      }

      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_NAME;
      }

      public void dispatch(UUID p_178730_, ClientboundBossEventPacket.Handler p_178731_) {
         p_178731_.updateName(p_178730_, this.name);
      }

      public void write(FriendlyByteBuf p_178733_) {
         p_178733_.writeComponent(this.name);
      }
   }

   static class UpdateProgressOperation implements ClientboundBossEventPacket.Operation {
      private final float progress;

      UpdateProgressOperation(float p_178736_) {
         this.progress = p_178736_;
      }

      private UpdateProgressOperation(FriendlyByteBuf p_178738_) {
         this.progress = p_178738_.readFloat();
      }

      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROGRESS;
      }

      public void dispatch(UUID p_178741_, ClientboundBossEventPacket.Handler p_178742_) {
         p_178742_.updateProgress(p_178741_, this.progress);
      }

      public void write(FriendlyByteBuf p_178744_) {
         p_178744_.writeFloat(this.progress);
      }
   }

   static class UpdatePropertiesOperation implements ClientboundBossEventPacket.Operation {
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      UpdatePropertiesOperation(boolean p_178751_, boolean p_178752_, boolean p_178753_) {
         this.darkenScreen = p_178751_;
         this.playMusic = p_178752_;
         this.createWorldFog = p_178753_;
      }

      private UpdatePropertiesOperation(FriendlyByteBuf p_178749_) {
         int i = p_178749_.readUnsignedByte();
         this.darkenScreen = (i & 1) > 0;
         this.playMusic = (i & 2) > 0;
         this.createWorldFog = (i & 4) > 0;
      }

      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROPERTIES;
      }

      public void dispatch(UUID p_178756_, ClientboundBossEventPacket.Handler p_178757_) {
         p_178757_.updateProperties(p_178756_, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(FriendlyByteBuf p_178759_) {
         p_178759_.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   static class UpdateStyleOperation implements ClientboundBossEventPacket.Operation {
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;

      UpdateStyleOperation(BossEvent.BossBarColor p_178763_, BossEvent.BossBarOverlay p_178764_) {
         this.color = p_178763_;
         this.overlay = p_178764_;
      }

      private UpdateStyleOperation(FriendlyByteBuf p_178766_) {
         this.color = p_178766_.readEnum(BossEvent.BossBarColor.class);
         this.overlay = p_178766_.readEnum(BossEvent.BossBarOverlay.class);
      }

      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_STYLE;
      }

      public void dispatch(UUID p_178769_, ClientboundBossEventPacket.Handler p_178770_) {
         p_178770_.updateStyle(p_178769_, this.color, this.overlay);
      }

      public void write(FriendlyByteBuf p_178772_) {
         p_178772_.writeEnum(this.color);
         p_178772_.writeEnum(this.overlay);
      }
   }
}