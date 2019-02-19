package thebetweenlands.common.advancments;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPredicate {

    public static final BlockPredicate ANY = new BlockPredicate();
    private final Block block;
    private final Map<IProperty<?>, Object > map;

    public BlockPredicate() {
        block = null;
        map = null;
    }

    public BlockPredicate(Block block, Map<IProperty<?>, Object> map) {
        this.block = block;
        this.map = map;
    }

    public boolean test(IBlockState state) {
        if (this.block != null && state.getBlock() != this.block) {
            return false;
        } else {
            if (this.map != null) {
                for (Map.Entry< IProperty<?>, Object > entry : this.map.entrySet()) {
                    if (state.get(entry.getKey()) != entry.getValue()) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static BlockPredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject json = JsonUtils.getJsonObject(element, "block");

            Block block = null;
            Map <IProperty<?>, Object > map = null;

            if (json.has("block")) {
                ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "block"));

                
                
                if (!ForgeRegistries.BLOCKS.containsKey(resourcelocation)) {
                    throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
                }

                block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
            }

            if (json.has("state")) {
                if (block == null) {
                    throw new JsonSyntaxException("Can't define block state without a specific block type");
                }

                StateContainer<Block, IBlockState> stateContainer = block.getStateContainer();

                for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "state").entrySet()) {
                    IProperty<?> property = stateContainer.getProperty(entry.getKey());

                    if (property == null) {
                        throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                    }

                    String s = JsonUtils.getString(entry.getValue(), entry.getKey());
                    Optional<?> optional = property.parseValue(s);

                    if (!optional.isPresent()) {
                        throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + entry.getKey() + "' on block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                    }

                    if (map == null) {
                        map = Maps.newHashMap();
                    }

                    map.put(property, optional.get());
                }
            }

            return new BlockPredicate(block, map);
        } else {
            return ANY;
        }
    }

    public static BlockPredicate[] deserializeArray(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonArray jsonarray = JsonUtils.getJsonArray(element, "blocks");
            BlockPredicate[] aitempredicate = new BlockPredicate[jsonarray.size()];

            for (int i = 0; i < aitempredicate.length; ++i) {
                aitempredicate[i] = deserialize(jsonarray.get(i));
            }

            return aitempredicate;
        } else {
            return new BlockPredicate[0];
        }
    }
}
