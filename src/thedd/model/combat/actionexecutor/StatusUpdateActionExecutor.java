package thedd.model.combat.actionexecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import thedd.model.character.BasicCharacter;
import thedd.model.combat.action.Action;
import thedd.model.combat.action.result.ActionResult;
import thedd.model.combat.action.result.ActionResultImpl;
import thedd.model.combat.action.result.ActionResultType;
import thedd.model.combat.actor.ActionActor;
import thedd.model.combat.instance.ActionExecutionInstance;
import thedd.model.combat.instance.ExecutionStatus;
import thedd.model.combat.instance.ExecutionInstanceImpl;
import thedd.model.combat.status.Status;

/**
 * Updates the {@link Status} of all the {@link ActionActor} present
 * in the {@link ActionExecutionInstance}, executes their provided
 * actions and, if necessary, removes them.<p>
 * It goes through only one round.<p>
 * This executor does not support the addActorToQueue method.
 */
public class StatusUpdateActionExecutor implements ActionExecutor {

    private ActionExecutionInstance instance = new ExecutionInstanceImpl();
    private final List<Status> queue = new ArrayList<>();
    private Optional<Status> currentStatus = Optional.empty();
    private Optional<Action> currentAction = Optional.empty();
    private Optional<ActionResult> currentActionResult = Optional.empty();
    private Iterator<Status> iterator;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExecutionInstance(final ActionExecutionInstance newInstance) {
        instance = newInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startExecutor() {
        instance.setExecutionStatus(ExecutionStatus.STARTED);
        instance.getAllParties().forEach(a -> {
            queue.addAll(a.getStatuses());
        });
        iterator = queue.iterator();
        prepareNextRound();
    }

    /**
     * Does nothing.
     */
    @Override
    public void prepareNextRound() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeCurrentAction() {
        if (currentActionResult.isPresent()) {
            final Action action = currentAction.get();
            currentActionResult.get().getResults().stream()
                                                  .filter(p -> p.getValue() == ActionResultType.HIT)
                                                  .forEach(p -> {
                                                      final ActionActor target = p.getKey();
                                                      action.applyEffects(target);
                                                      if (target instanceof BasicCharacter
                                                              && !((BasicCharacter) target).isAlive()) {
                                                          queue.removeAll(target.getStatuses());
                                                      }
                                                  });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNextAction() {
        if (iterator.hasNext() && !currentAction.isPresent()) {
            currentStatus = Optional.of(iterator.next());
            currentStatus.get().update(instance);
        }
        if (!currentAction.isPresent() && currentStatus.isPresent()) {
            currentAction = currentStatus.get().getAction();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ActionResult> evaluateCurrentAction() {
        instance.setExecutionStatus(ExecutionStatus.ROUND_IN_PROGRESS);
        if (!currentAction.isPresent()) {
            currentActionResult = Optional.empty();
        } else {
            final Action action = currentAction.get();
            final ActionResult result = new ActionResultImpl(action.getCopy());
            final List<ActionActor> targets = action.getTargets();
            targets.forEach(target -> {
                action.rollToHit(target);
                if (action.isTargetHit()) {
                    result.addResult(target, ActionResultType.HIT);
                } else {
                    result.addResult(target, ActionResultType.MISSED);
                }
            });
            currentActionResult = Optional.of(result);
        }
        return currentActionResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateExecutionStatus() {
        if (instance.getNumberOfAliveCharacters(instance.getPlayerParty()) <= 0) {
                instance.setExecutionStatus(ExecutionStatus.PLAYER_LOST);
                return;
        }

        currentAction = Optional.empty();
        currentStatus.ifPresent(status -> {
            if (status.getCurrentDuration() <= 0) {
                status.update(instance);
                currentAction = status.getAction();
                status.getAfflictedActor().get().removeStatus(status);
                currentStatus = Optional.empty();
            }
        });

        if (!iterator.hasNext() && !currentAction.isPresent()) {
            queue.forEach(s -> s.setIsUpdated(false));
            instance.setExecutionStatus(ExecutionStatus.COMBAT_ENDED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionStatus getExecutionStatus() {
        return instance.getExecutionStatus();
    }

    /**
     * Not implemented, does nothing.
     */
    @Override
    public void addActorToQueue(final ActionActor actor) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoundReady() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActionActor> getOrderedActorsList() {
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionExecutionInstance getExecutionInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ActionResult> getLastActionResult() {
        return currentActionResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canActorAct(final ActionActor actor) {
        return false;
    }

}
