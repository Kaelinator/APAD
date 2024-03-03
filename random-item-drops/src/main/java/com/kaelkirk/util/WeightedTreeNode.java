package com.kaelkirk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WeightedTreeNode<T> {

  private List<T> values; // only leaves have values
  private Map<String, WeightedTreeNode<T>> children; // only non-leaves have children
  private int weight;
  private int childrenWeight;
  private String key;
  
  public WeightedTreeNode(String key, int weight) {
    this.key = key;
    if (weight <= 0) {
      throw new Error("weight cannot equal 0");
    }
    this.weight = weight;
    children = new HashMap<String, WeightedTreeNode<T>>();
    values = new ArrayList<T>();
  }

  public void addChild(WeightedTreeNode<T> child) {
    if (values.size() != 0) {
      throw new Error("Cannot add child to leaf");
    }
    children.put(child.key, child);
    childrenWeight += child.weight;
  }

  public void addValue(T value) {
    if (children.size() != 0) {
      throw new Error("Cannot add value to non-leaf");
    }
    values.add(value);
  }

  public boolean hasChild(String childKey) {
    return children.containsKey(childKey);
  }

  public WeightedTreeNode<T> getChild(String childKey) {
    return children.get(childKey);
  }

  public boolean isLeaf() {
    return values == null || values.size() > 0;
  }

  public boolean hasChildren() {
    return children != null && children.size() > 0;
  }

  public T pickValueRandomly(Random random) {

    if (isLeaf()) {
      if (values.size() == 0) {
        throw new Error("Leaf element does not have any values");
      }
      return values.get(random.nextInt(values.size()));
    }

    if (children.size() == 0) {
      throw new Error("Parent element does not have any children");
    }
    int randomOrdinal = random.nextInt(childrenWeight);

    WeightedTreeNode<T> selectedChild = null;
    for (WeightedTreeNode<T> child : children.values()) {
      if (child.weight > randomOrdinal) {
        selectedChild = child;
        break;
      }
      randomOrdinal -= child.weight;
    }

    return selectedChild.pickValueRandomly(random);
  }

  public void printAll(int depth) {
    if (depth == -1) {
      printAll(Integer.MAX_VALUE);
      return;
    }

    printAll(depth, 1);
  }

  private void printAll(int depth, double chance) {
    if (depth == -1) {
      return;
    }

    System.out.println("PARENT:" + key + " = " + chance);

    if (isLeaf()) {
      for (T value : values) {
        System.out.println(value + " = " + (chance / values.size()));
      }
      return;
    }

    for (WeightedTreeNode<T> child : children.values()) {
      child.printAll(depth - 1, chance * ((double) child.weight / childrenWeight));
    }
  }

}
