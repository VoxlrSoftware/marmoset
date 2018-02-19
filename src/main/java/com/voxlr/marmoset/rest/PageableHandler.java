package com.voxlr.marmoset.rest;

import static com.voxlr.marmoset.util.StreamUtils.asStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class PageableHandler {
    
    private final Map<String, List<String>> sortMap;
    
    public PageableHandler() {
	this.sortMap = new HashMap<>();
    }
    
    public PageableHandler(Map<String, List<String>> sortMap) {
	this.sortMap = sortMap;
    }
    
    public Pageable handleSort(Pageable pageable) {
	Sort sort = pageable.getSort();
	
	if (sort == null) {
	    return pageable;
	}
	
	List<Order> finalOrders = new ArrayList<Order>();
	asStream(sort.iterator()).forEach(x -> {
	    if (sortMap.containsKey(x.getProperty())) {
		sortMap.get(x.getProperty()).stream().forEach(field -> {
		    finalOrders.add(new Order(x.getDirection(), field));
		});
	    } else {
		finalOrders.add(x);
	    }
	});
	
	return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(finalOrders));
    }
}
