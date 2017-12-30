package thebetweenlands.common.block;

import java.util.List;

public interface ICustomJsonGenerationBlock {

    String getBlockStateText();

    String getBlockModelText(int meta);

    String getFileNameFromMeta(int meta);

    void getMetas(List<Integer> list);

    String getBlockModelForItem();
}
