package org.golde.discordbot.supportserver.crash;

import java.util.Arrays;
import java.util.Date;

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
	SystemDetails systemDetails;
	
	@Override
	public String toString() {
		return "CrashReport [wittyComment=" + wittyComment + ", time=" + time + ", description=" + description
				+ ", exception=" + exception + ", head=" + Arrays.toString(head) + ", initialization="
				+ Arrays.toString(initialization) + ", systemDetails=" + systemDetails + "]";
	}
	
	@Getter
	@Builder
	public static class SystemDetails {
		String minecraftVersion;
		String operatingSystem;
		String CPU;
		String javaVersion;
		String javaVMVersion;
		String memory;
		String[] jVMFlags;
		IntCache intCache;
		String launchedVersion;
		String LWJGL;
		String OpenGL;
		String[] GLCaps;
		boolean usingVBOs;
		EnumModded isModded;
		String type;
		String[] resourcePacks;
		String currentLanguage;
		String profilerPosition;
		
		public enum EnumModded {
			YES("Definitely"), LIKELY("Very likely"), NO("Probably not");
			
			final String word;
			EnumModded(String word){
				this.word = word;
			}
			
			public static EnumModded get(String word) {
				for(EnumModded f : values()) {
					if(f.word.equals(word)) {
						return f;
					}
				}
				return null;
			}
		}

		@Override
		public String toString() {
			return "SystemDetails [minecraftVersion=" + minecraftVersion + ", operatingSystem=" + operatingSystem
					+ ", CPU=" + CPU + ", javaVersion=" + javaVersion + ", javaVMVersion=" + javaVMVersion + ", memory="
					+ memory + ", jVMFlags=" + Arrays.toString(jVMFlags) + ", intCache=" + intCache
					+ ", launchedVersion=" + launchedVersion + ", LWJGL=" + LWJGL + ", OpenGL=" + OpenGL + ", GLCaps="
					+ Arrays.toString(GLCaps) + ", usingVBOs=" + usingVBOs + ", isModded=" + isModded + ", type=" + type
					+ ", resourcePacks=" + Arrays.toString(resourcePacks) + ", currentLanguage=" + currentLanguage
					+ ", profilerPosition=" + profilerPosition + "]";
		}
		
	}
	
	@Getter
	@AllArgsConstructor
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class IntCache {
		int cache;
		int tcache;
		int allocated;
		int tallocated;
		@Override
		public String toString() {
			return "IntCache [cache=" + cache + ", tcache=" + tcache + ", allocated=" + allocated + ", tallocated="
					+ tallocated + "]";
		}
		
	}
	
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
