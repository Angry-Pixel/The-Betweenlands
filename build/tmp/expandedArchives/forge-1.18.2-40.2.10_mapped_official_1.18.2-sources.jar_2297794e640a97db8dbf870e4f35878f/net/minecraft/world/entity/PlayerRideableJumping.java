package net.minecraft.world.entity;

public interface PlayerRideableJumping extends PlayerRideable {
   void onPlayerJump(int p_21696_);

   boolean canJump();

   void handleStartJump(int p_21695_);

   void handleStopJump();
}