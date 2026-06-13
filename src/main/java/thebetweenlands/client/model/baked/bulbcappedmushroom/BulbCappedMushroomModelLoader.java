package thebetweenlands.client.model.baked.bulbcappedmushroom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BulbCappedMushroomModelLoader implements IGeometryLoader<UnbakedBulbCappedMushroomModel> {
public static final BulbCappedMushroomModelLoader INSTANCE = new BulbCappedMushroomModelLoader();

@Override
public UnbakedBulbCappedMushroomModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
return new UnbakedBulbCappedMushroomModel();
}
}

