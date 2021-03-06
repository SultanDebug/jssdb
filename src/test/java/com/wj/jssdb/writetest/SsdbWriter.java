/*
* 2014-11-28 上午10:02:57
* 吴健 HQ01U8435
*/

package com.wj.jssdb.writetest;

import java.util.ArrayList;
import java.util.List;

import com.wj.jssdb.pool.JssdbClient;

public class SsdbWriter implements Runnable {
	public static Integer counter = 0;
	public static List<TimeLog> log = new ArrayList<TimeLog>();
	private JssdbClient jssdbClient = null;
	
	private List<String> keys = null;
	private List<String> values = null;
	
	public SsdbWriter(JssdbClient jssdbClient, List<String> keys,
			List<String> values) {
		super();
		this.jssdbClient = jssdbClient;
		this.keys = keys;
		this.values = values;
	}

	public JssdbClient getJssdbClient() {
		return jssdbClient;
	}

	public void setJssdbClient(JssdbClient jssdbClient) {
		this.jssdbClient = jssdbClient;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	private void finish(long size, long cost) {
		synchronized (counter) {
			counter ++;
			log.add(new TimeLog("子线程[#" + counter + "]", size, cost));
		}
	}
	
	public void run() {
		if(jssdbClient != null && keys != null && values != null) {
			System.out.println("thread start...");
			long b = System.currentTimeMillis();
			try {
				jssdbClient.mSet(keys, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long delt = System.currentTimeMillis() - b;
			finish(keys.size(), delt);
		}
	}

}
