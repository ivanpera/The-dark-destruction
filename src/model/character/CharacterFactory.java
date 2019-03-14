package model.character;

import java.util.Optional;

/**
 * Implementation of a character factory.
 */
public final class CharacterFactory {

    private static final String DEFAULT_PC_NAME = "Player";

    private CharacterFactory() {
    }

    /**
     * Method that create a new PlayerCharacter.
     * @param name chosen by the player.
     * @return a new Player Character
     */
    public static BasicCharacter createPlayerCharacter(final Optional<String> name) {
        if (name.isPresent()) {
            return new PlayerCharacter(name.get());
        }
        return new PlayerCharacter(DEFAULT_PC_NAME);
    }

    /**
     * Method that create a new Enemy Non-Player Character.
     * @param type the type of the enemy.
     * @param multiplier 
     * @return a new specified Non-Player Character
     */
    public static BasicCharacter createEnemy(final EnemyCharacterType type, final int multiplier) {
        switch (type) {
        case GOBLIN:
            return new GoblinNPC(multiplier);
        default:
            return new HeadlessNPC(multiplier);
        }
    }

    /**
     * Method that create a new Boss Character.
     * @param multiplier 
     * @return a new boss.
     */
    public static BasicCharacter createFinalBoss(final int multiplier) {
        return new DarkDestructorNPC(multiplier);
    }
}
