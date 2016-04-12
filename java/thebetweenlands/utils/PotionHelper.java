package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.potion.Potion;

public class PotionHelper
{
	private static boolean init = false;

	/** adds support for 128 different potions, hopefully this doesn't break mods :I **/
	public static void initPotionArray() {
		if(!init) {
			init = true;
			try {
				Field potionArr = ReflectionHelper.findField(Potion.class, "potionTypes", "field_76425_a", "a");
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
	}
}
