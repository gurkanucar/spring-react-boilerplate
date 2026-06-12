package com.gucardev.springreactboilerplate.config.cache;

/**
 * Logical cache names — <strong>names only, no TTL</strong>.
 *
 * <p>A cache name is just a bucket of related entries. The TTL and backing store are
 * decided by which cache manager you pass (see {@link CacheManagers}). The same name can
 * be used under different managers to get different TTLs/stores.
 *
 * <pre>{@code
 * @Cacheable(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M)
 * public User findById(Long id) { ... }
 *
 * @CacheEvict(cacheNames = CacheNames.USERS, cacheManager = CacheManagers.REDIS_10M, allEntries = true)
 * public void onUsersChanged() { ... }
 * }</pre>
 *
 * <p>Caches are created on demand, so adding a new one is just declaring a constant here.
 */
public final class CacheNames {

    private CacheNames() {
    }

    public static final String USERS = "users";
    public static final String ROLES = "roles";
    public static final String SETTINGS = "settings";
}
