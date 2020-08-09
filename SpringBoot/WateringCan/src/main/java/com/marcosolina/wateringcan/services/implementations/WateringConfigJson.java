package com.marcosolina.wateringcan.services.implementations;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosolina.wateringcan.devices.Pump;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;

public class WateringConfigJson implements WateringConfigService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WateringConfigJson.class);

	private Map<String, Pump> cache = new HashMap<>();

	private String fileName = "config.json";

	@Override
	public boolean storePumpsConfig(Set<Pump> pumps) throws WateringException {
		try {
			LOGGER.debug("Storing the config into a json file");
			ObjectMapper mapper = new ObjectMapper();
			String folder = jarFolder();
			JsonConfig jc = new JsonConfig();
			jc.setPumps(pumps);
			mapper.writeValue(Paths.get(folder, fileName).toFile(), jc);

			/*
			 * Update the cache
			 */
			cache = new HashMap<>();
			pumps.stream().forEach(p -> cache.put(p.getMac() + "_" + p.getId(), p));
			return true;
		} catch (IOException e) {
			throw new WateringException(e);
		}
	}

	@Override
	public Set<Pump> loadPumpsConfig() throws WateringException {
		LOGGER.debug("Loading the config from the json file");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String folder = jarFolder();
			File json = Paths.get(folder, fileName).toFile();
			if (!json.exists()) {
				LOGGER.debug("Json file not founded");
				return new HashSet<>();
			}

			Set<Pump> pumps = mapper.readValue(json, JsonConfig.class).getPumps();

			/*
			 * Update the cache
			 */
			cache = new HashMap<>();
			pumps.stream().forEach(p -> cache.put(p.getMac() + "_" + p.getId(), p));
			return pumps;
		} catch (IOException e) {
			throw new WateringException(e);
		}
	}

	private String jarFolder() throws WateringException {
		String jarFolder;
		try {
			jarFolder = new File(WateringConfigJson.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
		} catch (URISyntaxException e) {
			throw new WateringException(e);
		}
		return jarFolder;
	}

	static class JsonConfig {
		
		public JsonConfig() {}
		
		private Set<Pump> pumps;

		public Set<Pump> getPumps() {
			return pumps;
		}

		public void setPumps(Set<Pump> pumps) {
			this.pumps = pumps;
		}

	}

	@Override
	public String getPupmDescription(String mac, String id) {
		Pump p = cache.get(mac + "_" + id);
		if (p != null) {
			return p.getDescription();
		}
		return null;
	}

}
