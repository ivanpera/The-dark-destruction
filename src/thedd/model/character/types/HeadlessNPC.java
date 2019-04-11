package thedd.model.character.types;

import thedd.model.character.BasicCharacterImpl;

/**
 * Headless extension of {@link thedd.model.character.BasicCharacterImpl}.
 */
public class HeadlessNPC extends BasicCharacterImpl {

    /**
     * Headless' constructor.
     * 
     * @param name       name of this NPC.
     * @param multiplier rate multiplied at the basic statistics.
     */
    public HeadlessNPC(final String name, final double multiplier) {
        super(name, multiplier);
        // ret.addWeightedAction(new ActionImpl() , RandomActionPrority.DEFAULT);
    }
}
