package top.guoziyang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓冲池类，存储从url到byte数组映射
 *
 * @author Ziyang Guo
 */
public class CachePool {
    private static CachePool cachePool = new CachePool();

    public static CachePool getInstance() {
        return cachePool;
    }

    private static final int CACHE_SIZE = 256;

    private List<String> cacheUrl = new ArrayList<>();
    private Map<String, byte[]> cacheContent = new HashMap<>();

    /**
     * 向缓冲池添加缓存
     *
     * @param url 内容所属的url
     * @param content 内容byte数组
     */
    public void addCache(String url, byte[] content) {
        if(cacheUrl.size() == CACHE_SIZE) {
            String firstUrl = cacheUrl.remove(0);
            cacheContent.remove(firstUrl);
            cacheUrl.add(url);
            cacheContent.put(url, content);
        } else {
            cacheUrl.add(url);
            cacheContent.put(url, content);
        }
    }

    /**
     * 根据url获取byte数组
     *
     * @param url 内容url
     * @return url对应的缓存内容
     */
    public byte[] getContent(String url) {
        return cacheContent.get(url);
    }

}
