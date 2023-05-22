import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<K, V> {
    private int cacheCapacity;
    private ConcurrentHashMap<K, CacheItem> map;
    private CacheItem first;
    private CacheItem last;
    private int size;

    private int hitCount = 0;
    private int missCount = 0;
    public Cache(int capacity) {
        cacheCapacity = capacity;
        map = new ConcurrentHashMap<>(cacheCapacity);
    }

    public void put(K key, V value) {
        CacheItem node = new CacheItem(key, value);

        if(map.containsKey(key) == false) {
            if(size() >= cacheCapacity) {
                deleteNode(first);
            }
            addNodeToLast(node);
        }
        map.put(key, node);
    }

    private void deleteNode(CacheItem node) {
        if(node == null) {
            return;
        }
        if(last == node) {
            last = node.getPrev();
        }
        if(first == node) {
            first = node.getNext();
        }
        map.remove(node.getKey());
        size--;
    }

    private void addNodeToLast(CacheItem node) {
        if(last != null) {
            last.setNext(node);
            node.setPrev(last);
        }

        last = node;
        if(first == null) {
            first = node;
        }
        size++;
    }

    public V get(K key) {
        if(map.containsKey(key) == false) {
            return null;
        }
        CacheItem node = (CacheItem) map.get(key);
        node.incrementHitCount();
        reorder(node);
        return (V) node.getValue();
    }

    // Least Recently Used (LRU) cache, we delete the candidate which is the oldest entry used.
    // Therefore, we have to sort items based on how recently the item was used.
    // The last accessed node will be at the end of the entire chain end, preceded by other newly added items.
    // In this way, we know that "first" is the least recently used node.
    private void reorder(CacheItem node) {
        if(last == node) {
            return;
        }
        if(first == node) {
            first = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
        }
        last.setNext(node);
        node.setPrev(last);
        node.setNext(null);
        last = node;
    }

    public int size() {
        return size;
    }
    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }
}
