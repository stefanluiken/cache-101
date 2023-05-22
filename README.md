# Database Caching 101
A cache is a software or hardware component that stores the data in such a way, that future requests for that data can be executed faster. Here, we will focus on database caching. Database caching is very important for database driven applications:

- It stores the data in local memory on the server and helps avoid extra trips to the database for retrieving data that has not changed.
- For most database solutions, cache frequently used queries in order to reduce turnaround time.
- It is standard practice to clear any cache data after it has been altered.

For scalability purposes, we could even create distributed caches. A distributed cache has its data spread across several clusters around the world.
Under the hood, a distributed cache is:

- A distributed Hash table that has the responsibility of mapping Object Values to Keys.
- A hash table manages the addition, deletion, and failure of nodes as long as the cache is online.

To keep the cache coherent with the database, we may have to invalidate the cache. We can invalidate caches through different strategies:

- Cache Aside (Read through): when there is a client request for data, it will try the cache first. If the data is available within the cache, the data is returned to the client. If not, it will check the underlying database. In case the data exists in the database, it is returned to the application which will return it to the client and copies it to the cache (with read through, the cache provider will take care of copying values to the cache in case of a cache miss).
- Write through: data is first written to the cache and then to the database. The cache is coherent with the database and writes always go through the cache to the main database.
- Write around: the data is written to the database directly. Only the data that is read makes it way into the cache.
- Write back: the application writes data to the cache and acknowledges this to the application immediately. Only later, the cache writes the data back to the database.

A cache eviction algorithm is a way of deciding which element to remove when the cache is full. The following are some of the most common cache eviction policies:

- Least Recently Used (LRU)
- Least Frequently Used (LFU)
- First In First Out (FIFO)
- Last In First Out (LIFO)

We can introduce an example cache using:

- HashMap to get and put data in constant time.
- Using a Doubly linked list data structure, which is also commonly used for LRU cache implementations.

Below is an example of a Least Recently Used (LRU) cache implementation:

```java
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<K, V> {
    private ConcurrentHashMap<K, CacheItem> map;
    private CacheItem first;
    private CacheItem last;
    private int size;
    private final int CAPACITY;
    private int hitCount = 0;
    private int missCount = 0;
    public Cache(int capacity) {
        CAPACITY = capacity;  //
        map = new ConcurrentHashMap<>(CAPACITY);
    }

    public void put(K key, V value) {
        CacheItem node = new CacheItem(key, value);

        if(map.containsKey(key) == false) {
            if(size() >= CAPACITY) {
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
```

```java
public class CacheItem<K, V> {
    private K key;
    private V value;
    private int hitCount = 0;
    private CacheItem prev;
    private CacheItem next;

    public CacheItem(K key, V value) {
        this.value = value;
        this.key = key;
    }

    public void incrementHitCount() {
        this.hitCount++;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public CacheItem getPrev() {
        return prev;
    }

    public void setPrev(CacheItem prev) {
        this.prev = prev;
    }

    public CacheItem getNext() {
        return next;
    }

    public void setNext(CacheItem next) {
        this.next = next;
    }
}
```

```java
public class CacheImplementation {

    public static void main(String[] args) {
        Cache cache = new Cache<>(3);
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        cache.put(6, 600);
        //cache.put(1, 100);

        //cache.getHitCount();
        cache.get(3);
    }
}
```