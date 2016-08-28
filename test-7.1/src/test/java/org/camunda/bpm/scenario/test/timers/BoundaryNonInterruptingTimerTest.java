package org.camunda.bpm.scenario.test.timers;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class BoundaryNonInterruptingTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testExactlyReachingMaximalTimeForTask() {

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT5M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testTakeMuchTooLongForTask() {

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT6M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testTakeABitTimeForTask() {

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT4M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherScenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    Scenario.run(otherScenario).startBy("BoundaryNonInterruptingTimerTest").execute();
    Scenario.run(scenario).startBy("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).actsOnUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

}
