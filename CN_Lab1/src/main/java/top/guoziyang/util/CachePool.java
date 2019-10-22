package top.guoziyang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachePool {
    private static CachePool cachePool = new CachePool();

    public static CachePool getInstance() {
        return cachePool;
    }

    private int CACHE_SIZE = 100;

    private List<String> cacheUrl = new ArrayList<>();
    private Map<String, String> cacheTime = new HashMap<>();
    private Map<String, byte[]> cacheContent = new HashMap<>();

    public void addCache(String url, byte[] content, String time) {
        if(cacheUrl.size() == CACHE_SIZE) {
            String firstUrl = cacheUrl.remove(0);
            cacheContent.remove(firstUrl);
            cacheTime.remove(firstUrl);
            cacheUrl.add(url);
            cacheContent.put(url, content);
            cacheTime.put(url, time);
        } else {
            cacheUrl.add(url);
            cacheContent.put(url, content);
            cacheTime.put(url, time);
        }
    }

    public byte[] getContent(String url) {
        return cacheContent.get(url);
    }

    public String getTime(String url) {
        return cacheTime.get(url);
    }
}
