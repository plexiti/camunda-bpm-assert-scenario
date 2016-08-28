package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskWaitstate extends AbstractTaskDelegate {

  public UserTaskWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected Task getDelegate() {
    return getTaskService().createTaskQuery().activityInstanceIdIn(historicDelegate.getId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.actsOnUserTask(getActivityId());
  }

  protected void leave() {
    getTaskService().complete(getDelegate().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getTaskService().complete(getDelegate().getId(), variables);
  }

  @Override
  public void complete() {
    leave();
  }

  @Override
  public void complete(Map<String, Object> variables) {
    leave(variables);
  }

}
