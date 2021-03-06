/*
* 2014-11-20 下午2:24:04
* 吴健 HQ01U8435
*/

package com.wj.jssdb.pool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wj.jssdb.core.Response;
import com.wj.jssdb.core.SSDB;
import com.wj.jssdb.core.SSDBCoderUtil;

public class Jssdb {
	private SSDB ssdb = null;
	public Jssdb(String host, int port) {
		try {
			ssdb = new SSDB(host, port);
		} catch (Exception e) {
			System.out.println("initial jssdb error : " + e.getMessage());
		}
	}
	public Jssdb(String host, int port, int timeOutMs) {
		try {
			ssdb = new SSDB(host, port, timeOutMs);
		} catch (Exception e) {
			System.out.println("initial jssdb error : " + e.getMessage());
		}
	}
	public boolean testConnected() {
		try {
			ssdb.get("test");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean isOpen() {
		return isConnected() && !ssdb.isClosed();
	}
	public boolean isConnected() {
		try {
			return ssdb.isConnected();
		} catch (Exception e) {
			return false;
		}
	}
	public void close() {
		if(ssdb != null && !ssdb.isClosed()) {
			ssdb.close();
		}
	}
	public boolean set(String key, String value) {
		try {
			ssdb.set(key, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean setExp(String key, String value, int seconds) {
		try {
			ssdb.setExp(key, value, seconds);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public String get(String key) {
		try {
			return new String(ssdb.get(key));
		} catch (Exception e) {
			return null;
		}
	}
	public <T extends Serializable> boolean setPojo(String key, T value) {
		try {
			ssdb.set(key, SSDBCoderUtil.encode(value));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public <T extends Serializable> boolean setPojoExp(String key, T value, int seconds) {
		try {
			ssdb.setExp(key, SSDBCoderUtil.encode(value), seconds);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public <T extends Serializable> T getPojo(String key) {
		try {
			return SSDBCoderUtil.decode(ssdb.get(key));
		} catch (Exception e) {
			return null;
		}
	}
	public <T extends Serializable> void mSet(List<String> keys, List<String> values) throws Exception {
		try {
			if(keys.size() != values.size()) {
				throw new JssdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for(int i = 0; i < length ; i ++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = values.get(i).getBytes();
			}
			ssdb.multi_set(kvs);
		} catch (Exception e) {
			throw e;
		}
	}
	public <T extends Serializable> void mSetPojo(List<String> keys, List<T> values) throws Exception {
		try {
			if(keys.size() != values.size()) {
				throw new JssdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for(int i = 0; i < length ; i ++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = SSDBCoderUtil.encode(values.get(i));
			}
			ssdb.multi_set(kvs);
		} catch (Exception e) {
			throw e;
		}
	}
	public <T extends Serializable> List<T> mGetPojo(List<String> keys) {
		List<T> rs = new ArrayList<T>();
		try {
			Response response = ssdb.multi_get(keys.toArray(new String[]{}));
			for(byte[] value : response.items.values()) {
				T obj = SSDBCoderUtil.decode(value);
				rs.add(obj);
			}
			return rs;
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, String> mGet(List<String> keys) {
		return mGet(keys.toArray(new String[]{}));
	}

	public Map<String, String> mGet(String[] keys) {
		Map<String, String> rs = new HashMap<String, String>();
		try {
			Response response = ssdb.multi_get(keys);
			for(Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.put(new String(en.getKey()), new String(en.getValue()));
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<KeyValueBean> mGetAsList(String[] keys) {
		List<KeyValueBean> rs = new ArrayList<KeyValueBean>();
		try {
			Response response = ssdb.multi_get(keys);
			for(Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.add(new KeyValueBean(new String(en.getKey()), new String(en.getValue())));
			}
			return rs;
		} catch (Exception e) {
			return rs;
		}
	}
	
	public void setNx(String key, String value) {
		try {
			ssdb.setnx(key, value);
		} catch (Exception e) {
		}
	}
	public <T extends Serializable> void setPojoNx(String key, T value) {
		try {
			ssdb.setnx(key, SSDBCoderUtil.encode(value));
		} catch (Exception e) {
		}
	}
//	public boolean exists(String key) {
//		try {
//			return ssdb.exists(key);
//		} catch (Exception e) {
//			return false;
//		}
//	}
	public long increase(String key, long delt) {
		try {
			return ssdb.incr(key, delt);
		} catch (Exception e) {
			return -12345678900l;
		}
	}
	
	public void delete(String key) throws Exception {
		ssdb.del(key);
	}
	
	public void multiHSet(String mapName, List<String> keys, List<String> values) {
		try {
			if(keys.size() != values.size()) {
				throw new JssdbException("the size of keys and values is not equal..");
			}
			int length = keys.size();
			byte[][] kvs = new byte[length * 2][];
			for(int i = 0; i < length ; i ++) {
				kvs[i * 2] = keys.get(i).getBytes();
				kvs[i * 2 + 1] = values.get(i).getBytes();
			}
			ssdb.multi_hset(mapName, kvs);
		} catch (Exception e) {
			
		}
	}
	public Map<String, String> hGetAll(String mapName) {
		Map<String, String> rs = new HashMap<String, String>();
		try {
			Response response = ssdb.hgetall(mapName);
			for(Map.Entry<byte[], byte[]> en : response.items.entrySet()) {
				rs.put(new String(en.getKey()), new String(en.getValue()));
			}
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void hSet(String mapperName, String key, String value) {
		try {
			ssdb.hset(mapperName, key, value);
		} catch (Exception e) {
			
		}
	}
	public String hGet(String mapperName, String key) {
		try {
			byte[] temp = ssdb.hget(mapperName, key);
			if(temp == null) {
				return null;
			}
			return new String(temp);
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends Serializable> void hSetPojo(String mapperName, String key, T value) {
		try {
			ssdb.hset(mapperName, key, SSDBCoderUtil.encode(value));
		} catch (Exception e) {
			
		}
	}
	public <T extends Serializable> T hGetPojo(String mapperName, String key) {
		try {
			byte[] temp = ssdb.hget(mapperName, key);
			if(temp == null) {
				return null;
			}
			return SSDBCoderUtil.decode(temp);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void hDelete(String mapperName, String keyName) {
		try {
			ssdb.hdel(mapperName, keyName);
		} catch (Exception e) {
			
		}
	}
	public void hClear(String mapperName) throws Exception {
		ssdb.hclear(mapperName);
	}
}
