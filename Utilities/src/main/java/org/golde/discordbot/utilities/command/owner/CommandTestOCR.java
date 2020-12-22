package org.golde.discordbot.utilities.command.owner;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.utilities.event.OCRCommonErrors;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.internal.JDAImpl;

public class CommandTestOCR extends OwnerCommand {

	public CommandTestOCR(ESSBot bot) {
		super(bot, "ocr", null, "Test OCR");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		OCRCommonErrors.doOcr(bot, event.getTextChannel(), event.getMember(), "{\"ParsedResults\":[{\"TextOverlay\":{\"Lines\":[],\"HasOverlay\":false,\"Message\":\"Text overlay is not provided as it is not requested\"},\"TextOrientation\":\"0\",\"FileParseExitCode\":1,\"ParsedText\":\"ors (6 items)\\r\\nThe method addLayer(U) in the type Render...\\r\\nThe method addLayer(U) in the type Render...\\r\\nThe method addLayer(U) in the type Render...\\r\\nThe method addLayer(U) in the type Render...\\r\\nThe method addLayer(U) in the type Render...\\r\\nThe method addLayer(U) in the type Render...\\r\\nResource\\r\\nRenderPlayer.j...\\r\\nRenderDragon...\\r\\nRenderEnder...\\r\\nRenderSheep....\\r\\nRenderWolf.java\\r\\nRenderSpider....\\r\\n\",\"ErrorMessage\":\"\",\"ErrorDetails\":\"\"}],\"OCRExitCode\":1,\"IsErroredOnProcessing\":false,\"ProcessingTimeInMilliseconds\":\"796\",\"SearchablePDFURL\":\"Searchable PDF not generated as it was not requested.\"}\r\n" + 
				"", new Attachment(0, "https://cdn.discordapp.com/attachments/604769552416374807/763167044123230208/Screen_Shot_2020-10-04_at_11.png", "https://cdn.discordapp.com/attachments/604769552416374807/763167044123230208/Screen_Shot_2020-10-04_at_11.png", "test", 1, 1, 1, (JDAImpl)bot.getJda()));
		
	}

}
