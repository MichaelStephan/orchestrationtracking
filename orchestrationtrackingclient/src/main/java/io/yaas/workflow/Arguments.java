package io.yaas.workflow;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Arguments implements Map<String, Object> {
	
	private Map<String, Object> _args;
	
	public static Arguments EMPTY_ARGUMENTS = new Arguments(Collections.emptyMap());
	
	public Arguments(Map<String, Object> m) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.putAll(m);
		_args = Collections.unmodifiableMap(args);
	}

	@Override
	public void clear() {
		_args.clear();
	}
	
	@Override
	public boolean containsKey(Object k) {
		return _args.containsKey(k);
	}

	@Override
	public boolean containsValue(Object v) {
		return _args.containsValue(v);
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return _args.entrySet();
	}

	@Override
	public Object get(Object k) {
		return _args.get(k);
	}

	@Override
	public boolean isEmpty() {
		return _args.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return _args.keySet();
	}

	@Override
	public Object put(String k, Object v) {
		return _args.put(k, v);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		_args.putAll(m);
	}

	@Override
	public Object remove(Object k) {
		return _args.remove(k);
	}

	@Override
	public int size() {
		return _args.size();
	}

	@Override
	public Collection<Object> values() {
		return _args.values();
	}

}
