package org.golde.discordbot.supportserver.util;

//https://commons.apache.org/proper/commons-lang/apidocs/src-html/org/apache/commons/lang3/StringUtils.html
public class StringUtil {

	/**
	 * <p>Checks if a CharSequence is empty ("") or null.</p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>NOTE: This method changed in Lang version 2.0.
	 * It no longer trims the CharSequence.
	 * That functionality is available in isBlank().</p>
	 *
	 * @param cs  the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	// Abbreviating
	//-----------------------------------------------------------------------
	/**
	 * <p>Abbreviates a String using ellipses. This will turn
	 * "Now is the time for all good men" into "Now is the time for..."</p>
	 *
	 * <p>Specifically:</p>
	 * <ul>
	 *   <li>If the number of characters in {@code str} is less than or equal to
	 *       {@code maxWidth}, return {@code str}.</li>
	 *   <li>Else abbreviate it to {@code (substring(str, 0, max-3) + "...")}.</li>
	 *   <li>If {@code maxWidth} is less than {@code 4}, throw an
	 *       {@code IllegalArgumentException}.</li>
	 *   <li>In no case will it return a String of length greater than
	 *       {@code maxWidth}.</li>
	 * </ul>
	 *
	 * <pre>
	 * StringUtils.abbreviate(null, *)      = null
	 * StringUtils.abbreviate("", 4)        = ""
	 * StringUtils.abbreviate("abcdefg", 6) = "abc..."
	 * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
	 * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
	 * StringUtils.abbreviate("abcdefg", 4) = "a..."
	 * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @param maxWidth  maximum length of result String, must be at least 4
	 * @return abbreviated String, {@code null} if null String input
	 * @throws IllegalArgumentException if the width is too small
	 * @since 2.0
	 */
	public static String abbreviate(final String str, final int maxWidth) {
		final String defaultAbbrevMarker = "...";
		return abbreviate(str, defaultAbbrevMarker, 0, maxWidth);
	}

	/**
	 * <p>Abbreviates a String using ellipses. This will turn
	 * "Now is the time for all good men" into "...is the time for..."</p>
	 *
	 * <p>Works like {@code abbreviate(String, int)}, but allows you to specify
	 * a "left edge" offset.  Note that this left edge is not necessarily going to
	 * be the leftmost character in the result, or the first character following the
	 * ellipses, but it will appear somewhere in the result.
	 *
	 * <p>In no case will it return a String of length greater than
	 * {@code maxWidth}.</p>
	 *
	 * <pre>
	 * StringUtils.abbreviate(null, *, *)                = null
	 * StringUtils.abbreviate("", 0, 4)                  = ""
	 * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
	 * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
	 * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
	 * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
	 * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
	 * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
	 * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
	 * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
	 * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
	 * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
	 * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @param offset  left edge of source String
	 * @param maxWidth  maximum length of result String, must be at least 4
	 * @return abbreviated String, {@code null} if null String input
	 * @throws IllegalArgumentException if the width is too small
	 * @since 2.0
	 */
	public static String abbreviate(final String str, final int offset, final int maxWidth) {
		final String defaultAbbrevMarker = "...";
		return abbreviate(str, defaultAbbrevMarker, offset, maxWidth);
	}

	/**
	 * <p>Abbreviates a String using another given String as replacement marker. This will turn
	 * "Now is the time for all good men" into "Now is the time for..." if "..." was defined
	 * as the replacement marker.</p>
	 *
	 * <p>Specifically:</p>
	 * <ul>
	 *   <li>If the number of characters in {@code str} is less than or equal to
	 *       {@code maxWidth}, return {@code str}.</li>
	 *   <li>Else abbreviate it to {@code (substring(str, 0, max-abbrevMarker.length) + abbrevMarker)}.</li>
	 *   <li>If {@code maxWidth} is less than {@code abbrevMarker.length + 1}, throw an
	 *       {@code IllegalArgumentException}.</li>
	 *   <li>In no case will it return a String of length greater than
	 *       {@code maxWidth}.</li>
	 * </ul>
	 *
	 * <pre>
	 * StringUtils.abbreviate(null, "...", *)      = null
	 * StringUtils.abbreviate("abcdefg", null, *)  = "abcdefg"
	 * StringUtils.abbreviate("", "...", 4)        = ""
	 * StringUtils.abbreviate("abcdefg", ".", 5)   = "abcd."
	 * StringUtils.abbreviate("abcdefg", ".", 7)   = "abcdefg"
	 * StringUtils.abbreviate("abcdefg", ".", 8)   = "abcdefg"
	 * StringUtils.abbreviate("abcdefg", "..", 4)  = "ab.."
	 * StringUtils.abbreviate("abcdefg", "..", 3)  = "a.."
	 * StringUtils.abbreviate("abcdefg", "..", 2)  = IllegalArgumentException
	 * StringUtils.abbreviate("abcdefg", "...", 3) = IllegalArgumentException
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @param abbrevMarker  the String used as replacement marker
	 * @param maxWidth  maximum length of result String, must be at least {@code abbrevMarker.length + 1}
	 * @return abbreviated String, {@code null} if null String input
	 * @throws IllegalArgumentException if the width is too small
	 * @since 3.6
	 */
	public static String abbreviate(final String str, final String abbrevMarker, final int maxWidth) {
		return abbreviate(str, abbrevMarker, 0, maxWidth);
	}

