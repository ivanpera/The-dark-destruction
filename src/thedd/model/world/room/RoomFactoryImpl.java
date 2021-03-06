package thedd.model.world.room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomUtils;
import edu.princeton.cs.algs4.StdRandom;
import thedd.model.character.BasicCharacter;
import thedd.model.character.RandomEnemyFactory;
import thedd.model.character.types.DarkDestructor;
import thedd.model.world.floor.details.FloorDetails;
import thedd.model.combat.actionexecutor.DefaultCombatActionExecutor;
import thedd.model.roomevent.RoomEvent;
import thedd.model.roomevent.RoomEventHelper;
import thedd.model.roomevent.combatevent.CombatEvent;

/**
 * Implementation of RoomFactory.
 * 
 */
public class RoomFactoryImpl implements RoomFactory {

    /**
     * Mininum interactable actions in a room.
     */
    public static final int MIN_INTERACTABLE_ACTION_PER_ROOM = 0;

    /**
     * Maximum interactable actions in a room.
     */
    public static final int MAX_INTERACTABLE_ACTIONS_PER_ROOM = 3;

    /**
     * Maximim enemies in a room.
     */
    public static final int MAX_ENEMIES_PER_ROOM = 2;

    /**
     * Minimum enemies in a room.
     */
    public static final int MIN_ENEMIES_PER_ROOM = 0;

    private static final String ERROR_NOMOREROOMS = "Rooms are over";
    private static final double PROB_TO_SET_CONTENT = 0.60;
    private static final int NONE_ROOMS = -1;

    private final EnumMap<RoomContent, Integer> remainingContent;
    private final FloorDetails floorDetails;
    private int roomIndex;

