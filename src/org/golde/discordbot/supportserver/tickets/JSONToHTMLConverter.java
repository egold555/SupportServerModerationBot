package org.golde.discordbot.supportserver.tickets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.golde.discordbot.supportserver.Main;

public class JSONToHTMLConverter {

	private static final String HEADER = "<!DOCTYPE html><html lang=en><title>Eric's Support Server - %channel_name%</title><meta charset=utf-8><meta content=\"width=device-width\"name=viewport><style>@font-face{font-family:Whitney;src:url(https://discordapp.com/assets/6c6374bad0b0b6d204d8d6dc4a18d820.woff);font-weight:300}@font-face{font-family:Whitney;src:url(https://discordapp.com/assets/e8acd7d9bf6207f99350ca9f9e23b168.woff);font-weight:400}@font-face{font-family:Whitney;src:url(https://discordapp.com/assets/3bdef1251a424500c1b3a78dea9b7e57.woff);font-weight:500}@font-face{font-family:Whitney;src:url(https://discordapp.com/assets/be0060dafb7a0e31d2a1ca17c0708636.woff);font-weight:600}@font-face{font-family:Whitney;src:url(https://discordapp.com/assets/8e12fb4f14d9c4592eb8ec9f22337b04.woff);font-weight:700}body{font-family:Whitney,\"Helvetica Neue\",Helvetica,Arial,sans-serif;font-size:17px}a{text-decoration:none}a:hover{text-decoration:underline}img{object-fit:contain}.markdown{max-width:100%;white-space:pre-wrap;line-height:1.3;overflow-wrap:break-word}.spoiler{width:fit-content}.spoiler--hidden{cursor:pointer}.spoiler-text{border-radius:3px}.spoiler--hidden .spoiler-text{color:transparent}.spoiler--hidden .spoiler-text::selection{color:transparent}.spoiler-image{position:relative;overflow:hidden;border-radius:3px}.spoiler--hidden .spoiler-image{box-shadow:0 0 1px 1px rgba(0,0,0,.1)}.spoiler--hidden .spoiler-image img{filter:blur(44px)}.spoiler--hidden .spoiler-image:after{content:\"SPOILER\";color:#dcddde;background-color:rgba(0,0,0,.6);position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);font-weight:600;padding:.5em .7em;border-radius:20px;letter-spacing:.05em;font-size:.9em}.spoiler--hidden:hover .spoiler-image:after{color:#fff;background-color:rgba(0,0,0,.9)}.quote{margin:.1em 0;padding-left:.6em;border-left:4px solid;border-radius:3px}.pre{font-family:Consolas,\"Courier New\",Courier,monospace}.pre--multiline{margin-top:.25em;padding:.5em;border:2px solid;border-radius:5px}.pre--inline{padding:2px;border-radius:3px;font-size:.85em}.mention{border-radius:3px;padding:0 2px;color:#7289da;background:rgba(114,137,218,.1);font-weight:500}.emoji{width:1.25em;height:1.25em;margin:0 .06em;vertical-align:-.4em}.emoji--small{width:1em;height:1em}.emoji--large{width:2.8em;height:2.8em}.preamble{display:grid;margin:0 .3em .6em .3em;max-width:100%;grid-template-columns:auto 1fr}.preamble_guild-icon-container{grid-column:1}.preamble_guild-icon{max-width:88px;max-height:88px}.preamble_entries-container{grid-column:2;margin-left:.6em}.preamble_entry{font-size:1.4em}.preamble_entry--small{font-size:1em}.chatlog{max-width:100%}.message-group{display:grid;margin:0 .6em;padding:.9em 0;border-top:1px solid;grid-template-columns:auto 1fr}.author-avatar-container{grid-column:1;width:40px;height:40px}.author-avatar{border-radius:50%;height:40px;width:40px}.messages{grid-column:2;margin-left:1.2em;min-width:50%}.author-name{font-weight:500}.timestamp{margin-left:.3em;font-size:.75em}.message{padding:.1em .3em;margin:0 -.3em;background-color:transparent;transition:background-color 1s ease}.content{font-size:.95em;word-wrap:break-word}.edited-timestamp{margin-left:.15em;font-size:.8em}.attachment{margin-top:.3em}.attachment-thumbnail{vertical-align:top;max-width:45vw;max-height:500px;border-radius:3px}.embed{display:flex;margin-top:.3em;max-width:520px}.embed-color-pill{flex-shrink:0;width:.25em;border-top-left-radius:3px;border-bottom-left-radius:3px}.embed-content-container{display:flex;flex-direction:column;padding:.5em .6em;border:1px solid;border-top-right-radius:3px;border-bottom-right-radius:3px}.embed-content{display:flex;width:100%}.embed-text{flex:1}.embed-author{display:flex;margin-bottom:.3em;align-items:center}.embed-author-icon{margin-right:.5em;width:20px;height:20px;border-radius:50%}.embed-author-name{font-size:.875em;font-weight:600}.embed-title{margin-bottom:.2em;font-size:.875em;font-weight:600}.embed-description{font-weight:500;font-size:.85em}.embed-fields{display:flex;flex-wrap:wrap}.embed-field{flex:0;min-width:100%;max-width:506px;padding-top:.6em;font-size:.875em}.embed-field--inline{flex:1;flex-basis:auto;min-width:150px}.embed-field-name{margin-bottom:.2em;font-weight:600}.embed-field-value{font-weight:500}.embed-thumbnail{flex:0;margin-left:1.2em;max-width:80px;max-height:80px;border-radius:3px}.embed-image-container{margin-top:.6em}.embed-image{max-width:500px;max-height:400px;border-radius:3px}.embed-footer{margin-top:.6em}.embed-footer-icon{margin-right:.2em;width:20px;height:20px;border-radius:50%;vertical-align:middle}.embed-footer-text{font-size:.75em;font-weight:500}.reactions{display:flex}.reaction{display:flex;align-items:center;margin:.35em .1em .1em .1em;padding:.2em .35em;border-radius:3px}.reaction-count{min-width:9px;margin-left:.35em;font-size:.875em}.bot-tag{position:relative;top:-.2em;margin-left:.3em;padding:.05em .3em;border-radius:3px;vertical-align:middle;line-height:1.3;background:#7289da;color:#fff;font-size:.625em;font-weight:500}.postamble{margin:1.4em .3em .6em .3em;padding:1em;border-top:1px solid}</style><style>body{background-color:#36393e;color:#dcddde}a{color:#0096cf}.spoiler-text{background-color:rgba(255,255,255,.1)}.spoiler--hidden .spoiler-text{background-color:#202225}.spoiler--hidden:hover .spoiler-text{background-color:rgba(32,34,37,.8)}.quote{border-color:#4f545c}.pre{background-color:#2f3136!important}.pre--multiline{border-color:#282b30!important;color:#b9bbbe!important}.preamble_entry{color:#fff}.message-group{border-color:rgba(255,255,255,.1)}.author-name{color:#fff}.timestamp{color:rgba(255,255,255,.2)}.message--highlighted{background-color:rgba(114,137,218,.2)!important}.message--pinned{background-color:rgba(249,168,37,.05)}.edited-timestamp{color:rgba(255,255,255,.2)}.embed-color-pill--default{background-color:rgba(79,84,92,1)}.embed-content-container{background-color:rgba(46,48,54,.3);border-color:rgba(46,48,54,.6)}.embed-author-name{color:#fff}.embed-author-name-link{color:#fff}.embed-title{color:#fff}.embed-description{color:rgba(255,255,255,.6)}.embed-field-name{color:#fff}.embed-field-value{color:rgba(255,255,255,.6)}.embed-footer{color:rgba(255,255,255,.6)}.reaction{background-color:rgba(255,255,255,.05)}.reaction-count{color:rgba(255,255,255,.3)}.postamble{border-color:rgba(255,255,255,.1)}.postamble_entry{color:#fff}</style><link href=https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.6/styles/solarized-dark.min.css rel=stylesheet><script src=https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.6/highlight.min.js></script><script>document.addEventListener('DOMContentLoaded', () => {document.querySelectorAll('.pre--multiline').forEach(block => hljs.highlightBlock(block));});</script><script>function scrollToMessage(e,t){var o=document.getElementById(\"message-\"+t);o&&(e.preventDefault(),o.classList.add(\"message--highlighted\"),window.scrollTo({top:o.getBoundingClientRect().top-document.body.getBoundingClientRect().top-window.innerHeight/2,behavior:\"smooth\"}),window.setTimeout(function(){o.classList.remove(\"message--highlighted\")},2e3))}function showSpoiler(e,t){t&&t.classList.contains(\"spoiler--hidden\")&&(e.preventDefault(),t.classList.remove(\"spoiler--hidden\"))}</script><div class=preamble><div class=preamble_guild-icon-container><img alt=\"Guild icon\"class=preamble_guild-icon src=%guild-icon%></div><div class=preamble_entries-container><div class=preamble_entry>Eric's Support Server</div><div class=preamble_entry>%channel_category% / %channel_name%</div></div></div><div class=chatlog>";
	private static final String MESSAGE_TEMPLATE = "<div class=message-group><div class=author-avatar-container><img alt=Avatar class=author-avatar src=%avatar%></div><div class=messages><span class=author-name data-user-id=%user-id% style=color:rgb(%role-color-r%,%role-color-g%,%role-color-b%) title=%username-full%>%username%</span> <span class=timestamp>%time%</span><div class=message data-message-id=%messgae-id% id=message-%messgae-id%><div class=content><div class=markdown>%message%</div></div></div></div></div>";
	private static final String FOOTER = "<div class=postamble><div class=postamble_entry>Exported %date% by %support_server%</div></div>";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public static String convert(JSONTicket ticket) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader(ticket));
		for(JSONMessage msg : ticket.getMessages()) {
			builder.append(getMessage(msg));
		}
		builder.append(getFooter(ticket));
		
		return builder.toString();
		
	}
	
	private static String getHeader(JSONTicket ticket) {
		return HEADER
				.replace("%channel_category%", "Tickets")
				.replace("%channel_name%", ticket.getChannelName())
				.replace("%guild-icon%", Main.getGuild().getIconUrl())
				;
	}
	
	private static String getMessage(JSONMessage msg) {
		
		JSONMember member = msg.getMember();
		
		return MESSAGE_TEMPLATE
				.replace("%avatar%", member.getAvatar())
				.replace("%username%", member.getName())
				.replace("%user-id%", "null")
				.replace("%role-color%", "#000")
				.replace("%time%", msg.getEpochMillis() + "")
				.replace("%message-id%", "null")
				.replace("%message%", msg.getMessage())
				
				;
		
	}
	
	private static String getFooter(JSONTicket ticket) {
		return FOOTER
				.replace("%date%", sdf.format(new Date()).toString())
				.replace("%support_server%", Main.getJda().getSelfUser().getName())

				;
	}
	
}
