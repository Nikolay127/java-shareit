package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {

    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    ItemDto toItemDto(Item item);

    @Mappings({
            @Mapping(target = "id", source = "itemDto.id"),
            @Mapping(target = "name", source = "itemDto.name"),
            @Mapping(target = "description", source = "itemDto.description"),
            @Mapping(target = "available", source = "itemDto.available"),
            @Mapping(target = "owner", source = "owner"),
            @Mapping(target = "request", source = "request")
    })
    Item fromItemDto(ItemDto itemDto, User owner, ItemRequest request);

}