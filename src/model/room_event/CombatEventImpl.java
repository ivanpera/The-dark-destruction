package model.room_event;

import model.combat.enums.CombatStatus;
import model.combat.implementations.HostileEncounterImpl;
import model.combat.interfaces.HostileEncounter;

/**
 * Implementation of {@link model.room_event.CombatEvent}.
 *
 */
public final class CombatEventImpl extends AbstractRoomEvent implements CombatEvent {

    private static final String NAME = "Combat";
    private final HostileEncounter hostileEncounter;

    /**
     * Create a combat event.
     */
    public CombatEventImpl() {
        super(NAME);
        this.hostileEncounter = new HostileEncounterImpl();
    }

    @Override
    public RoomEventType getType() {
        return RoomEventType.COMBAT_EVENT;
    }

    @Override
    public HostileEncounter getHostileEncounter() {
        return hostileEncounter;
    }

    @Override
    public boolean isCompleted() {
        return hostileEncounter.getCombatLogic().getCombatStatus() == CombatStatus.PLAYER_LOST || hostileEncounter.getCombatLogic().getCombatStatus() == CombatStatus.PLAYER_WON;
    }

    @Override
    public boolean isSkippable() {
        return false;
    }

}
