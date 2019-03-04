package model.character;

import java.util.EnumMap;

/**
 * Class that implements a Headless Non-Player Character.
 */
public class HeadlessNPC extends GenericCharacter {

    // Questi valori sono messi a caso e credo non abbiano assolutamente senso.
    private static final int BASIC_PV = 50;
    private static final int BASIC_COS = 30;
    private static final int BASIC_FOR = 30;
    private static final int BASIC_RIFL = 20;
    private static final String NAME = "Headless";
    private static final EnumMap<Statistic, StatValues> BASIC_STAT = new EnumMap<Statistic, StatValues>(
            Statistic.class);

    static {
        BASIC_STAT.put(Statistic.PV, new StatValuesImpl(BASIC_PV));
        BASIC_STAT.put(Statistic.COS, new StatValuesImpl(BASIC_COS));
        BASIC_STAT.put(Statistic.FOR, new StatValuesImpl(BASIC_FOR));
        BASIC_STAT.put(Statistic.RIFL, new StatValuesImpl(BASIC_RIFL));
    }

    /**
     * Headless' constructor.
     * 
     * @param multiplier rate multiplied at the basic statistics.
     */
    public HeadlessNPC(final int multiplier) {
        super(NAME);
        this.setBasicStat(BASIC_STAT);
        // ret.addWeightedAction(new ActionImpl() , RandomActionPrority.DEFAULT);
    }
}
