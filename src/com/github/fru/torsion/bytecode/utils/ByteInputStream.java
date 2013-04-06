package com.github.fru.torsion.bytecode.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ByteInputStream {

	private InputStream input;

	public InputStream getInput() {
		return input;
	}

	private Position position;

	public ByteInputStream(InputStream input) {
		this.input = input;
		position = new Position();
	}

	private int maxReads = -1;

	public ByteInputStream(ByteInputStream reader, int maxReads) {
		this(reader.input);
		this.maxReads = maxReads;
	}

	public int findNext() throws EOFException {
		if (maxReads == 0) throw new EOFException();
		maxReads--;
		int out = -1;
		try {
			out = input.read();
		} catch (IOException ex) {
			// Do nothing
		}
		if (out == -1) {
			throw new EOFException();
		}
		position.move(out == '\n');
		return out;
	}

	public int findInt() throws EOFException {
		return (findNext() << 24) + ((findNext() & 0xFF) << 16) + ((findNext() & 0xFF) << 8) + (findNext() & 0xFF);
	}

	public int findShort() throws EOFException {
		return ((findNext() & 0xFF) << 8) + (findNext() & 0xFF);
	}

	public long findLong() throws EOFException {
		return ((long) findInt() << 32) + (findInt() & 0xFFFFFFFFL);
	}

	public Position getPosition() {
		return position;
	}

	public class Position {
		private int lineCount = 0;
		private int lineByte = -1;
		private int byteCount = 0;

		public int getLineCount() {
			return lineCount;
		}

		public int getLineByte() {
			return lineByte;
		}

		public int getByteCount() {
			return byteCount;
		}

		public void move(boolean isNewLine) {
			lineByte++;
			byteCount++;
			if (isNewLine) {
				lineCount++;
				lineByte = 0;
			}
		}

		@Override
		public String toString() {
			return "line " + lineCount + " @ " + lineByte;
		}
	}

	public int getByteCount() {
		return getPosition().getByteCount();
	}
}
