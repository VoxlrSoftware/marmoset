package com.voxlr.marmoset.util;

import static com.voxlr.marmoset.util.StreamUtils.asStream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.voxlr.marmoset.model.Pagination;
import com.voxlr.marmoset.model.SortField;
import com.voxlr.marmoset.model.persistence.dto.PageDTO;

@Component
public class MapperUtils {

    @Autowired
    private ModelMapper modelMapper;
    
    public <T1 extends List<?>, T2> List<T2> mapList(T1 list, Class<T2> clazz) {
	return list.stream().map(x -> modelMapper.map(x, clazz)).collect(Collectors.toList());
    }
    
    public <T> PageDTO<T> mapPage(Page<?> page, List<T> content) {
	Pagination pagination = Pagination.builder()
		.page(page.getNumber())
		.pageSize(page.getSize()).build();
	
	if (page.getSort() != null) {
	    List<SortField> sortFields = new ArrayList<SortField>();
	    Sort sort = page.getSort();
	    asStream(sort.iterator()).forEach(x -> {
		sortFields.add(new SortField(x.getProperty(), x.getDirection().name()));
	    });
	    pagination.setSortFields(sortFields);
	}
	
	return PageDTO.<T>builder().results(content)
		.totalCount(page.getTotalElements())
		.totalPages(page.getTotalPages())
		.pagination(pagination).build();
    }
    
    public <T> PageDTO<T> mapPage(Page<T> page) {
	return mapPage(page, page.getContent());
    }
    
    public <T1, T2> PageDTO<T2> mapPage(Page<T1> page, Class<T2> clazz) {
	List<T2> content = page.getContent().stream().map(x -> modelMapper.map(x, clazz)).collect(Collectors.toList());
	return mapPage(page, content);
    }
}
