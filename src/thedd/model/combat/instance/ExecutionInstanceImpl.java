package thedd.model.combat.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import thedd.model.character.BasicCharacter;
import thedd.model.combat.actor.ActionActor;

/**
 *  Basic implementation of the {@link ActionExecutionInstance} interface.
 */
public class ExecutionInstanceImpl implements ActionExecutionInstance {

    private final Set<ActionActor> npcsParty = new LinkedHashSet<>();
    private final Set<ActionActor> playerParty = new LinkedHashSet<>();
    private int roundCount;
    private ExecutionStatus combatStatus = ExecutionStatus.NOT_STARTED;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRoundNumber() {
        return roundCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increaseRoundNumber() {
        roundCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNPCsPartyMembers(final Set<ActionActor> hostileNPCs) {
        npcsParty.addAll(hostileNPCs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayerPartyMembers(final Set<ActionActor> alliedPCs) {
        playerParty.addAll(alliedPCs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNPCsPartyMember(final ActionActor hostileNPC) {
        npcsParty.add(hostileNPC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayerPartyMember(final ActionActor alliedPC) {
        playerParty.add(alliedPC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActionActor> getNPCsParty() {
        return Collections.unmodifiableList(new ArrayList<>(npcsParty));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActionActor> getPlayerParty() {
        return Collections.unmodifiableList(new ArrayList<>(playerParty));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExecutionStatus(final ExecutionStatus newStatus) {
        combatStatus = newStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionStatus getExecutionStatus() {
        return combatStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActionActor> getAllParties() {
        return Stream.concat(playerParty.stream(), npcsParty.stream()).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionExecutionInstance getCopy() {
        final ActionExecutionInstance copy = new ExecutionInstanceImpl();
        copy.addPlayerPartyMembers(playerParty);
        copy.addNPCsPartyMembers(npcsParty);
        copy.setExecutionStatus(getExecutionStatus());
        while (getRoundNumber() > copy.getRoundNumber()) {
            copy.increaseRoundNumber();
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getNumberOfAliveCharacters(final List<ActionActor> actors) {
        return  actors.stream()
                      .filter(a -> a instanceof BasicCharacter)
                      .map(a -> ((BasicCharacter) a))
                      .filter(BasicCharacter::isAlive)
                      .count();
    }
}
