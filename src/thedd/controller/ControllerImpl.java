package thedd.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Platform;
import thedd.controller.information.PlayerInformation;
import thedd.controller.information.PlayerInformationImpl;
import thedd.controller.information.StatisticsInformation;
import thedd.controller.information.StatisticsInformationImpl;
import thedd.model.Model;
import thedd.model.ModelImpl;
import thedd.model.character.BasicCharacter;
import thedd.model.character.statistics.Statistic;
import thedd.model.combat.action.Action;
import thedd.model.combat.action.result.ActionResult;
import thedd.model.combat.actionexecutor.ActionExecutor;
import thedd.model.combat.actionexecutor.OutOfCombatActionExecutor;
import thedd.model.combat.actionexecutor.StatusUpdateActionExecutor;
import thedd.model.combat.actor.ActionActor;
import thedd.model.combat.encounter.HostileEncounter;
import thedd.model.combat.instance.ActionExecutionInstance;
import thedd.model.combat.instance.CombatStatus;
import thedd.model.combat.instance.ExecutionInstanceImpl;
import thedd.model.item.Item;
import thedd.model.item.ItemFactory;
import thedd.model.item.usableitem.UsableItem;
import thedd.model.roomevent.RoomEvent;
import thedd.model.roomevent.RoomEventType;
import thedd.model.roomevent.combatevent.CombatEvent;
import thedd.model.world.environment.EnvironmentImpl;
import thedd.model.world.floor.FloorDetails.FloorDetails;
import thedd.model.world.room.Room;
import thedd.view.ApplicationViewState;
import thedd.view.View;

/**
 * Implementation of the {@link Controller}.
 */
public class ControllerImpl implements Controller {

    private final View view;
    private final Model model;
    private PlayerInformation playerInfo;
    private StatisticsInformation statisticsInfo;
    private Optional<ActionExecutor> actionExecutor = Optional.empty();
    private final List<ActionResult> roundResults = new ArrayList<>();

