package thebetweenlands.manual.gui.entries;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all world gen thingies
public interface IManualEntryWorldGeneration {

    //If any of these don't have to do with the worldgen return null
    String manualPictureLocation();

    String manualSpawnDescription();

    String manualLore();

    String manualTrivia();
}
