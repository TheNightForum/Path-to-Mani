

package org.burntgameproductions.PathToMani.game.item;

import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.Const;

import java.util.*;

public class ItemContainer implements Iterable<List<ManiItem>> {
  public static final int MAX_GROUP_COUNT = 4 * Const.ITEM_GROUPS_PER_PAGE;
  public static final int MAX_GROUP_SZ = 30;

  private List<List<ManiItem>> myGroups;
  private Set<List<ManiItem>> myNewGroups;
  private int mySize;

  public ItemContainer() {
    myGroups = new ArrayList<List<ManiItem>>();
    myNewGroups = new HashSet<List<ManiItem>>();
  }

  public boolean tryConsumeItem(ManiItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      ManiItem item = group.get(0);
      if (!example.isSame(item)) continue;
      remove(item);
      return true;
    }
    return false;
  }

  public int count(ManiItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      ManiItem item = group.get(0);
      if (example.isSame(item)) return group.size();
    }
    return 0;
  }

  public boolean canAdd(ManiItem example) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      ManiItem item = group.get(0);
      if (item.isSame(example)) return group.size() < MAX_GROUP_SZ;
    }
    return myGroups.size() < MAX_GROUP_COUNT;
  }

  public void add(ManiItem addedItem) {
    if (addedItem == null) throw new AssertionError("adding null item");
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      ManiItem item = group.get(0);
      if (item.isSame(addedItem)) {
        if (group.size() >= MAX_GROUP_SZ) throw new AssertionError("reached group size limit");
        group.add(addedItem);
        mySize++;
        return;
      }
    }
    if (myGroups.size() >= MAX_GROUP_COUNT) throw new AssertionError("reached group count limit");
    ArrayList<ManiItem> group = new ArrayList<ManiItem>();
    group.add(addedItem);
    myGroups.add(0, group);
    mySize++;
    myNewGroups.add(group);
  }

  @Override
  public Iterator<List<ManiItem>> iterator() {
    return new Itr();
  }

  public int groupCount() {
    return myGroups.size();
  }

  public int size() {
    return mySize;
  }

  public boolean contains(ManiItem item) {
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      if (group.contains(item)) return true;
    }
    return false;
  }

  public void remove(ManiItem item) {
    List<ManiItem> remGroup = null;
    boolean removed = false;
    for (int i = 0, myGroupsSize = myGroups.size(); i < myGroupsSize; i++) {
      List<ManiItem> group = myGroups.get(i);
      removed = group.remove(item);
      if (group.isEmpty()) remGroup = group;
      if (removed) break;
    }
    if (removed) mySize--;
    if (remGroup != null) {
      myGroups.remove(remGroup);
      myNewGroups.remove(remGroup);
    }
  }

  public List<ManiItem> getSelectionAfterRemove(List<ManiItem> selected) {
    if (selected.size() > 1) return selected;
    int idx = myGroups.indexOf(selected) + 1;
    if (idx <= 0 || idx >= groupCount()) return null;
    return myGroups.get(idx);
  }

  public ManiItem getRandom() {
    return myGroups.isEmpty() ? null : ManiMath.elemRnd(ManiMath.elemRnd(myGroups));
  }

  public boolean isNew(List<ManiItem> group) {
    return myNewGroups.contains(group);
  }

  public void seen(List<ManiItem> group) {
    myNewGroups.remove(group);
  }

  public void seenAll() {
    myNewGroups.clear();
  }

  public boolean hasNew() {
    return !myNewGroups.isEmpty();
  }

  public int getCount(int groupIdx) {
    return myGroups.get(groupIdx).size();
  }

  public boolean containsGroup(List<ManiItem> group) {
    return myGroups.contains(group);
  }

  public List<ManiItem> getGroup(int groupIdx) {
    return myGroups.get(groupIdx);
  }

  public void clear() {
    myGroups.clear();
    myNewGroups.clear();
    mySize = 0;
  }

  private class Itr implements Iterator<List<ManiItem>> {
    int myCur;       // index of next element to return

    public boolean hasNext() {
      return myCur != myGroups.size();
    }

    public List<ManiItem> next() {
      return myGroups.get(myCur++);
    }

    @Override
    public void remove() {
      throw new AssertionError("tried to remove via item iterator");
    }
  }
}