    /**
     * RoomFactoryImpl constructor.
     * 
     * @param floorDetails that describe the floor
     * @throws NullPointerException if floorDetails is null
     */
    public RoomFactoryImpl(final FloorDetails floorDetails) {
        Objects.requireNonNull(floorDetails);
        this.floorDetails = floorDetails;
        this.roomIndex = RoomFactoryImpl.NONE_ROOMS;
        this.remainingContent = new EnumMap<RoomContent, Integer>(RoomContent.class);
        this.remainingContent.put(RoomContent.ENEMY, this.floorDetails.getNumberOfEnemies());
        this.remainingContent.put(RoomContent.CONTRAPTION, this.floorDetails.getNumberOfContraptions());
        this.remainingContent.put(RoomContent.TREASURE, this.floorDetails.getNumberOfTreasures());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Room createRoom() {
        if (this.roomIndex >= (this.floorDetails.getNumberOfRooms() - 1)) {
            throw new IllegalStateException(ERROR_NOMOREROOMS);
        }
        return this.changeRoom();
    }

    private Room changeRoom() {
        this.roomIndex++;
        if (this.floorDetails.isBossFloor() && this.isLastRoom()) {
            return this.createBossRoom();
        } else if (this.isLastRoom()) {
            return this.createStairsRoom();
        }
        return this.createBaseRoom();
    }

    private boolean isLastRoom() {
        return this.roomIndex == (this.floorDetails.getNumberOfRooms() - 1);
    }

    private Room createBossRoom() {
        final BasicCharacter boss = new DarkDestructor();
        final CombatEvent event = RoomEventHelper.getCombat();
        event.getHostileEncounter().addNPC(boss);
        event.getHostileEncounter()
             .setCombatLogic(new DefaultCombatActionExecutor(event.getHostileEncounter().getNPCs()));
        return new RoomImpl(Arrays.asList(event));
    }

    private Room createStairsRoom() {
        return new RoomImpl(Arrays.asList(RoomEventHelper.getStairs()));
    }

    private Room createBaseRoom() {
        final List<RoomEvent> events = new ArrayList<>();
        final CombatEvent combatEvent = RoomEventHelper.getCombat();
        final int numberOfEnemies = this.getQuantityOfEnemies();
        this.remainingContent.compute(RoomContent.ENEMY, (k, v) -> v - numberOfEnemies);
        IntStream.range(0, numberOfEnemies).boxed().map(i -> RandomEnemyFactory.createRandomEnemy())
                 .forEach(c -> combatEvent.getHostileEncounter().addNPC(c));
        if (!combatEvent.getHostileEncounter().getNPCs().isEmpty()) {
            combatEvent.getHostileEncounter()
                       .setCombatLogic(new DefaultCombatActionExecutor(combatEvent.getHostileEncounter().getNPCs()));
            events.add(combatEvent);
        }
        final EnumMap<RoomContent, Integer> interactableActions = getQuantityOfInteractableAction();
        events.addAll(IntStream.range(0, interactableActions.get(RoomContent.CONTRAPTION))
              .boxed()
              .map(b -> RoomEventHelper.getContraption()).collect(Collectors.toList()));
        events.addAll(IntStream.range(0, interactableActions.get(RoomContent.TREASURE))
              .boxed()
              .map(b -> RoomEventHelper.getTreasureChest()).collect(Collectors.toList()));
        return new RoomImpl(events);
    }

    private int getQuantityOfEnemies() {
        if (this.remainingContent.get(RoomContent.ENEMY) > this.getRemainingBaseRoomsToSet() * MAX_ENEMIES_PER_ROOM) {
            return Integer.min(MAX_ENEMIES_PER_ROOM, this.remainingContent.get(RoomContent.ENEMY));
        } else if (StdRandom.bernoulli(PROB_TO_SET_CONTENT) && this.remainingContent.get(RoomContent.ENEMY) > 0) {
            return RandomUtils.nextInt(MIN_ENEMIES_PER_ROOM, 
                                       Integer.min(MAX_ENEMIES_PER_ROOM, this.remainingContent.get(RoomContent.ENEMY)));
        } 
        return MIN_ENEMIES_PER_ROOM;
    }

    private EnumMap<RoomContent, Integer> getQuantityOfInteractableAction() {
        final EnumMap<RoomContent, Integer> content = new EnumMap<RoomContent, Integer>(RoomContent.class);
        content.put(RoomContent.CONTRAPTION, 0);
        content.put(RoomContent.TREASURE, 0);
        final int maxInteractAfterNextRoom = MAX_INTERACTABLE_ACTIONS_PER_ROOM * (this.getRemainingBaseRoomsToSet());
        if (this.getRamainingInteractToSet() > maxInteractAfterNextRoom || RandomUtils.nextBoolean()) {
            int minInteractable = 0;
            if (this.getRamainingInteractToSet() > maxInteractAfterNextRoom) {
                minInteractable = this.getRamainingInteractToSet() - maxInteractAfterNextRoom;
            }
            final int maxInteract = Integer.min(MAX_INTERACTABLE_ACTIONS_PER_ROOM, this.getRamainingInteractToSet());
            final int numberOfInteractable = RandomUtils.nextInt(minInteractable, maxInteract + 1);
            IntStream.range(0, numberOfInteractable)
                     .boxed()
                     .map(i -> getAvailableRandomInteractableAction().get())
                     .forEach(roomContent -> content.compute(roomContent, (k, v) -> v + 1));
        }
        return content;
    }

    private Optional<RoomContent> getAvailableRandomInteractableAction() {
        Optional<RoomContent> contentType = Optional.empty();
        if (this.remainingContent.get(RoomContent.CONTRAPTION) > 0 && StdRandom.bernoulli(PROB_TO_SET_CONTENT)) {
            contentType = Optional.of(RoomContent.CONTRAPTION);
        } else if (this.remainingContent.get(RoomContent.TREASURE) > 0 && !contentType.isPresent()) {
            contentType = Optional.of(RoomContent.TREASURE);
        } else if (this.remainingContent.get(RoomContent.CONTRAPTION) > 0 && !contentType.isPresent()) {
            contentType = Optional.of(RoomContent.CONTRAPTION);
        }
        if (contentType.isPresent()) {
            this.remainingContent.compute(contentType.get(), (k, v) -> v - 1);
        }
        return contentType;
    }

    private int getRamainingInteractToSet() {
        return this.remainingContent.get(RoomContent.TREASURE) + this.remainingContent.get(RoomContent.CONTRAPTION);
    }

    private int getRemainingBaseRoomsToSet() {
        return (this.floorDetails.getNumberOfRooms() - (this.roomIndex + 1)) - 1;
    }

}
