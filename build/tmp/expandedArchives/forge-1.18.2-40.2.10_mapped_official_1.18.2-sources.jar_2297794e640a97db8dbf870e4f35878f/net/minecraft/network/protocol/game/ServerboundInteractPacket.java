package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ServerboundInteractPacket implements Packet<ServerGamePacketListener> {
   private final int entityId;
   private final ServerboundInteractPacket.Action action;
   private final boolean usingSecondaryAction;
   static final ServerboundInteractPacket.Action ATTACK_ACTION = new ServerboundInteractPacket.Action() {
      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.ATTACK;
      }

      public void dispatch(ServerboundInteractPacket.Handler p_179624_) {
         p_179624_.onAttack();
      }

      public void write(FriendlyByteBuf p_179622_) {
      }
   };

   private ServerboundInteractPacket(int p_179598_, boolean p_179599_, ServerboundInteractPacket.Action p_179600_) {
      this.entityId = p_179598_;
      this.action = p_179600_;
      this.usingSecondaryAction = p_179599_;
   }

   public static ServerboundInteractPacket createAttackPacket(Entity p_179606_, boolean p_179607_) {
      return new ServerboundInteractPacket(p_179606_.getId(), p_179607_, ATTACK_ACTION);
   }

   public static ServerboundInteractPacket createInteractionPacket(Entity p_179609_, boolean p_179610_, InteractionHand p_179611_) {
      return new ServerboundInteractPacket(p_179609_.getId(), p_179610_, new ServerboundInteractPacket.InteractionAction(p_179611_));
   }

   public static ServerboundInteractPacket createInteractionPacket(Entity p_179613_, boolean p_179614_, InteractionHand p_179615_, Vec3 p_179616_) {
      return new ServerboundInteractPacket(p_179613_.getId(), p_179614_, new ServerboundInteractPacket.InteractionAtLocationAction(p_179615_, p_179616_));
   }

   public ServerboundInteractPacket(FriendlyByteBuf p_179602_) {
      this.entityId = p_179602_.readVarInt();
      ServerboundInteractPacket.ActionType serverboundinteractpacket$actiontype = p_179602_.readEnum(ServerboundInteractPacket.ActionType.class);
      this.action = serverboundinteractpacket$actiontype.reader.apply(p_179602_);
      this.usingSecondaryAction = p_179602_.readBoolean();
   }

   public void write(FriendlyByteBuf p_134058_) {
      p_134058_.writeVarInt(this.entityId);
      p_134058_.writeEnum(this.action.getType());
      this.action.write(p_134058_);
      p_134058_.writeBoolean(this.usingSecondaryAction);
   }

   public void handle(ServerGamePacketListener p_134055_) {
      p_134055_.handleInteract(this);
   }

   @Nullable
   public Entity getTarget(ServerLevel p_179604_) {
      return p_179604_.getEntityOrPart(this.entityId);
   }

   public boolean isUsingSecondaryAction() {
      return this.usingSecondaryAction;
   }

   public void dispatch(ServerboundInteractPacket.Handler p_179618_) {
      this.action.dispatch(p_179618_);
   }

   interface Action {
      ServerboundInteractPacket.ActionType getType();

      void dispatch(ServerboundInteractPacket.Handler p_179626_);

      void write(FriendlyByteBuf p_179625_);
   }

   static enum ActionType {
      INTERACT(ServerboundInteractPacket.InteractionAction::new),
      ATTACK((p_179639_) -> {
         return ServerboundInteractPacket.ATTACK_ACTION;
      }),
      INTERACT_AT(ServerboundInteractPacket.InteractionAtLocationAction::new);

      final Function<FriendlyByteBuf, ServerboundInteractPacket.Action> reader;

      private ActionType(Function<FriendlyByteBuf, ServerboundInteractPacket.Action> p_179636_) {
         this.reader = p_179636_;
      }
   }

   public interface Handler {
      void onInteraction(InteractionHand p_179643_);

      void onInteraction(InteractionHand p_179644_, Vec3 p_179645_);

      void onAttack();
   }

   static class InteractionAction implements ServerboundInteractPacket.Action {
      private final InteractionHand hand;

      InteractionAction(InteractionHand p_179648_) {
         this.hand = p_179648_;
      }

      private InteractionAction(FriendlyByteBuf p_179650_) {
         this.hand = p_179650_.readEnum(InteractionHand.class);
      }

      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.INTERACT;
      }

      public void dispatch(ServerboundInteractPacket.Handler p_179655_) {
         p_179655_.onInteraction(this.hand);
      }

      public void write(FriendlyByteBuf p_179653_) {
         p_179653_.writeEnum(this.hand);
      }
   }

   static class InteractionAtLocationAction implements ServerboundInteractPacket.Action {
      private final InteractionHand hand;
      private final Vec3 location;

      InteractionAtLocationAction(InteractionHand p_179659_, Vec3 p_179660_) {
         this.hand = p_179659_;
         this.location = p_179660_;
      }

      private InteractionAtLocationAction(FriendlyByteBuf p_179662_) {
         this.location = new Vec3((double)p_179662_.readFloat(), (double)p_179662_.readFloat(), (double)p_179662_.readFloat());
         this.hand = p_179662_.readEnum(InteractionHand.class);
      }

      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.INTERACT_AT;
      }

      public void dispatch(ServerboundInteractPacket.Handler p_179667_) {
         p_179667_.onInteraction(this.hand, this.location);
      }

      public void write(FriendlyByteBuf p_179665_) {
         p_179665_.writeFloat((float)this.location.x);
         p_179665_.writeFloat((float)this.location.y);
         p_179665_.writeFloat((float)this.location.z);
         p_179665_.writeEnum(this.hand);
      }
   }
}