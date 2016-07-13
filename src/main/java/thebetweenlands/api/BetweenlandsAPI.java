package thebetweenlands.api;

public class BetweenlandsAPI {

    public interface IBetweenlandsAPI {

        //METHODS GO IN THIS INTERFACE


    }

    public static IBetweenlandsAPI instance;

    public static IBetweenlandsAPI getInstance(){
        return instance;
    }

    public static void init(IBetweenlandsAPI inst){
        if(instance == null){
            instance = inst;
        }
    }
}
