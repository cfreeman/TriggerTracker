/*
 * Copyright (c) Luke Atherton & Clinton Freeman 2012
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
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
			File configFile = new File(Environment.getExternalStorageDirectory() + filepath);

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
*/
