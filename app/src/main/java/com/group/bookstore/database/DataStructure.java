package com.group.bookstore.database;

/**
 * @author Yusen Nian
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

public class DataStructure {
}

class BPlusNode<K extends Comparable<K>, V> {

    // 是否为根节点
    protected boolean isRoot;

    // 是否为叶子节点
    protected boolean isLeaf;

    // 父节点
    protected BPlusNode<K, V> parent;

    // 叶节点的前节点
    protected BPlusNode<K, V> pre;

    // 叶节点的后节点
    protected BPlusNode<K, V> next;

    // 节点的关键字列表
    protected List<Entry<K, V>> entryList;

    // 子节点列表
    protected List<BPlusNode<K, V>> childrenList;

    public BPlusNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        entryList = new ArrayList();

        if (!isLeaf) {
            childrenList = new ArrayList();
        }
    }

    public BPlusNode(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }


    //按key查找返回value
    public V fuzzyGet(K key) {
        //如果是叶子节点
        if (isLeaf == true) {
            int low = 0, high = entryList.size() - 1, mid = 0, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return entryList.get(mid).getValue();
                } else if (comp > 0) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            if (entryList.get(low) != null)
                return entryList.get(low).getValue();
            else if (pre != null && pre.entryList.get(entryList.size() - 1) != null)
                return pre.entryList.get(entryList.size() - 1).getValue();
            else
                return null;
        }
        //如果不是叶子节点
        //如果key小于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(entryList.get(0).getKey()) < 0) {
            return childrenList.get(0).get(key);
            //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索
        } else if (key.compareTo(entryList.get(entryList.size() - 1).getKey()) >= 0) {
            //否则沿比key大的前一个子节点继续搜索
            return childrenList.get(childrenList.size() - 1).get(key);
        } else {
            int low = 0, high = entryList.size() - 1, mid = 0, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return childrenList.get(mid + 1).get(key);
                } else if (comp > 0) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            return childrenList.get(low).get(key);
        }
    }

    //按key查找返回value
    public V get(K key) {
        //如果是叶子节点
        if (isLeaf == true) {
            int low = 0, high = entryList.size() - 1, mid, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return entryList.get(mid).getValue();
                } else if (comp > 0) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            return null;
        }
        //如果不是叶子节点
        //如果key小于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(entryList.get(0).getKey()) < 0) {
            return childrenList.get(0).get(key);
            //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索
        } else if (key.compareTo(entryList.get(entryList.size() - 1).getKey()) >= 0) {
            //否则沿比key大的前一个子节点继续搜索
            return childrenList.get(childrenList.size() - 1).get(key);
        } else {
            int low = 0, high = entryList.size() - 1, mid = 0, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return childrenList.get(mid + 1).get(key);
                } else if (comp > 0) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            return childrenList.get(low).get(key);
        }
    }

    public void insertOrUpdate(K key, V value, BPlusTree<K, V> tree) {
        //如果是叶子节点
        if (isLeaf) {
            //不需要分裂，直接插入或更新
            if (entryList.size() < tree.getOrder() || contains(key) != -1) {
                insertOrUpdate(key, value);
                if (tree.getHeight() == 0) {
                    tree.setHeight(1);
                }
                return;
            }
            //需要分裂成左右两个节点
            BPlusNode<K, V> right = new BPlusNode<K, V>(true);
            BPlusNode<K, V> left = new BPlusNode<K, V>(true);
            //设置链接
            if (pre != null) {
                pre.next = left;
                left.pre = pre;
            }
            if (next != null) {
                next.pre = right;
                right.next = next;
            }
            if (pre == null) {
                tree.setHead(left);
            }

            left.next = right;
            right.pre = left;
            next = null;
            pre = null;

            //复制原节点关键字到分裂出来的新节点
            copy2Nodes(key, value, left, right, tree);

            //如果不是根节点
            if (parent == null) {
                //如果是根节点
                isRoot = false;
                BPlusNode<K, V> parent = new BPlusNode<K, V>(false, true);
                tree.setRoot(parent);
                left.parent = parent;
                right.parent = parent;
                parent.childrenList.add(left);
                parent.childrenList.add(right);
                parent.entryList.add(right.entryList.get(0));
                entryList = null;
                childrenList = null;
            } else {
                //如果不是根节点
                //调整父子节点关系
                int index = parent.childrenList.indexOf(this);
                parent.childrenList.remove(this);
                left.parent = parent;
                right.parent = parent;
                parent.childrenList.add(index, left);
                parent.childrenList.add(index + 1, right);
                parent.entryList.add(index, right.entryList.get(0));
                entryList = null; //删除当前节点的关键字信息
                childrenList = null; //删除当前节点的孩子节点引用

                //父节点插入或更新关键字
                parent.updateInsert(tree);
                parent = null; //删除当前节点的父节点引用
            }
            return;

        }
        //如果不是叶子节点
        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(entryList.get(0).getKey()) < 0) {
            childrenList.get(0).insertOrUpdate(key, value, tree);
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        } else if (key.compareTo(entryList.get(entryList.size() - 1).getKey()) >= 0) {
            childrenList.get(childrenList.size() - 1).insertOrUpdate(key, value, tree);
            //否则沿比key大的前一个子节点继续搜索
        } else {
            int low = 0, high = entryList.size() - 1, mid = 0, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    childrenList.get(mid + 1).insertOrUpdate(key, value, tree);
                    break;
                } else if (comp > 0) {
                    low = mid + 1;
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            if (low > high) {
                childrenList.get(low).insertOrUpdate(key, value, tree);
            }
        }
    }

    private void copy2Nodes(K key, V value, BPlusNode<K, V> left,
                            BPlusNode<K, V> right, BPlusTree<K, V> tree) {
        //左右两个节点关键字长度
        int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
        boolean record = false;//用于记录新元素是否已经被插入
        for (int i = 0; i < entryList.size(); i++) {
            if (leftSize != 0) {
                leftSize -= 1;
                if (!record && entryList.get(i).getKey().compareTo(key) > 0) {
                    left.entryList.add(new SimpleEntry<K, V>(key, value));
                    record = true;
                    i -= 1;
                } else {
                    left.entryList.add(entryList.get(i));
                }
            } else {
                if (!record && entryList.get(i).getKey().compareTo(key) > 0) {
                    right.entryList.add(new SimpleEntry<K, V>(key, value));
                    record = true;
                    i -= 1;
                } else {
                    right.entryList.add(entryList.get(i));
                }
            }
        }
        if (record == false) {
            right.entryList.add(new SimpleEntry<K, V>(key, value));
        }
    }

    /**
     * 插入节点后中间节点的更新
     */
    protected void updateInsert(BPlusTree<K, V> tree) {

        //如果子节点数超出阶数，则需要分裂该节点
        if (childrenList.size() > tree.getOrder()) {
            //分裂成左右两个节点
            BPlusNode<K, V> right = new BPlusNode<K, V>(false);
            BPlusNode<K, V> left = new BPlusNode<K, V>(false);
            //左右两个节点子节点的长度
            int rightSize = (tree.getOrder() + 1) / 2;
            int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
            //复制子节点到分裂出来的新节点，并更新关键字
            for (int i = 0; i < leftSize; i++) {
                left.childrenList.add(childrenList.get(i));
                childrenList.get(i).parent = left;
            }
            for (int i = 0; i < rightSize; i++) {
                right.childrenList.add(childrenList.get(leftSize + i));
                childrenList.get(leftSize + i).parent = right;
            }
            for (int i = 0; i < leftSize - 1; i++) {
                left.entryList.add(entryList.get(i));
            }
            for (int i = 0; i < rightSize - 1; i++) {
                right.entryList.add(entryList.get(leftSize + i));
            }

            //如果不是根节点
            if (parent != null) {
                //调整父子节点关系
                int index = parent.childrenList.indexOf(this);
                parent.childrenList.remove(this);
                right.parent = parent;
                left.parent = parent;
                parent.childrenList.add(index, left);
                parent.childrenList.add(index + 1, right);
                parent.entryList.add(index, entryList.get(leftSize - 1));
                entryList = null;
                childrenList = null;

                //父节点更新关键字
                parent.updateInsert(tree);
                parent = null;
                //如果是根节点
            } else {
                isRoot = false;
                BPlusNode<K, V> parent = new BPlusNode<K, V>(false, true);
                tree.setRoot(parent);
                tree.setHeight(tree.getHeight() + 1);
                right.parent = parent;
                left.parent = parent;
                parent.childrenList.add(left);
                parent.childrenList.add(right);
                parent.entryList.add(entryList.get(leftSize - 1));
                entryList = null;
                childrenList = null;
            }
        }
    }

    /**
     * 删除节点后中间节点的更新
     */
    protected void updateRemove(BPlusTree<K, V> tree) {

        // 如果子节点数小于M / 2或者小于2，则需要合并节点
        if (childrenList.size() < tree.getOrder() / 2 || childrenList.size() < 2) {
            if (isRoot == true) {
                // 如果是根节点并且子节点数大于等于2，OK
                if (childrenList.size() >= 2) return;
                // 否则与子节点合并
                BPlusNode<K, V> root = childrenList.get(0);
                tree.setRoot(root);
                tree.setHeight(tree.getHeight() - 1);
                root.parent = null;
                root.isRoot = true;
                entryList = null;
                childrenList = null;
                return;
            }
            //计算前后节点
            int currIdx = parent.childrenList.indexOf(this);
            int prevIdx = currIdx - 1;
            int nextIdx = currIdx + 1;
            BPlusNode<K, V> previous = null, next = null;
            if (prevIdx >= 0) {
                previous = parent.childrenList.get(prevIdx);
            }
            if (nextIdx < parent.childrenList.size()) {
                next = parent.childrenList.get(nextIdx);
            }

            // 如果前节点子节点数大于M / 2并且大于2，则从其处借补
            if (previous != null && previous.childrenList.size() > tree.getOrder() / 2 && previous.childrenList.size() > 2) {
                //前叶子节点末尾节点添加到首位
                int index = previous.childrenList.size() - 1;
                BPlusNode<K, V> borrow = previous.childrenList.get(index);
                previous.childrenList.remove(index);
                borrow.parent = this;
                childrenList.add(0, borrow);
                int preIndex = parent.childrenList.indexOf(previous);

                entryList.add(0, parent.entryList.get(preIndex));
                parent.entryList.set(preIndex, previous.entryList.remove(index - 1));
                return;
            }

            // 如果后节点子节点数大于M / 2并且大于2，则从其处借补
            if (next != null && next.childrenList.size() > tree.getOrder() / 2 && next.childrenList.size() > 2) {
                //后叶子节点首位添加到末尾
                BPlusNode<K, V> node = next.childrenList.get(0);
                next.childrenList.remove(0);
                node.parent = this;
                childrenList.add(node);
                int preIndex = parent.childrenList.indexOf(this);
                entryList.add(parent.entryList.get(preIndex));
                parent.entryList.set(preIndex, next.entryList.remove(0));
                return;
            }

            // 同前面节点合并
            if (previous != null && (previous.childrenList.size() <= 2 || previous.childrenList.size() <= tree.getOrder() / 2)) {
                for (int i = 0; i < childrenList.size(); i++) {
                    previous.childrenList.add(childrenList.get(i));
                }
                for (int i = 0; i < previous.childrenList.size(); i++) {
                    previous.childrenList.get(i).parent = this;
                }
                int indexPre = parent.childrenList.indexOf(previous);
                previous.entryList.add(parent.entryList.get(indexPre));
                for (int i = 0; i < entryList.size(); i++) {
                    previous.entryList.add(entryList.get(i));
                }
                childrenList = previous.childrenList;
                entryList = previous.entryList;

                //更新父节点的关键字列表
                parent.childrenList.remove(previous);
                previous.parent = null;
                previous.childrenList = null;
                previous.entryList = null;
                parent.entryList.remove(parent.childrenList.indexOf(this));
                if ((!parent.isRoot
                        && (parent.childrenList.size() >= tree.getOrder() / 2
                        && parent.childrenList.size() >= 2))
                        || parent.isRoot && parent.childrenList.size() >= 2) {
                    return;
                }
                parent.updateRemove(tree);
                return;
            }

            // 同后面节点合并
            if (next != null && (next.childrenList.size() <= 2 || next.childrenList.size() <= tree.getOrder() / 2)) {
                for (int i = 0; i < next.childrenList.size(); i++) {
                    BPlusNode<K, V> child = next.childrenList.get(i);
                    childrenList.add(child);
                    child.parent = this;
                }
                int index = parent.childrenList.indexOf(this);
                entryList.add(parent.entryList.get(index));
                for (int i = 0; i < next.entryList.size(); i++) {
                    entryList.add(next.entryList.get(i));
                }
                parent.childrenList.remove(next);
                next.parent = null;
                next.childrenList = null;
                next.entryList = null;
                parent.entryList.remove(parent.childrenList.indexOf(this));
                if ((!parent.isRoot && (parent.childrenList.size() >= tree.getOrder() / 2
                        && parent.childrenList.size() >= 2))
                        || parent.isRoot && parent.childrenList.size() >= 2) {
                    return;
                }
                parent.updateRemove(tree);
                return;
            }
        }
    }

    public V remove(K key, BPlusTree<K, V> tree) {
        //如果是叶子节点
        if (isLeaf == true) {
            //如果不包含该关键字，则直接返回
            if (contains(key) == -1) {
                return null;
            }
            //如果既是叶子节点又是根节点，直接删除
            if (isRoot) {
                if (entryList.size() == 1) {
                    tree.setHeight(0);
                }
                return remove(key);
            }
            //如果关键字数大于M / 2，直接删除
            if (entryList.size() > tree.getOrder() / 2 && entryList.size() > 2) {
                return remove(key);
            }
            //如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补
            if (pre != null &&
                    pre.parent == parent
                    && pre.entryList.size() > tree.getOrder() / 2
                    && pre.entryList.size() > 2) {
                //添加到首位
                int size = pre.entryList.size();
                entryList.add(0, pre.entryList.remove(size - 1));
                int index = parent.childrenList.indexOf(pre);
                parent.entryList.set(index, entryList.get(0));
                return remove(key);
            }
            //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
            if (next != null
                    && next.parent == parent
                    && next.entryList.size() > tree.getOrder() / 2
                    && next.entryList.size() > 2) {
                entryList.add(next.entryList.remove(0));
                int index = parent.childrenList.indexOf(this);
                parent.entryList.set(index, next.entryList.get(0));
                return remove(key);
            }

            //同前面节点合并
            if (pre != null
                    && pre.parent == parent
                    && (pre.entryList.size() <= tree.getOrder() / 2
                    || pre.entryList.size() <= 2)) {
                V returnValue = remove(key);
                for (int i = 0; i < entryList.size(); i++) {
                    //将当前节点的关键字添加到前节点的末尾
                    pre.entryList.add(entryList.get(i));
                }
                entryList = pre.entryList;
                parent.childrenList.remove(pre);
                pre.parent = null;
                pre.entryList = null;
                //更新链表
                if (pre.pre != null) {
                    BPlusNode<K, V> temp = pre;
                    temp.pre.next = this;
                    pre = temp.pre;
                    temp.pre = null;
                    temp.next = null;
                } else {
                    tree.setHead(this);
                    pre.next = null;
                    pre = null;
                }
                parent.entryList.remove(parent.childrenList.indexOf(this));
                if ((!parent.isRoot && (parent.childrenList.size() >= tree.getOrder() / 2
                        && parent.childrenList.size() >= 2))
                        || parent.isRoot && parent.childrenList.size() >= 2) {
                    return returnValue;
                }
                parent.updateRemove(tree);
                return returnValue;
            }
            //同后面节点合并
            if (next != null
                    && next.parent == parent
                    && (next.entryList.size() <= tree.getOrder() / 2
                    || next.entryList.size() <= 2)) {
                V returnValue = remove(key);
                for (int i = 0; i < next.entryList.size(); i++) {
                    //从首位开始添加到末尾
                    entryList.add(next.entryList.get(i));
                }
                next.parent = null;
                next.entryList = null;
                parent.childrenList.remove(next);
                //更新链表
                if (next.next != null) {
                    BPlusNode<K, V> temp = next;
                    temp.next.pre = this;
                    next = temp.next;
                    temp.pre = null;
                    temp.next = null;
                } else {
                    next.pre = null;
                    next = null;
                }
                //更新父节点的关键字列表
                parent.entryList.remove(parent.childrenList.indexOf(this));
                if ((!parent.isRoot && (parent.childrenList.size() >= tree.getOrder() / 2
                        && parent.childrenList.size() >= 2))
                        || parent.isRoot && parent.childrenList.size() >= 2) {
                    return returnValue;
                }
                parent.updateRemove(tree);
                return returnValue;
            }
        }
        /*如果不是叶子节点*/

        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(entryList.get(0).getKey()) < 0) {
            return childrenList.get(0).remove(key, tree);
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        } else if (key.compareTo(entryList.get(entryList.size() - 1).getKey()) >= 0) {
            return childrenList.get(childrenList.size() - 1).remove(key, tree);
            //否则沿比key大的前一个子节点继续搜索
        } else {
            int low = 0, high = entryList.size() - 1, mid = 0, comp;
            while (high >= low) {
                mid = (high + low) / 2;
                comp = entryList.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return childrenList.get(mid + 1).remove(key, tree);
                } else if (comp > 0) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            return childrenList.get(low).remove(key, tree);
        }
    }

    // 判断当前节点是否包含该关键字
    protected int contains(K key) {
        int low = 0, high = entryList.size() - 1, mid, comp;
        while (low <= high) {
            mid = (high + low) / 2;
            comp = entryList.get(mid).getKey().compareTo(key);
            if (comp == 0) {
                return mid;
            } else if (comp > 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    // 插入到当前节点的关键字中
    protected void insertOrUpdate(K key, V value) {
        //二叉查找，插入
        int low = 0, high = entryList.size() - 1, mid, comp;
        while (high >= low) {
            mid = (high + low) / 2;
            comp = entryList.get(mid).getKey().compareTo(key);
            if (comp == 0) {
                entryList.get(mid).setValue(value);
                break;
            } else if (comp > 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        if (low > high) {
            entryList.add(low, new SimpleEntry<K, V>(key, value));
        }
    }

    // 删除节点
    protected V remove(K key) {
        int low = 0, high = entryList.size() - 1, mid, comp;
        while (high >= low) {
            mid = (high + low) / 2;
            comp = entryList.get(mid).getKey().compareTo(key);
            if (comp == 0) {
                return entryList.remove(mid).getValue();
            } else if (comp > 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("isRoot: ");
        string.append(isRoot);
        string.append(", ");
        string.append("isLeaf: ");
        string.append(isLeaf);
        string.append(", ");
        string.append("keys: ");
        for (Entry<K, V> entry : entryList) {
            string.append(entry.getKey());
            string.append(", ");
        }
        string.append(", ");
        return string.toString();
    }

    public void printBPlusTree(int index) {
        if (this.isLeaf) {
            System.out.print("层数：" + index + ",叶子节点，keys为: ");
            for (int i = 0; i < entryList.size(); ++i)
                System.out.print(entryList.get(i) + " ");
            System.out.println();
        } else {
            System.out.print("层数：" + index + ",非叶子节点，keys为: ");
            for (int i = 0; i < entryList.size(); ++i)
                System.out.print(entryList.get(i) + " ");
            System.out.println();
            for (int i = 0; i < childrenList.size(); ++i)
                childrenList.get(i).printBPlusTree(index + 1);
        }
    }
}


class BPlusTree<K extends Comparable<K>, V> {

    // 根节点
    protected BPlusNode<K, V> root;

    // 阶数，M值
    protected int order;

    // 叶子节点的链表头
    protected BPlusNode<K, V> head;

    // 树高
    protected int height = 0;

    public BPlusNode<K, V> getHead() {
        return head;
    }

    public void setHead(BPlusNode<K, V> head) {
        this.head = head;
    }

    public BPlusNode<K, V> getRoot() {
        return root;
    }

    public void setRoot(BPlusNode<K, V> root) {
        this.root = root;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public V get(K key) {
        return root.get(key);
    }

    public V fuzzyGet(K key) {
        return root.fuzzyGet(key);
    }

    public V remove(K key) {
        return root.remove(key, this);
    }

    public void insertOrUpdate(K key, V value) {
        root.insertOrUpdate(key, value, this);
    }

    public BPlusTree(int order) {
        if (order < 3) {
            System.out.print("order must be greater than 2");
            System.exit(0);
        }
        this.order = order;
        root = new BPlusNode<K, V>(true, true);
        head = root;
    }

    public void printBPlusTree() {
        this.root.printBPlusTree(0);
    }
}

class BPlusTreeTest {
    // 测试
    public static void main(String[] args) {

        int size = 10;
        int order = 3;
        testRandomInsert(size, order);

        testOrderInsert(size, order);

        testRandomSearch(size, order);

        testOrderSearch(size, order);

        testRandomRemove(size, order);

        testOrderRemove(size, order);
    }

    private static void testOrderRemove(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest order remove " + size + " datas, of order:"
                + order);
        System.out.println("Begin order insert...");
        for (int i = 0; i < size; i++) {
            tree.insertOrUpdate(i, i);
        }
        tree.printBPlusTree();
        System.out.println("Begin order remove...");
        long current = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            if (tree.remove(j) == null) {
                System.err.println("得不到数据:" + j);
                break;
            }
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        tree.printBPlusTree();
        System.out.println(tree.getHeight());
    }

    private static void testRandomRemove(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest random remove " + size + " datas, of order:"
                + order);
        Random random = new Random();
        boolean[] a = new boolean[size + 10];
        List<Integer> list = new ArrayList<Integer>();
        int randomNumber = 0;
        System.out.println("Begin random insert...");
        for (int i = 0; i < size; i++) {
            randomNumber = random.nextInt(size);
            a[randomNumber] = true;
            list.add(randomNumber);
            tree.insertOrUpdate(randomNumber, randomNumber);
        }
        tree.printBPlusTree();
        System.out.println("Begin random remove...");
        long current = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            randomNumber = list.get(j);
            if (a[randomNumber]) {
                if (tree.remove(randomNumber) == null) {
                    System.err.println("得不到数据:" + randomNumber);
                    break;
                } else {
                    a[randomNumber] = false;
                }
            }
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        tree.printBPlusTree();
        System.out.println(tree.getHeight());
    }

    private static void testOrderSearch(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest order search " + size + " datas, of order:"
                + order);
        System.out.println("Begin order insert...");
        for (int i = 0; i < size; i++) {
            tree.insertOrUpdate(i, i);
        }
        tree.printBPlusTree();
        System.out.println("Begin order search...");
        long current = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            if (tree.get(j) == null) {
                System.err.println("得不到数据:" + j);
                break;
            }
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
    }

    private static void testRandomSearch(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest random search " + size + " datas, of order:"
                + order);
        Random random = new Random();
        boolean[] a = new boolean[size + 10];
        int randomNumber = 0;
        System.out.println("Begin random insert...");
        for (int i = 0; i < size; i++) {
            randomNumber = random.nextInt(size);
            a[randomNumber] = true;
            tree.insertOrUpdate(randomNumber, randomNumber);
        }
        tree.printBPlusTree();
        System.out.println("Begin random search...");
        long current = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            randomNumber = random.nextInt(size);
            if (a[randomNumber]) {
                if (tree.get(randomNumber) == null) {
                    System.err.println("得不到数据:" + randomNumber);
                    break;
                }
            }
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
    }

    private static void testRandomInsert(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest random insert " + size + " datas, of order:"
                + order);
        Random random = new Random();
        int randomNumber = 0;
        long current = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            randomNumber = random.nextInt(size * 10);
            System.out.print(randomNumber + " ");
            tree.insertOrUpdate(randomNumber, randomNumber);
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        tree.printBPlusTree();
        System.out.println(tree.getHeight());
    }

    private static void testOrderInsert(int size, int order) {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>(order);
        System.out.println("\nTest order insert " + size + " datas, of order:"
                + order);
        long current = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            tree.insertOrUpdate(i, i);
        }
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
        tree.printBPlusTree();
    }

}