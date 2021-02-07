package com.icnmicros.app.item.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.icnmicros.app.commons.models.entity.Producto;
import com.icnmicros.app.item.models.Item;
import com.icnmicros.app.item.service.ItemsService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RefreshScope
@RestController
public class ItemController {

	private static Logger log = org.slf4j.LoggerFactory.getLogger(ItemController.class);

	@Autowired
	@Qualifier("serviceFeing")
	private ItemsService itemService;

	@Value("${configuracion.texto}")
	private String texto;

	@Autowired
	private Environment env;

	@GetMapping("/listar")
	public List<Item> listar() {
		return itemService.findAll();
	}

	@HystrixCommand(fallbackMethod = "alternativeMethod")
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
		return itemService.findById(id, cantidad);
	}

	public Item alternativeMethod(Long id, Integer cantidad) {

		Item itemAlternative = new Item();
		Producto productoAlternative = new Producto();

		itemAlternative.setCantidad(cantidad);
		productoAlternative.setId(id);
		productoAlternative.setNombre("Producto alternativo Hystrix");
		productoAlternative.setPrecio(500.99);

		itemAlternative.setProducto(productoAlternative);

		return itemAlternative;

	}

	@GetMapping("/obtener-config")
	public ResponseEntity<?> obtenerConfigucarion(@Value("${server.port}") String server) {
		Map<String, String> json = new HashMap<>();

		log.info(texto);

		if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("autor", env.getProperty("configuracion.autor.nombre"));
			json.put("email", env.getProperty("configuracion.autor.email"));
		}

		json.put("texto", texto);
		json.put("server", server);
		return new ResponseEntity<Map<String, String>>(json, HttpStatus.OK);
	}
	
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto crearProducto(@RequestBody Producto producto) {
		return itemService.save(producto);
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto editarProducto(@RequestBody Producto producto, @PathVariable Long id) {
		return itemService.update(producto, id);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProducto( @PathVariable Long id) {
		itemService.delete(id);
	}

}
