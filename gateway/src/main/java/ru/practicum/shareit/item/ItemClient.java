package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.share.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> getItemsByOwner(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getItem(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> patchItem(long userId, long itemId, UpdateItemDto updateItemDto) {
        return patch("/" + itemId, userId, null, updateItemDto);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CreateCommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}