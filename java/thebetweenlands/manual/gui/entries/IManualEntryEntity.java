package thebetweenlands.manual.gui.entries;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all Entities
public interface IManualEntryEntity {

    //If any of these don't have to do with the Entity return null

    String manualPictureLocation();

    String manualStats();

    String manualSpawnDescription();

    String manualLore();

    String manualTrivia();
}
