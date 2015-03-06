package de.danielbechler.diff.comparison;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.DiffNode.State;

public class ArrayComparisonStrategy implements ComparisonStrategy {

   private static final Logger log = LoggerFactory.getLogger(ArrayComparisonStrategy.class);

   public void compare(final DiffNode node, final Class<?> type, final Object working, final Object base) {
      if (type == null || !type.isArray()) {
         log.warn("Failed to apply ArrayComparisonStrategy: Class is of type " + type);
         return;
      }

      boolean isEqual = true;
      if (type.getComponentType() == int.class) {
         isEqual = Arrays.equals((int[]) working, (int[]) base);
      } else if (type.getComponentType() == short.class) {
         isEqual = Arrays.equals((short[]) working, (short[]) base);
      } else if (type.getComponentType() == long.class) {
         isEqual = Arrays.equals((long[]) working, (long[]) base);
      } else if (type.getComponentType() == byte.class) {
         isEqual = Arrays.equals((byte[]) working, (byte[]) base);
      } else if (type.getComponentType() == boolean.class) {
         isEqual = Arrays.equals((boolean[]) working, (boolean[]) base);
      } else if (type.getComponentType() == char.class) {
         isEqual = Arrays.equals((char[]) working, (char[]) base);
      } else if (type.getComponentType() == double.class) {
         isEqual = Arrays.equals((double[]) working, (double[]) base);
      } else if (type.getComponentType() == float.class) {
         isEqual = Arrays.equals((float[]) working, (float[]) base);
      } else if (type.getComponentType() == Object.class) {
         isEqual = Arrays.equals((Object[]) working, (Object[]) base);
      }

      if (isEqual) {
         node.setState(State.UNTOUCHED);
      } else {
         node.setState(State.CHANGED);
      }
   }

}
