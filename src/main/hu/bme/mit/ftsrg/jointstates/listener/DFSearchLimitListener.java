package hu.bme.mit.ftsrg.jointstates.listener;

import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListenerAdapter;

public class DFSearchLimitListener extends SearchListenerAdapter {
  @Override
  public void stateAdvanced(Search search) {
    super.stateAdvanced(search);

    // search.getTransition().getChoiceGenerator().setDone();
  }
}
