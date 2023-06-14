package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.Stats;

public class StatsMapper {

    public static Stats toStats(EndpointHitDto endpointHit) {
        Stats stats = new Stats();
        stats.setApp(endpointHit.getApp());
        stats.setUri(endpointHit.getUri());
        stats.setIp(endpointHit.getIp());
        stats.setTimestamp(endpointHit.getTimestamp());
        return stats;
    }
}
