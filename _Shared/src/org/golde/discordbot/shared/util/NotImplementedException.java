package org.golde.discordbot.shared.util;

public class NotImplementedException extends UnsupportedOperationException {

	private static final long serialVersionUID = 7526106521092643258L;

	/**
     * Creates a NotImplementedException.
     */
    public NotImplementedException() {
        super();
    }

    /**
     * Creates a NotImplementedException.
     *
     * @param message description of the exception
     */
    public NotImplementedException(final String message) {
        super(message);
    }

    /**
     * Creates a NotImplementedException.
     *
     * @param cause cause of the exception
     */
    public NotImplementedException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a NotImplementedException.
     *
     * @param message description of the exception
     * @param cause cause of the exception
     */
    public NotImplementedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}