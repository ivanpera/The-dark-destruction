package thedd.model.world.environment;

import java.util.List;

import thedd.model.world.floor.Floor;
import thedd.model.world.floor.details.FloorDetails;

/**
 * Interface that define the environment.
 */
public interface Environment {

    /***
     * This method allows to set the next floor.
     * 
     * @param floorDetails is the details of the floor that has to be setted
     * @return false if FloorDetails is not one of FloorDetails given by this class
     *         to change the current floor or the current floor is not changeable.
     * @throws NullPointerException if floorDetails is null
     */
    boolean setNextFloor(FloorDetails floorDetails);

    /**
     * This method allows to get the current floor.
     * 
     * @return the current floor
     * @throws IllegalStateException if there aren't floors
     */
    Floor getCurrentFloor();

    /**
     * This method allows to get the current floor index. If the index value is
     * below 0 it means there aren't floor yet.
     * The first floor is 0, the ground floor.
     * 
     * @return the current floor index
     */
    int getCurrentFloorIndex();

    /**
     * This method allows to know if the current floor is the last.
     * 
     * @return true if the current floor is the last
     */
    boolean isCurrentLastFloor();

    /**
     * This method allows to get a list of possible next floors like FloorDetails.
     * 
     * @return the list of possible FloorDetails
     * @throws IllegalStateException if this is the last floor, it means that floors
     *                               are ended
     */
    List<FloorDetails> getFloorOptions();
}
