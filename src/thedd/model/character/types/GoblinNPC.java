package thedd.model.character.types;

import java.util.EnumMap;

import thedd.model.character.BasicCharacterImpl;
import thedd.model.character.statistics.StatValues;
import thedd.model.character.statistics.StatValuesImpl;
import thedd.model.character.statistics.Statistic;

/**
 * Class that implements a Goblin Non-Player Character.
 */
public class GoblinNPC extends BasicCharacterImpl {
    private static final int BASIC_PV = 50;
    private static final int BASIC_COS = 30;
    private static final int BASIC_FOR = 30;
    private static final int BASIC_RIFL = 20;
    private static final String NAME = "Goblin";
    private static final int DEFAULT_MULTIPLIER = 1;
    private final EnumMap<Statistic, StatValues> basicStat;

    /**
     * Goblin's constructor.
     * 
     * @param multiplier rate multiplied at the basic statistics.
     */
    public GoblinNPC(final int multiplier) {
        super(NAME);
        basicStat = new EnumMap<Statistic, StatValues>(Statistic.class);
        if (multiplier > 0) {
            initStat(multiplier);
        } else {
            initStat(DEFAULT_MULTIPLIER);
        }
        this.setBasicStat(basicStat);
        // this.addWeightedAction(new ActionImpl() , RandomActionPrority.DEFAULT);
    }

    private void initStat(final int multiplier) {
        basicStat.put(Statistic.PV, new StatValuesImpl(BASIC_PV * multiplier));
        basicStat.put(Statistic.COS, new StatValuesImpl(BASIC_COS * multiplier));
        basicStat.put(Statistic.FOR, new StatValuesImpl(BASIC_FOR * multiplier));
        basicStat.put(Statistic.RIFL, new StatValuesImpl(BASIC_RIFL * multiplier));
    }
}
