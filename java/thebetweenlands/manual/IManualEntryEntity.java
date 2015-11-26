package thebetweenlands.manual;

import net.minecraft.entity.Entity;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all Entities
public interface IManualEntryEntity {

    //If any of these don't have to do with the Entity return null

    String manualPictureLocation();
    int pictureWidth();
    int pictureHeight();

    String manualStats();


    //split at '/'
    String manualName();

    //don't null plz
    Entity getEntity();
}
