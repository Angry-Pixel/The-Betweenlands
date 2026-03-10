package thebetweenlands.api.capability.lifecrystal;

public interface ILifeCrystalHandlerModifiable extends ILifeCrystalHandler {
    /**
     * Overrides the amount of power this life crystal has. This method is
     * not intended for general use, and the handler may throw an error if
     * it is called unexpectedly.
     *
     * @param power The life power to set
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    public void setLifePower(int power);
}
