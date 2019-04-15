package org.example.models.conway;

import java.util.List;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;
import simudyne.core.functions.SerializableConsumer;

public class Cell extends Agent<GameOfLife.Globals> {
  public static Action<Cell> action(SerializableConsumer<Cell> action) {
    return Action.create(Cell.class, action);
  }

  @Variable public boolean alive;

  public void onStart() {
    getLinks(Links.Neighbour.class).send(Messages.Alive.class, alive);
  }

  public void onNeighbourMessages() {
    long liveNeighbours = 0;

    List<Messages.Alive> messages = getMessagesOfType(Messages.Alive.class);
    for (Messages.Alive m : messages) {
      if (m.getBody()) {
        liveNeighbours += 1;
      }
    }

    if (alive && (liveNeighbours < 2 || liveNeighbours > 3)) {
      getLongAccumulator("died").add(1);
      alive = false;
    } else if (!alive && liveNeighbours == 3) {
      getLongAccumulator("born").add(1);
      alive = true;
    }
  }
}
