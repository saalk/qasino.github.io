package cloud.qasino.games.orchestration;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.orchestration.interfaces.EventHandlingResponse;
import cloud.qasino.games.orchestration.interfaces.Expression;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static cloud.qasino.games.pattern.statemachine.event.GameEvent.START;
import static cloud.qasino.games.pattern.statemachine.event.EventOutput.Result.SUCCESS;

/**
 * Contains states, events handled by certain states, actions to be carried out and transitions.
 * Is able to build a StatemachineConfig in order to facilitate gradual refactoring of currently deployed processes.
 * Is therefore also capable of storing configuration based on Triggers.
 */
@Slf4j
public class OrchestrationConfig {

    private static int actionIdCount = 0;
    //@Getter
    //private final RetryCriteria retryCriteria = new RetryCriteria();
    private Map<GameState, StateConfig> stateConfigMap = new LinkedHashMap<GameState, StateConfig>();
    private Map<Event, List<GameState>> statesPermittingEvent = new HashMap<>();
    private List<Class<? extends GenericLookupsAction>> actionsBeforeEvent = new ArrayList<>();
    private List<Class<? extends GenericLookupsAction>> actionsAfterEvent = new ArrayList<>();
    private Map<Class<? extends Exception>, Transition> exceptionToTransitionMap = new LinkedHashMap<>();
    private boolean rethrowExceptions = false;
    private EventHandlingResponse defaultResponse;
    //private List<Class<? extends Action>> actionsBeforeRetryEvent = new ArrayList<>();

    private static Transition getTransitionForException(Exception exception, Map<Class<? extends Exception>, Transition>
            exceptionToTransitionMap) {
        Class<? extends Exception> exceptionClass = exception.getClass();
        for (Class<? extends Exception> expectedExceptionClass : exceptionToTransitionMap.keySet()) {
            if (expectedExceptionClass.isAssignableFrom(exceptionClass)) {
                return exceptionToTransitionMap.get(expectedExceptionClass);
            }
        }
        return null;
    }

    /**
     * Adds a state.
     */
    public StateConfig onState(GameState state) {
        if (!stateConfigMap.containsKey(state)) {
            stateConfigMap.put(state, new StateConfig(state));
        }
        return stateConfigMap.get(state);
    }

    /**
     * Can be used by diagram generator
     *
     * @return Collection<StateConfig>
     */
    Collection<StateConfig> getStateConfigs() {
        return stateConfigMap.values();
    }

    /**
     * @param event
     * @return List<GameState>
     */
    List<GameState> getStatesPermittingEvent(Event event) {
        return statesPermittingEvent.containsKey(event) ? statesPermittingEvent.get(event) : new ArrayList<GameState>();
    }

    /**
     * @param event
     * @param state
     */
    private void addStatePermittingEvent(Event event, GameState state) {
        List<GameState> states = statesPermittingEvent.get(event);
        if (states == null) {
            states = new ArrayList<>();
            statesPermittingEvent.put(event, states);
        }
        if (states.contains(state)) {
            throw new IllegalStateException("event " + event + " cannot be handled twice by for state " + state);
        }
        states.add(state);
    }

    /**
     * @param state
     * @return StateConfig
     */
    StateConfig getStateConfig(GameState state) {
        return stateConfigMap.get(state);
    }

    /**
     * @param state
     * @param event
     * @return list of actions to be carried out in order
     */
    Collection<ActionConfig> getActionsForEvent(final GameState state, final Event event) {

        StateConfig stateConfig = stateConfigMap.get(state);
        if (stateConfig == null) {
            throw new IllegalStateException("State " + state + " not configured while looking for actions for event " + event);
        }
        EventConfig eventConfig = getEventConfig(state, event);
        if (eventConfig == null) {
            return new ArrayList<>();
        }
        return eventConfig.getActionConfigs();
    }

    EventConfig getEventConfig(final GameState state, final Event event) {
        return stateConfigMap.get(state).eventConfigMap.get(event);
    }

    /**
     * Adds an action to be performed before any other actions.
     *
     * @param action
     * @return OrchestrationConfig
     */
    public OrchestrationConfig beforeEventPerform(final Class<? extends GenericLookupsAction> action) {
        actionsBeforeEvent.add(action);
        return this;
    }

    /**
     * Adds an action to be performed after the event has been handled.
     *
     * @param action
     * @return OrchestrationConfig
     */
    public OrchestrationConfig afterEventPerform(final Class<? extends GenericLookupsAction> action) {
        actionsAfterEvent.add(action);
        return this;
    }

