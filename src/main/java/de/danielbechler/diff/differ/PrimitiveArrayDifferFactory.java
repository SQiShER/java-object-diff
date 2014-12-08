package de.danielbechler.diff.differ;

import de.danielbechler.diff.NodeQueryService;

public class PrimitiveArrayDifferFactory implements DifferFactory {

   public Differ createDiffer(DifferDispatcher differDispatcher, NodeQueryService nodeQueryService) {
      return new PrimitiveArrayDiffer();
   }

}