    /**
     * Create a new Controller instance.
     * 
     * @param view view reference
     */
    public ControllerImpl(final View view) {
        Objects.requireNonNull(view);
        this.view = view;
        this.model = new ModelImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean newGame(final String playerName, final String numberOfRooms, final String numberOfFloors) {
        if (this.isValidNumberOfRooms(numberOfRooms) && this.isValidNumberOfFloors(numberOfFloors)) {
            final int numOfRooms = Integer.parseInt(numberOfRooms);
            final int numOfFloors = Integer.parseInt(numberOfFloors);
            this.model.initGame(Optional.ofNullable(playerName), numOfFloors, numOfRooms);
            this.playerInfo = new PlayerInformationImpl(this.model.getPlayerCharacter());
            this.statisticsInfo = new StatisticsInformationImpl(this.model.getPlayerCharacter());

//---------------------QUESTO A LAVORI ULTIMATI è DA RIMUOVERE.
            BasicCharacter charac = this.model.getPlayerCharacter();
            IntStream.range(0, 15).forEach(i -> {
                charac.getInventory().addItem(ItemFactory.getRandomItem());
            });
//------------------------------------------------------------
            //return nextRoom();
            return true;
        }
        return false;
    }

    @Override
    public final boolean isValidNumberOfRooms(final String numberOfRooms) {
        if (this.checkNumber(numberOfRooms)) {
            final int numOfRooms = Integer.parseInt(numberOfRooms);
            return numOfRooms >= EnvironmentImpl.MIN_NUMBER_OF_ROOMS;
        }
        return false;
    }

    @Override
    public final boolean isValidNumberOfFloors(final String numberOfFloors) {
        if (this.checkNumber(numberOfFloors)) {
            final int numOfFloors = Integer.parseInt(numberOfFloors);
            return numOfFloors >= EnvironmentImpl.MIN_NUMBER_OF_FLOORS;
        }
        return false;
    }

    private boolean checkNumber(final String number) {
        return !number.isEmpty() && number.chars().allMatch(Character::isDigit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void closeApplication() {
        Platform.exit();
    }

    // -------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCombatActive() {
        return this.model.getPlayerCharacter().isInCombat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteItem(final Item item) {
        this.model.getPlayerCharacter().getInventory().removeItem(item);
        this.view.update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useItem(final Item item) {
        if (item.isUsable()) {
            final UsableItem usable = (UsableItem) item;
            playerInfo.setUsedItem(item);
//            this.view.update();
            final Action itemAction = usable.getAction();
            itemAction.setSource(getPlayer());
            if (getPlayer().isInCombat()) {
                this.selectAction(itemAction);
            } else {
                this.executeSingleAction(itemAction);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equipItem(final Item item) {
        final boolean ret = this.model.getPlayerCharacter().equipItem(item);
        this.view.update();
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unequipItem(final Item item) {
        this.model.getPlayerCharacter().unequipItem(item);
        this.view.update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerInformation getPlayerInformation() {
        return this.playerInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticsInformation getStatisticsInformation() {
        return this.statisticsInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStatistics(final BasicCharacter character) {
        this.statisticsInfo.setCharacter(character);
        //this.view.update();
        view.partialUpdate();
    }

    // ----------------------------------------------------Action execution and
    // combat management session

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoActionSelection() {
        final ActionActor playerActor = this.model.getPlayerCharacter();
        playerActor.resetSelectedAction();
        // call view to tell player to select a new action [DONE]
        view.showMessage("Select an action");
        view.showActionSelector();
        view.resetActionTargets();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void targetSelected(final ActionActor target) {
        view.hideMessage();
        final ActionActor playerActor = this.model.getPlayerCharacter();
        final ActionExecutor currentExecutor = actionExecutor.get();
        playerActor.getSelectedAction().ifPresent(a -> {
            playerInfo.getUsedItem().ifPresent(i -> {
                this.model.getPlayerCharacter().getInventory().removeItem(i);
                playerInfo.resetUsedItem();
            });
            a.setTargets(target, a.getValidTargets(currentExecutor.getExecutionInstance()));
            currentExecutor.addActorToQueue(playerActor);
        });
        if (currentExecutor.isRoundReady()) {
            evaluateNextAction();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeSingleAction(final Action action) {
        final ActionActor playerActor = this.model.getPlayerCharacter();
        final ActionExecutionInstance instance = new ExecutionInstanceImpl();
        actionExecutor = Optional.of(new OutOfCombatActionExecutor(action));
        instance.addPlayerPartyMember(playerActor);
        actionExecutor.get().setExecutionInstance(instance);
        if (action.getTargets().isEmpty()) {
            // call view to tell player to select a target [DONE]
            model.getPlayerCharacter().addActionToQueue(action, true);
            view.showMessage("Select a target");
            final List<ActionActor> targetables = nonDeadTargets(action.getValidTargets(instance));
            view.showActionTargets(targetables, instance.getPlayerParty(), instance.getNPCsParty(), action);
        } else {
            evaluateNextAction();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeCurrentAction() {
        if (!actionExecutor.isPresent()) {
            return;
        }
        final ActionExecutor executor = actionExecutor.get();
        executor.executeCurrentAction();
        // tell view to log
        evaluateExecutionState();
        //
        //togli view.showActionResult(executor.getLastActionResult().get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluateExecutionState() {
        if (!actionExecutor.isPresent()) {
            return;
        }
        final ActionExecutor executor = actionExecutor.get();
        executor.updateExecutionStatus();
        final CombatStatus status = executor.getExecutionStatus();
        switch (status) {
        case COMBAT_ENDED:
            actionExecutor = Optional.empty();
            view.showActionResult(roundResults);
            roundResults.clear();
            view.update();
            break;
        case PLAYER_LOST:
            System.out.println("player morto");
            this.view.setState(ApplicationViewState.END_GAME);
            view.update();
            break;
        case PLAYER_WON:
            System.out.println("playerWon");
            view.showActionResult(roundResults);
            roundResults.clear();
            if (this.isCurrentLastFloor() && this.isCurrentLastRoom()) {
                this.view.setState(ApplicationViewState.END_GAME);
            }
            this.view.showInventory();
            view.update();
            break;
        case ROUND_IN_PROGRESS:
            evaluateNextAction();
            break;
        case ROUND_PAUSED:
            //tell view to select a new action (by extension, enable all appropriate view components)
            view.showActionResult(roundResults);
            roundResults.clear();
            view.showMessage("Select an action");
            view.showActionSelector();
            view.update();
            break;
        case ROUND_ENDED:
            view.showActionResult(roundResults);
            roundResults.clear();
            view.showMessage("Select an action");
            view.showActionSelector();
            view.update();
            //view.showMessage("Select an action");
            //view.showActionSelector();
            executor.prepareNextRound();
            break;
        default:
            break;
        }
    }

    private void startCombat(final HostileEncounter encounter) {
        final ActionActor playerActor = this.model.getPlayerCharacter();
        final ActionExecutionInstance instance = new ExecutionInstanceImpl();
        final ActionExecutor combatExecutor = encounter.getCombatLogic();
        instance.addPlayerPartyMember(playerActor);
        instance.addNPCsPartyMembers(encounter.getNPCs());
        combatExecutor.setExecutionInstance(instance);
        combatExecutor.startExecutor();
        actionExecutor = Optional.of(combatExecutor);
        if (combatExecutor.isRoundReady()) {
            evaluateNextAction();
        } else {
            // call view to tell player to select an action
            view.showActionSelector();
            view.showMessage("Select an action");
        }
    }

    private void evaluateNextAction() {
        actionExecutor.ifPresent(a -> {
            a.setNextAction();
            final Optional<ActionResult> result = a.evaluateCurrentAction();
            // view visualize(result)
            if (result.isPresent()) {
                roundResults.add(result.get());
                //salva actionResult da qualche parte
                executeCurrentAction();
                //togli view.showActionEffect(result.get());
            } else {
                evaluateExecutionState();
            }
        });
    }

    private void updateStatuses() {
        final ActionExecutor executor = new StatusUpdateActionExecutor();
        final ActionExecutionInstance instance = new ExecutionInstanceImpl();
        instance.addPlayerPartyMember(model.getPlayerCharacter());
        executor.setExecutionInstance(instance);
        executor.startExecutor();
        actionExecutor = Optional.of(executor);
        evaluateNextAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectAction(final Action action) {
        //action.setSource(getPlayer());
        model.getPlayerCharacter().addActionToQueue(action, true);
        // call view to tell player to select a target (even if the action target type
        // is SELF?)

        final ActionExecutionInstance aei = actionExecutor.get().getExecutionInstance();
        final List<ActionActor> targetables = nonDeadTargets(action.getValidTargets(aei));
        view.showActionTargets(targetables, aei.getPlayerParty(), aei.getNPCsParty(), action);
        view.showMessage("Select a target");
    }

    @Override
    public final boolean nextRoom() {
        final boolean isChanged = this.model.getEnvironment().getCurrentFloor().nextRoom();
        if (isChanged) {
            final Room room = this.model.getEnvironment().getCurrentFloor().getCurrentRoom();
            if (room.getEvents().stream().anyMatch(e -> e instanceof CombatEvent)) {
                final CombatEvent combat = room.getEvents().stream().filter(e -> e.getType() == RoomEventType.COMBAT_EVENT)
                                                                    .findFirst()
                                                                    .map(e -> (CombatEvent) e)
                                                                    .get();
                //this.getPlayer().setIsInCombat(true);
                this.startCombat(combat.getHostileEncounter());
                this.view.update();
            } else {
                this.updateStatuses();
            }
        }
        return isChanged;
    }

    @Override
    public final boolean nextFloor(final FloorDetails floorDetails) {
        return this.model.getEnvironment().setNextFloor(floorDetails);
    }

    @Override
    public final List<RoomEvent> getRoomEvents() {
        return this.model.getEnvironment().getCurrentFloor().getCurrentRoom().getEvents();
    }

    @Override
    public final List<FloorDetails> getStairsOptions() {
        return this.model.getEnvironment().getFloorOptions();
    }

    @Override
    public final boolean isCurrentLastFloor() {
        return this.model.getEnvironment().isCurrentLastFloor();
    }

    @Override
    public final boolean isCurrentLastRoom() {
        return !this.model.getEnvironment().getCurrentFloor().hasNextRoom();
    }

    @Override
    public final boolean isCurrentRoomCompleted() {
        return this.model.getEnvironment().getCurrentFloor().getCurrentRoom().checkToMoveOn();
    }

    @Override
    public final boolean hasPlayerWon() {
        return this.model.hasPlayerWon();
    }

    @Override
    public final BasicCharacter getPlayer() {
        return this.model.getPlayerCharacter();
    }

    private List<ActionActor> nonDeadTargets(final List<ActionActor> targets) {
        return targets.stream()
                      .filter(t -> t instanceof BasicCharacter && ((BasicCharacter) t).getStat(Statistic.HEALTH_POINT).getActual() > 0)
                      .collect(Collectors.toList());
    }
}