	/**
	 * <p>Abbreviates a String using a given replacement marker. This will turn
	 * "Now is the time for all good men" into "...is the time for..." if "..." was defined
	 * as the replacement marker.</p>
	 *
	 * <p>Works like {@code abbreviate(String, String, int)}, but allows you to specify
	 * a "left edge" offset.  Note that this left edge is not necessarily going to
	 * be the leftmost character in the result, or the first character following the
	 * replacement marker, but it will appear somewhere in the result.
	 *
	 * <p>In no case will it return a String of length greater than {@code maxWidth}.</p>
	 *
	 * <pre>
	 * StringUtils.abbreviate(null, null, *, *)                 = null
	 * StringUtils.abbreviate("abcdefghijklmno", null, *, *)    = "abcdefghijklmno"
	 * StringUtils.abbreviate("", "...", 0, 4)                  = ""
	 * StringUtils.abbreviate("abcdefghijklmno", "---", -1, 10) = "abcdefg---"
	 * StringUtils.abbreviate("abcdefghijklmno", ",", 0, 10)    = "abcdefghi,"
	 * StringUtils.abbreviate("abcdefghijklmno", ",", 1, 10)    = "abcdefghi,"
	 * StringUtils.abbreviate("abcdefghijklmno", ",", 2, 10)    = "abcdefghi,"
	 * StringUtils.abbreviate("abcdefghijklmno", "::", 4, 10)   = "::efghij::"
	 * StringUtils.abbreviate("abcdefghijklmno", "...", 6, 10)  = "...ghij..."
	 * StringUtils.abbreviate("abcdefghijklmno", "*", 9, 10)    = "*ghijklmno"
	 * StringUtils.abbreviate("abcdefghijklmno", "'", 10, 10)   = "'ghijklmno"
	 * StringUtils.abbreviate("abcdefghijklmno", "!", 12, 10)   = "!ghijklmno"
	 * StringUtils.abbreviate("abcdefghij", "abra", 0, 4)       = IllegalArgumentException
	 * StringUtils.abbreviate("abcdefghij", "...", 5, 6)        = IllegalArgumentException
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @param abbrevMarker  the String used as replacement marker
	 * @param offset  left edge of source String
	 * @param maxWidth  maximum length of result String, must be at least 4
	 * @return abbreviated String, {@code null} if null String input
	 * @throws IllegalArgumentException if the width is too small
	 * @since 3.6
	 */
	public static String abbreviate(final String str, final String abbrevMarker, int offset, final int maxWidth) {
		if (isEmpty(str) || isEmpty(abbrevMarker)) {
			return str;
		}

		final int abbrevMarkerLength = abbrevMarker.length();
		final int minAbbrevWidth = abbrevMarkerLength + 1;
		final int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;

		if (maxWidth < minAbbrevWidth) {
			throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", minAbbrevWidth));
		}
		if (str.length() <= maxWidth) {
			return str;
		}
		if (offset > str.length()) {
			offset = str.length();
		}
		if (str.length() - offset < maxWidth - abbrevMarkerLength) {
			offset = str.length() - (maxWidth - abbrevMarkerLength);
		}
		if (offset <= abbrevMarkerLength+1) {
			return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
		}
		if (maxWidth < minAbbrevWidthOffset) {
			throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
		}
		if (offset + maxWidth - abbrevMarkerLength < str.length()) {
			return abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength);
		}
		return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
	}

	/**
	 * <p>Abbreviates a String to the length passed, replacing the middle characters with the supplied
	 * replacement String.</p>
	 *
	 * <p>This abbreviation only occurs if the following criteria is met:</p>
	 * <ul>
	 * <li>Neither the String for abbreviation nor the replacement String are null or empty </li>
	 * <li>The length to truncate to is less than the length of the supplied String</li>
	 * <li>The length to truncate to is greater than 0</li>
	 * <li>The abbreviated String will have enough room for the length supplied replacement String
	 * and the first and last characters of the supplied String for abbreviation</li>
	 * </ul>
	 * <p>Otherwise, the returned String will be the same as the supplied String for abbreviation.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.abbreviateMiddle(null, null, 0)      = null
	 * StringUtils.abbreviateMiddle("abc", null, 0)      = "abc"
	 * StringUtils.abbreviateMiddle("abc", ".", 0)      = "abc"
	 * StringUtils.abbreviateMiddle("abc", ".", 3)      = "abc"
	 * StringUtils.abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
	 * </pre>
	 *
	 * @param str  the String to abbreviate, may be null
	 * @param middle the String to replace the middle characters with, may be null
	 * @param length the length to abbreviate {@code str} to.
	 * @return the abbreviated String if the above criteria is met, or the original String supplied for abbreviation.
	 * @since 2.5
	 */
	public static String abbreviateMiddle(final String str, final String middle, final int length) {
		if (isEmpty(str) || isEmpty(middle)) {
			return str;
		}

		if (length >= str.length() || length < middle.length()+2) {
			return str;
		}

		final int targetSting = length-middle.length();
		final int startOffset = targetSting/2+targetSting%2;
		final int endOffset = str.length()-targetSting/2;

		return str.substring(0, startOffset) +
				middle +
				str.substring(endOffset);
	}

}
