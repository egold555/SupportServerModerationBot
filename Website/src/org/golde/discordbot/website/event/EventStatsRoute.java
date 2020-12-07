package org.golde.discordbot.website.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.RichPresence.Image;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;

public class EventStatsRoute extends EventBase {

	public EventStatsRoute(ESSBot bot) {
		super(bot);
	}

	private static HashMap<String, HashSet<Long>> activitiesToMembers = new HashMap<String, HashSet<Long>>();
	private static HashMap<String, String> activitiesToData = new HashMap<String, String>();

	@Override
	public void onGuildReady(GuildReadyEvent event) {

		Guild g = event.getGuild();
		for(Member m : g.getMembers()) {
			for(Activity act : m.getActivities()) {
				updateActivity(m, act);
			}

		}

	}

	@Override
	public void onUserActivityStart(UserActivityStartEvent event) {
		updateActivity(event.getMember(), event.getNewActivity());
	}

	private void updateActivity(Member m, Activity act) {

		if(act.isRich()) {
			RichPresence rp = act.asRichPresence();
			String key = rp.getName();



			if(!activitiesToMembers.containsKey(key)) {
				activitiesToMembers.put(key, new HashSet<Long>());
			}

			if(!activitiesToData.containsKey(key)) {
				Image imgData = rp.getLargeImage();



				if(imgData == null) {
					imgData = rp.getSmallImage();
				}
				String img = null;
				if(imgData != null) {
					img = imgData.getUrl();
				}

//				if(key.equals("Spotify")) {
//					img = "internal://spotify/";
//				}
//				
//				if(key.equals("Minecraft")) {
//					img = "https://cdn.discordapp.com/app-icons/356875570916753438/166fbad351ecdd02d11a3b464748f66b.webp";
//				}

				activitiesToData.put(key, img);
			}

			activitiesToMembers.get(key).add(m.getIdLong());
		}


		for(String key : activitiesToMembers.keySet()) {
			System.out.println("\"" + key + "\" " + activitiesToMembers.get(key).size());
		}
		System.out.println();
	}

	public static HashMap<String, String> getActivitiesToData() {
		return (HashMap<String, String>) activitiesToData.clone();
	}

	public static Map<String, Integer> getActivitiesToMembers() {
		HashMap<String, Integer> toReturn = new HashMap<String, Integer>();
		for(String key : activitiesToMembers.keySet()) {
			toReturn.put(key, activitiesToMembers.get(key).size());
		}

		Map<String, Integer> res = toReturn.entrySet().stream()
				.sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
				.limit(10)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));




		return sortByValue(res);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());
		Collections.reverse(list);

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

}
