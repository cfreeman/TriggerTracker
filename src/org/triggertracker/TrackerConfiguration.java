package org.triggertracker;

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
	private String gpsRange;

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

	public String getGPSRange() {
		return gpsRange;
	}

	public void setGPSRange(String gpsRange) {
		this.gpsRange = gpsRange;
	}

	private Object construct(InputStream data) {
		Yaml yaml = new Yaml();
		return yaml.load(data);
	}

	public void loadFromYaml(String filepath) {

		try {

			File configFile = new File(
					Environment.getExternalStorageDirectory() + filepath);

			InputStream configInput = new FileInputStream(configFile);

			Map<String, Object> object = (Map<String, Object>) construct(configInput);

			this.setPhoneName(object.get("phoneName").toString());
			this.setPhoneName(object.get("phoneNumber").toString());
			this.setGPSRange(object.get("gpsRange").toString());

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

	}

}
