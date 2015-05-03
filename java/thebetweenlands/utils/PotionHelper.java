package thebetweenlands.utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.potion.Potion;
import thebetweenlands.potion.PotionDecayRestore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PotionHelper
{
    public static PotionDecayRestore decayRestore;

    /** adds support for 128 different potions, hopefully this doesn't break mods :I **/
    public static void initPotionArray() {
        try {
            Field potionArr = ReflectionHelper.findField(Potion.class, "potionTypes", "field_76425_a");
            Field modfield = Field.class.getDeclaredField("modifiers");
            modfield.setAccessible(true);
            modfield.setInt(potionArr, potionArr.getModifiers() & ~Modifier.FINAL);

            Potion[] potionTypes = (Potion[])potionArr.get(null);
            final Potion[] newPotionTypes = new Potion[Math.max(128, potionTypes.length)];
            System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
            potionArr.set(null, newPotionTypes);
        } catch( NoSuchFieldException | IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    public static void registerPotions() {
        decayRestore = new PotionDecayRestore(getFreePotionId());
    }

    private static int getFreePotionId() {
        for( int i = Potion.potionTypes.length - 1; i >= 0; i-- ) {
            if( Potion.potionTypes[i] == null ) {
                return i;
            }
        }

        return -1;
    }
}
