package com.lcdw.electronic.store.Helper;

import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper {



    public static  <V,U> PageableResponse<V> getPageableResponse(Page<U> page,Class<V> type)
    {
        ModelMapper mapper=new ModelMapper();
        List<U> entity = page.getContent();
        List<V> dtoList=entity.stream().map(object->mapper.map(object,type)).collect(Collectors.toList());
        //List<V> dtoList = entity.stream().map(object -> new ModelMapper().map(object,type)).collect(Collectors.toList());

        PageableResponse<V> response=new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }
}
