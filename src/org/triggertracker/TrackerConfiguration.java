package org.triggertracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import android.os.Environment;

public class TrackerConfiguration {

	private String phoneName;
	private String phoneNumber;
	private Map<String, Tuple> stationMap;

	public TrackerConfiguration() {
	};

	public TrackerConfiguration(String phoneName, String phoneNumber) {
		this.phoneName = phoneName;
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Map<String, Tuple> getStationMap() {
		return stationMap;
	}

	public void setStationMap(Map<String, Tuple> stationMap) {
		this.stationMap = stationMap;
	}

	private Object construct(String data) {
		Yaml yaml = new Yaml();
		return yaml.load(data);
	}

	public void loadFromYaml(String filepath) {

		/*
		 * try {
		 * 
		 * File configFile = new File(Environment.getExternalStorageDirectory()
		 * + filepath);
		 * 
		 * InputStream configInput = new FileInputStream(configFile);
		 * 
		 * Yaml yaml = new Yaml();
		 * 
		 * yaml.load(configInput);
		 * 
		 * } catch (FileNotFoundException e) {
		 * 
		 * e.printStackTrace();
		 * 
		 * }
		 */

		String data = "--- !org.yaml.snakeyaml.constructor.TrackerConfiguration\nphoneName: Luke\nphoneNumber: 0439727186\n{Station1: [-27.511816, 153.035267]}";
		Object obj = construct(data);

		TrackerConfiguration config = (TrackerConfiguration) obj;

		this.setPhoneName(config.getPhoneName());
		this.setPhoneNumber(config.getPhoneNumber());
		this.setStationMap(config.getStationMap());

	}

}
