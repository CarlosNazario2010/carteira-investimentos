package com.carlosnazario.carteira_de_acoes.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationScheduler {

    @Autowired
    private CacheManager cacheManager;

    /**
     * Invalida o cache em intervalos fixos.
     * Este método é agendado para ser executado a cada 20 minutos (1200000 milissegundos). Ele limpa todos os caches gerenciados pelo "CacheManager".
     */
    @Scheduled(fixedRate = 1200000) // Invalida o cache a cada 20 minutos (1200000 milissegundos)
    public void invalidateCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
    }
}
