package thebetweenlands.manual.gui;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all blocks
public interface IManualEntryBlock {

    //If any of these don't have to do with the block return null
    String manualStats();

    String manualLore();

    String manualTrivia();
}
