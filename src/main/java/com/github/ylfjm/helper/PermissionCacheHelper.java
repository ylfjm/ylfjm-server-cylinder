package com.github.ylfjm.helper;

import com.github.ylfjm.pojo.dto.PermissionCacheDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 描述：权限缓存类，只在单机部署下使用，集群部署请使用redis实现
 *
 * @author YLFJM
 * @Date 2020/10/24
 */
public class PermissionCacheHelper {

    private static final String P_CACHE = "p_cache_";
    private static final String R_CACHE = "r_cache_";

    private static Map<String, Set<PermissionCacheDTO>> pMap = new HashMap<>();
    private static Map<String, Set<PermissionCacheDTO>> rMap = new HashMap<>();


    public static Set<PermissionCacheDTO> getPList() {
        return pMap.get(P_CACHE);
    }

    public static Set<PermissionCacheDTO> setPList(Set<PermissionCacheDTO> permissions) {
        return pMap.put(P_CACHE, permissions);
    }

    public static Set<PermissionCacheDTO> getRList(Integer suffix) {
        return rMap.get(R_CACHE + suffix);
    }

    public static Set<PermissionCacheDTO> setRList(Integer suffix, Set<PermissionCacheDTO> permissions) {
        return rMap.put(R_CACHE + suffix, permissions);
    }

}
