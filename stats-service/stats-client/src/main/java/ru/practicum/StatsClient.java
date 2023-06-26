package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String url, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void saveStats(HttpServletRequest request, String app) {
        final EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(app);
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());

        post("/hit", endpointHitDto);
        log.debug("Статистика сохранена");
    }

    public List<ViewStatsDto> getStatsCount(List<String> uris, LocalDateTime start, LocalDateTime end) {
        String joinUrl = String.join(",", uris);
        final Map<String, Object> parameters = Map.of(
                "start", encode(start),
                "end", encode(end),
                "uris", joinUrl,
                "unique", "true"
        );
        log.debug("Получен ответ сервиса статистики");
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    private String encode(LocalDateTime date) {
        return date.format(FORMAT);
    }
}
