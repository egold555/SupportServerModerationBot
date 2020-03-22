package org.golde.discordbot.supportserver.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

public class SuggestionImageGenerator {
	
	private static final int SPACE_BETWEEN_OPTIONS = 44;
	private static final int[] OPTIONS_AWAY_FROM_EDGES_WIDTH = new int[] {9, 9};
	private static int[] PERCENTAGE_COMPONENT_SIZE = new int[] {-1, 40}; //see updateVariables()

	private static final Color BG_COLOR =  new Color(34, 35, 41);
	private static final Color TEXT_COLOR = Color.WHITE;
	private static final Color GRADENT_LEFT = new Color(100, 116, 230);
	private static final Color GRADENT_RIGHT = new Color(209, 112, 245);

	private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("##.##");
	
	private static final Font FONT = new Font("Arial", Font.PLAIN, 20);

	public static int[] paint(Graphics2D g2, Poll poll) {
		return paint(g2, poll, 0, 0);
	}
	public static int[] paint(Graphics2D g2, Poll poll, int x, int y) {

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);


		String longestString = longestString(poll.getSuggestions(), poll.getTitle());
		int width = getStringPixelLength(longestString, g2);

		

		int height = 46 * poll.getSuggestions().size() + 24;
		updateVariables(width);
		drawBackground(g2, x, y, width, height);
		drawTitleText(g2, x, y - 17, poll);

		int newY = 15 + y;
		for(Suggestion s : poll.getSuggestions()) {
			drawPercentageBar(g2, x, newY, s);
			newY += SPACE_BETWEEN_OPTIONS;
		}
		
		return new int[] {width, height};

	}


	private static String longestString(List<Suggestion> set, String title) {
		String max = "";
		for(Suggestion s : set) {
			if(s.getQuestion().length() > max.length()) {
				max = s.getQuestion();
			}
		}
		if(title.length() > max.length()) {
			max = title;
		}
		return max;
	}

	private static int getStringPixelLength(String text, Graphics2D g) {
		FontMetrics metrics = g.getFontMetrics(FONT);
		return metrics.stringWidth(text) * 2;
	}

	private static void updateVariables(int width) {
		PERCENTAGE_COMPONENT_SIZE[0] = width - (OPTIONS_AWAY_FROM_EDGES_WIDTH[0] * 2);
	}

	private static void drawPercentageBar(Graphics2D g2, int x, int y, Suggestion sugg) {
		drawPercentageComponentBackground(g2, x, y);
		drawPercentageComponentOverlay(g2, x, y, sugg.getPercent());
		drawPercentageComponentOverlayText(g2, x, y, sugg);
	}

	private static void drawPercentageComponentBackground(Graphics2D g2, int x, int y) {
		drawRoundedRectangle(g2, x + OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1], PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1], 10, new Color(45, 46, 51));
	}

	private static void drawPercentageComponentOverlay(Graphics2D g2, int x, int y, double percentage) {

		double w = mapRange(0, 1, 0, PERCENTAGE_COMPONENT_SIZE[0], percentage);
		GradientPaint color = new GradientPaint(0, 0, GRADENT_LEFT, PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1], GRADENT_RIGHT);

		drawRoundedRectangle(g2, x + OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1], w, 40, 10, color);


	}

	private static void drawPercentageComponentOverlayText(Graphics2D g2, int x, int y,  Suggestion sugg) {
		g2.setColor(TEXT_COLOR);

		String thing = sugg.getQuestion() + ": " + PERCENTAGE_FORMAT.format(sugg.getPercent() * 100) + "% (" + sugg.getId() + ")";

		
		drawCenteredString(g2, thing, new Rectangle(new Point(x +  OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1]), new Dimension(PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1])), FONT);

	}
	
	private static void drawTitleText(Graphics2D g2, int x, int y, Poll poll) {
		g2.setColor(Color.YELLOW);

		String thing = poll.title + " (" + poll.getId() + ")";
		drawCenteredString(g2, thing, new Rectangle(new Point(x +  OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1]), new Dimension(PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1])), FONT.deriveFont(Font.BOLD));

	}

	private static void drawBackground(Graphics2D g2, int x, int y, int width, int height) {
		drawRoundedRectangle(g2, x, y, width, height, 50, BG_COLOR);
	}

	private static void drawRoundedRectangle(Graphics2D g2, double x, double y, double w, double h, double arc, Paint paint) {
		g2.setPaint(paint);
		g2.fill(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
	}

	private static double mapRange(double a1, double a2, double b1, double b2, double s){
		return b1 + ((s - a1)*(b2 - b1))/(a2 - a1);
	}

	private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(font);
		g.drawString(text, x, y);
	}
	

	@Getter
	public static class Suggestion {

		private final int id;
		private final String question;
		
		@Setter
		private double percent;
		
		public Suggestion(int id, String thing) {
			this.id = id;
			this.question = thing;
		}
	}
	
	@Getter
	public static class Poll {
		private final String title;
		private final int id;
		private final List<Suggestion> suggestions;
		
		public Poll(int id, String title) {
			this(id, title, new ArrayList<Suggestion>());
		}
		
		public Poll(int id, String title, List<Suggestion> suggestions) {
			this.id = id;
			this.title = title;
			this.suggestions = suggestions;
		}
		
		public void addSuggestion(Suggestion s) {
			suggestions.add(s);
		}
		
		public boolean removeSuggestion(Suggestion s) {
			return suggestions.remove(s);
		}
		
//		public boolean removeSuggestion(int id) {
//			ListIterator<Suggestion> sugg = suggestions.listIterator();
//			while(sugg.hasNext()) {
//				Suggestion next = sugg.next();
//				if(next.getId() == id) {
//					sugg.remove();
//					return true;
//				}
//			}
//			return false;
//		}
		
		public void sendImage(TextChannel channel) {
			BufferedImage bm = new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_ARGB);
			int[] cropXY = SuggestionImageGenerator.paint((Graphics2D) bm.getGraphics(),this, 0, 0);
			BufferedImage bi = bm.getSubimage(0, 0, cropXY[0], cropXY[1]);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write( bi, "PNG", baos );
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				channel.sendFile(imageInByte, "img.png").queue();;
				baos.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
