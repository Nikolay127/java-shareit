package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader(HEADER_USER_ID) Long ownerId) {
        return ResponseEntity.ok().body(itemService.getItemsByOwner(ownerId));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.getItemById(itemId));
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        return ResponseEntity.ok().body(itemService.searchItems(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @Validated({Marker.OnCreate.class}) @NotNull @RequestBody ItemDto itemDto) {
        ItemDto createdItem = itemService.createItem(itemDto, userId);
        return ResponseEntity.created(URI.create("/items/" + createdItem.getId())).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @Validated(Marker.OnUpdate.class) @NotNull @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(itemDto, itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.deleteItem(itemId, userId));
    }
}
