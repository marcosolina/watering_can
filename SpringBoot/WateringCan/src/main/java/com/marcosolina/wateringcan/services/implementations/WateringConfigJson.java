package com.marcosolina.wateringcan.services.implementations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosolina.wateringcan.WateringCanApplication;
import com.marcosolina.wateringcan.devices.FlowerPot;
import com.marcosolina.wateringcan.errors.WateringException;
import com.marcosolina.wateringcan.services.interfaces.WateringConfigService;

public class WateringConfigJson implements WateringConfigService {
	private static final Logger LOGGER = LoggerFactory.getLogger(WateringConfigJson.class);

	private Map<String, FlowerPot> cache = new HashMap<>();

	private String fileName = "config.json";

	@Override
	public boolean storePotsConfig(Set<FlowerPot> pots) throws WateringException {
		try {
			LOGGER.debug("Storing the config into a json file");
			ObjectMapper mapper = new ObjectMapper();
			String folder = jarFolder();
			JsonConfig jc = new JsonConfig();
			jc.setPots(pots);
			mapper.writeValue(Paths.get(folder, fileName).toFile(), jc);

			/*
			 * Update the cache
			 */
			cache = new HashMap<>();
			pots.stream().forEach(p -> cache.put(p.getMac() + "_" + p.getId(), p));
			return true;
		} catch (IOException e) {
			throw new WateringException(e);
		}
	}

	@Override
	public Set<FlowerPot> loadPotsConfig() throws WateringException {
		LOGGER.debug("Loading the config from the json file");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String folder = jarFolder();
			File json = Paths.get(folder, fileName).toFile();
			if (!json.exists()) {
				LOGGER.debug("Json file not founded");
				return new HashSet<>();
			}

			Set<FlowerPot> pots = mapper.readValue(json, JsonConfig.class).getPots();

			/*
			 * Update the cache
			 */
			cache = new HashMap<>();
			pots.stream().forEach(p -> cache.put(p.getMac() + "_" + p.getId(), p));
			return pots;
		} catch (IOException e) {
			throw new WateringException(e);
		}
	}

	private String jarFolder(){
		ApplicationHome home = new ApplicationHome(WateringCanApplication.class);
		home.getDir();
		String jarFolder = home.getDir().getAbsolutePath();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Jar file path: %s", jarFolder));
		}
		return jarFolder;
	}

	static class JsonConfig {

		private Set<FlowerPot> pots;

		public Set<FlowerPot> getPots() {
			return pots;
		}

		public void setPots(Set<FlowerPot> pots) {
			this.pots = pots;
		}

	}

	@Override
	public String getPotDescription(String mac, String id) {
		FlowerPot p = cache.get(mac + "_" + id);
		if (p != null) {
			return p.getDescription();
		}
		return null;
	}

}
