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
import java.text.DecimalFormat;
import java.util.List;

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

	
	public static int[] paint(Graphics2D g2, List<Suggestion> suggestions, int x, int y) {

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);


		String longestString = longestString(suggestions);
		int width = getStringPixelLength(longestString, g2);

		

		int height = 46 * suggestions.size() + 24;
		updateVariables(width);
		drawBackground(g2, x, y, width, height);

		int newY = 10 + y;
		for(Suggestion s : suggestions) {
			drawPercentageBar(g2, x, newY, s.getPercent(), s.getQuestion());
			newY += SPACE_BETWEEN_OPTIONS;
		}
		
		return new int[] {width, height};

	}


	private static String longestString(List<Suggestion> set) {
		String max = "";
		for(Suggestion s : set) {
			if(s.getQuestion().length() > max.length()) {
				max = s.getQuestion();
			}
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

	private static void drawPercentageBar(Graphics2D g2, int x, int y, double value, String text) {
		drawPercentageComponentBackground(g2, x, y);
		drawPercentageComponentOverlay(g2, x, y, value);
		drawPercentageComponentOverlayText(g2, x, y, value, text);
	}

	private static void drawPercentageComponentBackground(Graphics2D g2, int x, int y) {
		drawRoundedRectangle(g2, x + OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1], PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1], 10, new Color(45, 46, 51));
	}

	private static void drawPercentageComponentOverlay(Graphics2D g2, int x, int y, double percentage) {

		double w = mapRange(0, 1, 0, PERCENTAGE_COMPONENT_SIZE[0], percentage);
		GradientPaint color = new GradientPaint(0, 0, GRADENT_LEFT, PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1], GRADENT_RIGHT);

		drawRoundedRectangle(g2, x + OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1], w, 40, 10, color);


	}

	private static void drawPercentageComponentOverlayText(Graphics2D g2, int x, int y, double percentage, String thing) {
		g2.setColor(TEXT_COLOR);

		thing += ": " + PERCENTAGE_FORMAT.format(percentage * 100) + "%";

		drawCenteredString(g2, thing, new Rectangle(new Point(x +  OPTIONS_AWAY_FROM_EDGES_WIDTH[0], y + OPTIONS_AWAY_FROM_EDGES_WIDTH[1]), new Dimension(PERCENTAGE_COMPONENT_SIZE[0], PERCENTAGE_COMPONENT_SIZE[1])), FONT);

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
	
	public static class Suggestion {

		private final String question;
		private final double percent;
		
		public Suggestion(String question, double percent) {
			this.question = question;
			this.percent = percent;
		}
		
		public double getPercent() {
			return percent;
		}
		public String getQuestion() {
			return question;
		}
		
		
	}
	
}
