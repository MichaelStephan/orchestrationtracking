package io.yaas.workflow;

import java.util.*;

public class Arguments implements Map<String, Object> {
	private static final String ERROR_FIELD_NAME = "io.yaas.error";

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

	public synchronized void addError(Throwable error) {
		Map<String, Object> tmp = new HashMap<>();
		tmp.putAll(_args);
		tmp.put(ERROR_FIELD_NAME, error);
		_args = Collections.unmodifiableMap(tmp);
	}

	public Throwable getError() {
		return (Throwable)get(ERROR_FIELD_NAME);
	}
}