    /**
     * @return Collection<ActionConfig>
     */
    Collection<ActionConfig> getBeforeEventActions() {
        List<ActionConfig> result = new ArrayList<>();
        for (Class<? extends GenericLookupsAction> action : actionsBeforeEvent) {
            result.add(new ActionConfig(null, action));
        }
        return result;
    }

    /**
     * @return List<ActionConfig>
     */
    public List<ActionConfig> getAfterEventActions() {
        List<ActionConfig> result = new ArrayList<>();
        for (Class<? extends GenericLookupsAction> action : actionsAfterEvent) {
            result.add(new ActionConfig(null, action));
        }
        return result;
    }

    /**
     * Configures a transition to a next state if a certain exception type (or supertype) is caught while performing an action
     */
    public OrchestrationConfig onResult(final Class<? extends Exception> exceptionClass, final GameState nextState) {
        return onResult(exceptionClass, nextState, null);
    }

    /**
     * Configures a transition to a next state if a certain exception type (or supertype) is caught while performing an action
     * Sets (frontend) response on Dto
     *
     * @param exceptionClass
     * @param nextState
     * @param response
     * @return
     */
    public OrchestrationConfig onResult(final Class<? extends Exception> exceptionClass, final GameState nextState,
                                        EventHandlingResponse response) {
        //make sure state exists
        onState(nextState);
        this.exceptionToTransitionMap.put(exceptionClass, new Transition(nextState, response, null));
        return this;
    }

/*
    public OrchestrationConfig retryRequest(Type requestType) {
        this.retryCriteria.setTypeCriteria(requestType);
        return this;
    }

    public OrchestrationConfig onErrorRetryFrom(List<GameState> states) {
        this.retryCriteria.getErrorAndPreviousStateCriteria().addAll(states);
        return this;
    }

    public OrchestrationConfig onErrorRetryFrom(GameState... states) {
        onErrorRetryFrom(Arrays.asList(states));
        return this;
    }
*/

    /**
     * Configures Exceptions on actions to be rethrown instead of resulting in transitions
     *
     * @return
     */
    public OrchestrationConfig rethrowExceptions() {
        rethrowExceptions = true;
        return this;
    }

    boolean rethrowsExceptions() {
        return rethrowExceptions;
    }

    /**
     * @param exception
     * @return Transition
     */
    Transition getTransitionForException(Exception exception) {
        return getTransitionForException(exception, exceptionToTransitionMap);
    }

    /**
     * Configures a default response to be set on Dto in case flow does not result in other specific response
     *
     * @param defaultResponse
     * @return
     */
    public OrchestrationConfig respondDefault(final EventHandlingResponse defaultResponse) {
        this.defaultResponse = defaultResponse;
        return this;
    }

    /**
     * @param state
     * @param event
     * @return true if state is configured to handle event
     */
    public boolean statePermitsEvent(final GameState state, final GameEvent event) {
        return getEventConfig(state, event) != null;
    }

    /**
     * Contains both old-style Trigger and configured next GameState.
     * Is used to facilitate gradual refactoring of Trigger-based controller configuration.
     */
    static class Transition {
        private GameState nextState;
        private EventHandlingResponse response;
        private Event nextEvent;

        /**
         * One of the parameters may be null, depending on how transitions are configured.
         *
         * @param nextState
         */
        Transition(final GameState nextState, final EventHandlingResponse response, Event nextEvent) {
            this.nextState = nextState;
            this.response = response;
            this.nextEvent = nextEvent;
        }

        /**
         * @return GameState
         */
        GameState getNextState() {
            return nextState;
        }

        EventHandlingResponse getResponse() {
            return response;
        }

        public Event getNextEvent() {
            return nextEvent;
        }
    }

    /**
     * Contains configured events and permitted transitions.
     */
    public class StateConfig {

        private GameState state;
        private Map<Event, EventConfig> eventConfigMap = new HashMap<>();

        /**
         * @param state
         */
        StateConfig(GameState state) {
            this.state = state;
        }

        /**
         * @param event
         * @return EventConfig
         */
        EventConfig getEventConfig(Event event) {
            return eventConfigMap.get(event);
        }

        /**
         * Adds an event that can be handled by this state.
         *
         * @param event
         * @return EventConfig
         */
        public EventConfig onEvent(Event event) {
            if (!eventConfigMap.containsKey(event)) {
                addStatePermittingEvent(event, state);
                eventConfigMap.put(event, new EventConfig(this, event));
            }
            return eventConfigMap.get(event);
        }

