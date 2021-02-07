package com.icnmicros.app.item.service;

import java.util.List;

import com.icnmicros.app.commons.models.entity.Producto;
import com.icnmicros.app.item.models.Item;

public interface ItemsService {

	public List<Item> findAll();

	public Item findById(Long id, Integer cantidad);

	public Producto save(Producto producto);

	public Producto update(Producto producto, Long id);

	public void delete(Long id);

}
