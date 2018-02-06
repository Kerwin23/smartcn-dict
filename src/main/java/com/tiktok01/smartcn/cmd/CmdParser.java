package com.tiktok01.smartcn.cmd;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class CmdParser {

	public final static Command parse(String[] args) {
		Command command = new Command();
		if(args == null) {
			return command;
		}
		Map<String, Field> fieldsMap = mapFields();
		for(String arg : args) {
			if(StringUtils.isBlank(arg) || !arg.startsWith("-") || arg.indexOf("=") == -1) {
				continue;
			}
			try {
				String[] kvPair = arg.substring(1).split("=");
				Field field = fieldsMap.get(kvPair[0]);
				field.setAccessible(true);
				Object value = getFieldValue(field.getType(), kvPair[1]);
				field.set(command, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return command;
	}
	
	private final static Object getFieldValue(Class<?> fieldType, String vString) {
		if(fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
			if(StringUtils.isNumeric(vString)) {
				try {
					return Integer.parseInt(vString);
				} catch (Exception e) {
				}
			}
		} else if(fieldType.equals(String.class)) {
			return vString;
		}
		return null;
	}
	
	private final static Map<String, Field> mapFields() {
		Map<String, Field> fieldsMap = new HashMap<String, Field>();
		try {
			Field[] fields = Command.class.getDeclaredFields();
			for(Field field : fields) {
				Cmd cmd = field.getAnnotation(Cmd.class);
				if(cmd == null) {
					continue;
				}
				fieldsMap.put(cmd.name(), field);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldsMap;
	}
}