        /**
         * @return EventConfig
         */
        EventConfig getEntryEvent() {
            return eventConfigMap.get(START);
        }

        /**
         * @return GameState
         */
        GameState getState() {
            return state;
        }

        /**
         * @return Collection<EventConfig>
         */
        Collection<EventConfig> getEventConfigs() {
            return eventConfigMap.values();
        }

        /**
         * Performs an action after a transition to this particular state has been performed.
         *
         * @param action
         * @return ActionConfig
         */
        public ActionConfig onEntryPerform(Class<? extends GenericLookupsAction> action) {
            return onEvent(START).perform(action);
        }

        public EventConfig onEntry() {
            return onEvent(START);
        }

        /**
         * @param event
         * @return EventConfig
         */
        EventConfig getEvent(final Event event) {
            return eventConfigMap.get(event);
        }


        /**
         * Records in this state will be retried automatically when pending in current state.
         *
         * @see QasinoEventHandler
         */
/*        public StateConfig retryIfPending() {
            retryCriteria.getPendingRequestStateCriteria().add(this.state);
            return this;
        }*/

        /**
         * Records in this state will be retried automatically when pending in current state.
         *
         * @see QasinoEventHandler
         */
/*        public StateConfig retryIfError() {
            retryCriteria.getErrorAndPreviousStateCriteria().add(this.state);
            return this;
        }*/

    }

    public class EventConfig {
        private Event event;
        private List<ActionConfig> actions = new ArrayList<>();
        private StateConfig state;
        private boolean eventRethrowsExceptions = false;
        private EventHandlingResponse defaultEventResponse;

        /**
         * @param state
         * @param event
         */
        EventConfig(StateConfig state, Event event) {
            this.event = event;
            this.state = state;
            this.defaultEventResponse = defaultResponse;
        }

        /**
         * Adds an action to be carried out during event handling.
         *
         * @param action
         * @return ActionConfig
         */
        public ActionConfig perform(Class<? extends GenericLookupsAction> action) {
            ActionConfig actionConfig = new ActionConfig(this, action);
            actions.add(actionConfig);
            return actionConfig;
        }

        public EventConfig rethrowExceptions() {
            eventRethrowsExceptions = true;
            return this;
        }

        boolean rethrowsExceptions() {
            if (!eventRethrowsExceptions) {
                return rethrowExceptions;
            }
            return true;
        }


        /**
         * @return Collection<ActionConfig>
         */
        Collection<ActionConfig> getActionConfigs() {
            return actions;
        }

        /**
         * @return Event
         */
        Event getEvent() {
            return event;
        }

        StateConfig getStateConfig() {
            return state;
        }

        /**
         * Performs a transition to nextState regardless of the outcome of an action.
         *
         * @param nextState
         * @return EventConfig
         */
        public EventConfig transition(GameState nextState) {
            onState(nextState);
            perform((Class<? extends GenericLookupsAction>) OkAction.class).onResult(SUCCESS, nextState);
            return this;
        }

        public void transition(final GameState nextState, final Event nextEvent) {
            perform((Class<? extends GenericLookupsAction>) OkAction.class).onResult(SUCCESS, nextState, nextEvent);
        }

        /**
         * @return Collection<ActionConfig>
         */
        Collection<ActionConfig> getAfterEventActions() {
            List<ActionConfig> result = new ArrayList<>();
            for (Class<? extends GenericLookupsAction> action : actionsAfterEvent) {
                result.add(new ActionConfig(this, action));
            }
            return result;
        }

        public EventConfig respondDefault(final EventHandlingResponse defaultResponse) {
            this.defaultEventResponse = defaultResponse;
            return this;
        }

        public EventHandlingResponse getDefaultResponse() {
            return defaultEventResponse;
        }
    }

    /**
     * Contains configuration of actions to be performed during event handling.
     */
    public class ActionConfig {

        private EventConfig event;
        private Class<? extends GenericLookupsAction> action;
        //TODO  unchecked call
        private Map<Object, Transition> resultTransitionMap = new HashMap();
        private Map<Class<? extends Exception>, Transition> exceptionToTransitionMap = new LinkedHashMap<>();
        private Transition defaultTransition;
        private Class resultType;
        private int actionId = actionIdCount++;

        /**
         * @param event
         * @param action
         */
        ActionConfig(EventConfig event, Class<? extends GenericLookupsAction> action) {
            this.event = event;
            this.action = action;
        }

