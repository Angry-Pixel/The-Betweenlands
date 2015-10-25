package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.potion.PotionDecayRestore;
import thebetweenlands.potion.PotionPetrify;

public class PotionHelper
{
	private static boolean init = false;

	public static PotionDecayRestore decayRestore;
	public static PotionPetrify petrify;

	/** adds support for 128 different potions, hopefully this doesn't break mods :I **/
	public static void initPotionArray() {
		init = true;
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
		if(!init) initPotionArray();
		decayRestore = new PotionDecayRestore(getFreePotionId());
		petrify = new PotionPetrify(getFreePotionId());
	}

	public static int getFreePotionId() {
		if(!init) initPotionArray();
		for( int i = Potion.potionTypes.length - 1; i >= 0; i-- ) {
			if( Potion.potionTypes[i] == null ) {
				return i;
			}
		}

		return -1;
	}
}
