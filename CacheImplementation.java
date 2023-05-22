
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
