package org.example.models.conway;

import simudyne.core.Model;
import simudyne.core.abm.AgentSystem;
import simudyne.core.abm.GlobalState;
import simudyne.core.abm.Sequence;
import simudyne.core.abm.topology.Group;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Custom;
import simudyne.core.annotations.Input;
import simudyne.core.annotations.Variable;
import simudyne.core.graph.BlankLink;
import simudyne.core.graph.LongAccumulator;

public class GameOfLife implements Model {
  @Custom
  public AgentSystem<Globals> grid = AgentSystem.create(new Globals());

  private LongAccumulator bornAccumulator = grid.createLongAccumulator("born");
  private LongAccumulator diedAccumulator = grid.createLongAccumulator("died");

  public static final class Globals extends GlobalState {
    @Constant
    public float fillFactor = 0.25f;
  }

  @Variable
  public long aliveCells() {
    return grid.select(Cell.class).filter(agent -> agent.alive).count();
  }

  @Input
  public int gridSize = 100;

  public void setup() {

    grid.registerAgentType(Cell.class);
    grid.registerMessageTypes(Boolean.class);

    Group cellsGroup =
      grid.generateGroup(
        Cell.class,
        gridSize * gridSize,
        cell -> {
          cell.alive = cell.getPrng().uniform(0.0, 1.0).sample() < cell.getGlobals().fillFactor;
        });

    cellsGroup.gridConnected(BlankLink.class).wrapped().mooreConnected();

    grid.setup();
  }

  public void step() {
    bornAccumulator.reset();
    diedAccumulator.reset();

    Sequence.create(Cell.action(Cell::onStart), Cell.action(Cell::onNeighbourMessages)).run(grid);
  }
}
