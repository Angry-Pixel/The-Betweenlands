package thebetweenlands.manual;

import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.herblore.aspects.list.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Bart on 06/12/2015.
 */
public class HLEntryRegistry {

    public static ManualCategory aspectCategory;
    public static ArrayList<Page> aspects = new ArrayList<>();

    public static void init() {
        initAspectEntries();
    }


    public static void initAspectEntries() {
        aspects.clear();
        try {
            for(Field f : AspectManager.class.getDeclaredFields()) {
                if(f.getType() == IAspectType.class) {
                    Object obj = f.get(null);
                    if(obj instanceof IAspectType) {
                        aspects.addAll(PageCreators.AspectPages((IAspectType)obj));
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        aspectCategory = new ManualCategory(aspects, 1);
    }
}
