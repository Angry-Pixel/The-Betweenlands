package net.minecraft.network.protocol.game;

public interface ServerGamePacketListener extends ServerPacketListener {
   void handleAnimate(ServerboundSwingPacket p_133781_);

   void handleChat(ServerboundChatPacket p_133743_);

   void handleClientCommand(ServerboundClientCommandPacket p_133744_);

   void handleClientInformation(ServerboundClientInformationPacket p_133745_);

   void handleContainerButtonClick(ServerboundContainerButtonClickPacket p_133748_);

   void handleContainerClick(ServerboundContainerClickPacket p_133749_);

   void handlePlaceRecipe(ServerboundPlaceRecipePacket p_133762_);

   void handleContainerClose(ServerboundContainerClosePacket p_133750_);

   void handleCustomPayload(ServerboundCustomPayloadPacket p_133751_);

   void handleInteract(ServerboundInteractPacket p_133754_);

   void handleKeepAlive(ServerboundKeepAlivePacket p_133756_);

   void handleMovePlayer(ServerboundMovePlayerPacket p_133758_);

   void handlePong(ServerboundPongPacket p_179536_);

   void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket p_133763_);

   void handlePlayerAction(ServerboundPlayerActionPacket p_133764_);

   void handlePlayerCommand(ServerboundPlayerCommandPacket p_133765_);

   void handlePlayerInput(ServerboundPlayerInputPacket p_133766_);

   void handleSetCarriedItem(ServerboundSetCarriedItemPacket p_133774_);

   void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket p_133777_);

   void handleSignUpdate(ServerboundSignUpdatePacket p_133780_);

   void handleUseItemOn(ServerboundUseItemOnPacket p_133783_);

   void handleUseItem(ServerboundUseItemPacket p_133784_);

   void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket p_133782_);

   void handleResourcePackResponse(ServerboundResourcePackPacket p_133770_);

   void handlePaddleBoat(ServerboundPaddleBoatPacket p_133760_);

   void handleMoveVehicle(ServerboundMoveVehiclePacket p_133759_);

   void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket p_133740_);

   void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket p_133768_);

   void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket p_133767_);

   void handleSeenAdvancements(ServerboundSeenAdvancementsPacket p_133771_);

   void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket p_133746_);

   void handleSetCommandBlock(ServerboundSetCommandBlockPacket p_133775_);

   void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket p_133776_);

   void handlePickItem(ServerboundPickItemPacket p_133761_);

   void handleRenameItem(ServerboundRenameItemPacket p_133769_);

   void handleSetBeaconPacket(ServerboundSetBeaconPacket p_133773_);

   void handleSetStructureBlock(ServerboundSetStructureBlockPacket p_133779_);

   void handleSelectTrade(ServerboundSelectTradePacket p_133772_);

   void handleEditBook(ServerboundEditBookPacket p_133752_);

   void handleEntityTagQuery(ServerboundEntityTagQuery p_133753_);

   void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery p_133741_);

   void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket p_133778_);

   void handleJigsawGenerate(ServerboundJigsawGeneratePacket p_133755_);

   void handleChangeDifficulty(ServerboundChangeDifficultyPacket p_133742_);

   void handleLockDifficulty(ServerboundLockDifficultyPacket p_133757_);
}