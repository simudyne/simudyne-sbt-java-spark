package org.example.models.conway;

import simudyne.core.Model;
import simudyne.core.abm.AgentSystem;
import simudyne.core.abm.LongAccumulator;
import simudyne.core.abm.topology.Group;
import simudyne.core.abm.topology.linker.GridConnected;
import simudyne.core.annotations.Variable;

public class GameOfLife implements Model {
  private AgentSystem grid = AgentSystem.create();
  private LongAccumulator bornAccumulator = grid.createLongAccumulator("born");
  private LongAccumulator diedAccumulator = grid.createLongAccumulator("died");

  @Variable
  public long aliveCells() {
    return grid.selectAgents(Cell.class).filter(agent -> agent.alive).count();
  }

  @Variable
  public long born() {
    return bornAccumulator.value();
  }

  @Variable
  public long died() {
    return diedAccumulator.value();
  }

  public void setup() {

    grid.registerAgentTypes(Cell.class);
    grid.registerMessageTypes(Messages.Start.class, Messages.Neighbour.class);

    //set your Spark master URL below, default Spark Master URL is set to `local[*]`
    grid.getConfig().setBackend(new SparkAgentSystemBackend()/*.setMasterURL("<sparkMasterURL>")*/);

    Group cellsGroup =
        grid.getTopology()
            .generateGroup(
                100 * 100,
                init -> {
                  boolean alive = init.getPrng().uniform(0.0, 1.0).sample() < 0.25;

                  return new Cell(alive);
                });

    cellsGroup.connect(new GridConnected().wrapped().mooreConnected());

    grid.setup();
  }

  public void calculate() {
    bornAccumulator.reset();
    diedAccumulator.reset();

    grid.calculate(new Messages.Start());
  }
}
