package org.golde.discordbot.supportserver.crash;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrashReport {

	String wittyComment;
	String time;
	String description;
	Stacktrace exception;
	String[] head;
	String[] initialization;
	
	@Getter
	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class Stacktrace {
		String type;
		String desc;
		String[] body;
		@Override
		public String toString() {
			return "Stacktrace [type=" + type + ", desc=" + desc + ", body=" + Arrays.toString(body) + "]";
		}
		
	}

	
	
}
