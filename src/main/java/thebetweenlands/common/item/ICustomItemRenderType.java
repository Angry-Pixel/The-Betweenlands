package thebetweenlands.common.item;

/**
 * Created by Bart on 03/04/2016.
 */
public interface ICustomItemRenderType {
    //something like item/handheld. item/generated is the default
    String getCustomRenderType(int meta);
}
