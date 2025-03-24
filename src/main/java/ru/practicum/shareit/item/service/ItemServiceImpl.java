package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemRepository;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.getByOwner(ownerId).stream()
                             .map(itemDtoMapper::toItemDto)
                             .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));

        return itemDtoMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String query) {
        if (query.isEmpty() || query.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(query).stream()
                             .map(itemDtoMapper::toItemDto)
                             .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userDtoMapper.fromUserDto(userService.getUserById(ownerId));
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new BadRequestException("Name can not be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new BadRequestException("Description can not be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Available can not be empty");
        }
//        Item item = ItemMapper.fromItemDto(itemDto, owner, null);
        Item item = itemDtoMapper.fromItemDto(itemDto, owner, null);

        return itemDtoMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can edit the Item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemDtoMapper.toItemDto(itemRepository.update(itemId, item));
    }

    @Override
    public ItemDto deleteItem(Long itemId, Long userId) {
        ItemDto itemDto = getItemById(itemId);
        itemRepository.delete(itemId);

        return itemDto;
    }

}
