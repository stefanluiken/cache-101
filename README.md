# Database caching 101
A cache is a software or hardware component that stores the data in such a way, that future requests for that data can be executed faster.

Here, we will focus on database caching. Database Caching is very important for Database driven applications:

- It stores the data in local memory on the server and helps avoid extra trips to the database for retrieving data that has not changed.
- For most database solutions, cache frequently used queries in order to reduce turnaround time.
- It is standard practice to clear any cache data after it has been altered.

For scalability purposes, we could even create distributed caches. A distributed cache has its data spread across several clusters around the world.
Under the hood, a distributed cache is:

- A distributed Hash table that has the responsibility of mapping Object Values to Keys.
- A hash table manages the addition, deletion, and failure of nodes as long as the cache is online.

To keep the cache coherent with the database, we may have to invalidate the cache. We can invalidate caches through:

- Cache Aside (Read through): when there is a client request for data, it will try the cache first. If the data is available within the cache, the data is returned to the client. If not, it will check the underlying database. In case the data exists in the database, it is returned to the application which will return it to the client and copies it to the cache (with read through, the cache provider will take care of copying values to the cache in case of a cache miss).
- Write through: data is first written to the cache and then to the database. The cache is coherent with the database and writes always go through the cache to the main database.
- Write around: the data is written to the database directly. Only the data that is read makes it way into the cache.
- Write back: the application writes data to the cache and acknowledges this to the application immediately. Only later, the cache writes the data back to the database.

A cache eviction algorithm is a way of deciding which element to remove when the cache is full. The following are some of the most common cache eviction policies:

- Least Recently Used (LRU)
- Least Frequently Used (LFU)
- First In First Out (FIFO)
- Last In First Out (LIFO)