        int getActionId() {
            return actionId;
        }

        /**
         * Adds an action to be performed during event handling
         *
         * @param action
         * @return ActionConfig
         */
        public ActionConfig perform(Class<? extends GenericLookupsAction> action) {
            return event.perform(action);
        }

        /**
         * @return Class<? extends Action>
         */
        Class<? extends GenericLookupsAction> getAction() {
            return action;
        }

        /**
         * @return GameState
         */
        GameState getCorrespondingState() {
            return event.state.state;
        }


        /**
         * Ties the action to a transition that is to be performed if the action produces a certain result.
         *
         * @param expectedResult
         * @param nextState
         * @return ActionConfig
         */
        public ActionConfig onResult(final Object expectedResult, final GameState nextState) {
            return onResult(expectedResult, nextState, event.defaultEventResponse);
        }

        /**
         * @param expectedResult
         * @param nextState
         * @param nextEvent      to be fired immidiately after transition
         * @return
         */
        public ActionConfig onResult(final Object expectedResult, final GameState nextState, final Event nextEvent) {
            return onResult(expectedResult, nextState, null, nextEvent);
        }

        /**
         * @param response to be set on Dto
         */
        public ActionConfig onResult(final Object expectedResult, final GameState nextState, final EventHandlingResponse
                response) {
            return onResult(expectedResult, nextState, response, null);
        }

        /**
         * @param expectedResult
         * @param nextState
         * @param response       to be set on Dto
         * @param nextEvent      to be fired immidiately after transition
         * @return
         */
        public ActionConfig onResult(final Object expectedResult, final GameState nextState, final EventHandlingResponse
                response, final Event nextEvent) {
            assertResultType(expectedResult);
            resultType = expectedResult instanceof Expression ? null : expectedResult.getClass();
            onState(nextState);
            this.resultTransitionMap.put(expectedResult, new Transition(nextState, response, nextEvent));
            return this;
        }

        /**
         * Ties the action to a transition that is to be performed if the action encounters a specific exception.
         * The configured transition takes precedence over behaviour defined at a higher level.
         *
         * @param expectedExceptionClass
         * @param nextState
         * @return ActionConfig
         */
        public ActionConfig onResult(final Class<? extends Exception> expectedExceptionClass, final GameState nextState) {
            onState(nextState);
            this.exceptionToTransitionMap.put(expectedExceptionClass, new Transition(nextState, null, null));
            return this;
        }


        /**
         * Specifies that event must be fired right after transition.
         *
         * @param expectedExceptionClass
         * @param nextState
         * @param nextEvent
         * @return
         */
        public ActionConfig onResult(final Class<? extends Exception> expectedExceptionClass, final GameState nextState, final Event
                nextEvent) {
            onState(nextState);
            this.exceptionToTransitionMap.put(expectedExceptionClass, new Transition(nextState, null, nextEvent));
            return this;
        }

        /**
         * @param expectedResult
         */
        private void assertResultType(final Object expectedResult) {
            if (resultType != null) {
                //TODO  unchecked call
                if (!expectedResult.getClass().isAssignableFrom(resultType) || !resultType.isAssignableFrom(expectedResult
                        .getClass())) {
                    String errorMessage = "Result with type " + expectedResult.getClass().getSimpleName() + " for action " + action.getSimpleName()
                            + " in " +
                            "state " + event.state.state + " does not correspond with configured type " +
                            resultType.getSimpleName();
                    log.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            }
        }

        /**
         * @param exception
         * @return Transition
         */
        Transition getTransitionForException(Exception exception) {
            return OrchestrationConfig.getTransitionForException(exception, exceptionToTransitionMap);
        }


        /**
         * @param result
         * @return Transition
         */
        Transition getTransitionForResult(final Object result) {
            assertResultType(result);
            Transition transition = resultTransitionMap.get(result);
            if (transition == null) {
                return defaultTransition;
            }
            return transition;
        }

        /**
         * Perform a transaction for a certain trigger if no other transition can be found for a result.
         *
         * @param nextState
         * @deprecated
         */
        public void byDefault(final GameState nextState) {
            defaultTransition = new Transition(nextState, null, null);
        }

        public void byDefault(final GameState nextState, final EventHandlingResponse response) {
            defaultTransition = new Transition(nextState, response, null);
        }

        EventConfig getEvent() {
            return event;
        }

        Map<Object, Transition> getResultTransitionMap() {
            return resultTransitionMap;
        }
    }

    class RetryConfig {

    }
}
