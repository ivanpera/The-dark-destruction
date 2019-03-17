package model.item.usableitem;

import model.combat.action.effect.DamageEffect;
import model.item.Item;
import model.item.ItemRarity;

/**
 * A {@link UsableItem} that deals damage to the target. 
 *
 */
public class UsableItemBomb extends UsableItemImpl {

    private static final int ID = 1;
    private static final String NAME = "Bomb";
    private static final String DESCRIPTION = "An explosive. Be careful while handing it";
    private static final double DAMAGE = 20.0;

    /**
     * @param rarity
     *  the rarity of the item. The higher it is, the higher is the damage dealt by the bomb
     */
    public UsableItemBomb(final ItemRarity rarity) {
        super(ID, NAME, rarity, DESCRIPTION);
        this.addActionEffect(new DamageEffect(DAMAGE * this.getEffectsMultiplier().get(rarity)));
    }

    /**
     * 
     * @param rarity
     *          the rarity of the bomb
     * @return
     *          a new item Bomb of the designed rarity
     */
    public static Item getNewInstance(final ItemRarity rarity) {
        return new UsableItemBomb(rarity);
    }
}
