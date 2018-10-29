package org.example.models.advanced1;

import java.util.List;
import org.example.models.advanced1.Messages.RepaymentAmount;
import simudyne.core.abm.Agent;
import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Variable;

public class Bank extends Agent<GlobalState> {
  @Variable private int debt = 90;

  void updateBalanceSheet() {
    int assets = 0;
    List<RepaymentAmount> paymentMessages =
        getMessagesOfType(Messages.RepaymentAmount.class);
    for (Messages.RepaymentAmount payment : paymentMessages) {
      assets += payment.getBody();
    }

    getLongAccumulator("assets").add(assets);
    getLongAccumulator("equity").add(assets - this.debt);
  }
}
