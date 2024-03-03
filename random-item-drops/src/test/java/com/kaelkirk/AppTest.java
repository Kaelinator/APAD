package com.kaelkirk;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import com.kaelkirk.util.WeightedTreeNode;

public class AppTest 
{

  @Test
  public void RootWithWeight1()
  {
    Random r = new Random();
    WeightedTreeNode<Character> root = new WeightedTreeNode<Character>("root", 1);
    root.addValue('a');

    assertTrue(root.pickValueRandomly(r) == 'a');
  }

  @Test
  public void SimpleCaseDepth1() {

    Random r;
    WeightedTreeNode<Character> root = new WeightedTreeNode<>("root", 1);
    WeightedTreeNode<Character> child1 = new WeightedTreeNode<>("child1", 1);
    WeightedTreeNode<Character> child2 = new WeightedTreeNode<>("child2", 1);
    WeightedTreeNode<Character> child3 = new WeightedTreeNode<>("child3", 1);
    root.addChild(child1);
    root.addChild(child2);
    root.addChild(child3);

    child1.addValue('a');
    child1.addValue('A');
    child2.addValue('b');
    child2.addValue('B');
    child3.addValue('c');
    child3.addValue('C');

    r = new DeterministicRandom(0, 0);
    assertTrue(root.pickValueRandomly(r) == 'b');
    r = new DeterministicRandom(0, 1);
    assertTrue(root.pickValueRandomly(r) == 'B');

    r = new DeterministicRandom(1, 0);
    assertTrue(root.pickValueRandomly(r) == 'c');
    r = new DeterministicRandom(1, 1);
    assertTrue(root.pickValueRandomly(r) == 'C');

    r = new DeterministicRandom(2, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
    r = new DeterministicRandom(2, 1);
    assertTrue(root.pickValueRandomly(r) == 'A');
  }

  @Test
  public void SimpleCaseDepth2() {

    Random r;
    WeightedTreeNode<Character> root = new WeightedTreeNode<>("root", 1);
    WeightedTreeNode<Character> child1 = new WeightedTreeNode<>("child1", 1);
    WeightedTreeNode<Character> child2 = new WeightedTreeNode<>("child2", 1);
    WeightedTreeNode<Character> child3 = new WeightedTreeNode<>("child3", 1);
    root.addChild(child1);
    child1.addChild(child2);
    child1.addChild(child3);

    child2.addValue('b');
    child3.addValue('c');

    r = new DeterministicRandom(0, 0, 0);
    assertTrue(root.pickValueRandomly(r) == 'b');
    r = new DeterministicRandom(0, 1, 0);
    assertTrue(root.pickValueRandomly(r) == 'c');
  }


  @Test
  public void SimpleWeightedCaseDepth1() {

    Random r;
    WeightedTreeNode<Character> root = new WeightedTreeNode<>("root", 1);
    WeightedTreeNode<Character> child1 = new WeightedTreeNode<>("child1", 5);
    WeightedTreeNode<Character> child2 = new WeightedTreeNode<>("child2", 3);
    WeightedTreeNode<Character> child3 = new WeightedTreeNode<>("child3", 1);
    root.addChild(child1);
    root.addChild(child2);
    root.addChild(child3);

    child1.addValue('a');
    child2.addValue('b');
    child3.addValue('c');

    r = new DeterministicRandom(0, 0);
    assertTrue(root.pickValueRandomly(r) == 'b');
    r = new DeterministicRandom(1, 0);
    assertTrue(root.pickValueRandomly(r) == 'b');
    r = new DeterministicRandom(2, 0);
    assertTrue(root.pickValueRandomly(r) == 'b');
    r = new DeterministicRandom(3, 0);
    assertTrue(root.pickValueRandomly(r) == 'c');
    r = new DeterministicRandom(4, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
    r = new DeterministicRandom(5, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
    r = new DeterministicRandom(6, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
    r = new DeterministicRandom(7, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
    r = new DeterministicRandom(8, 0);
    assertTrue(root.pickValueRandomly(r) == 'a');
  }

  public class DeterministicRandom extends Random {

    private int index;
    private int[] values;
    public DeterministicRandom(int... values) {
      this.values = values;
    }

    @Override
    public int nextInt(int n) {
      if (index < values.length) {
        return values[index++];
      }
      throw new Error("Called nextInt " + (index + 1) + " time(s), expected " + values.length);
    }
  }
}
