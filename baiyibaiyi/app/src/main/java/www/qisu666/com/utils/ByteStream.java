package www.qisu666.com.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteStream {

	private ByteArrayOutputStream mOutput;
	private ByteArrayInputStream mInput;

	public ByteStream() {
		mOutput = new ByteArrayOutputStream();
		mInput = new ByteArrayInputStream(mOutput.toByteArray());
	}

	public void write(byte[] buffer) {
		mOutput.write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int offset, int count) {
		mOutput.write(buffer, offset, count);
	}

	public int read(byte[] bytes, int offset, int byteCount) {
		int readCount = 0;

		if (mInput.available() > 0) {
			readCount = mInput.read(bytes, offset, byteCount);
			offset += readCount;
			byteCount -= readCount;

			mInput.mark(0);
		}

		if (byteCount > 0 && mOutput.size() > 0) {
			mInput = new ByteArrayInputStream(mOutput.toByteArray());
			readCount += mInput.read(bytes, offset, byteCount);

			mOutput.reset();
			mInput.mark(0);
		}

		return readCount;
	}

	public int peek(byte[] bytes, int offset, int byteCount) {
		int readCount = 0;

		if (mInput.available() > 0) {
			readCount = mInput.read(bytes, offset, byteCount);
			offset += readCount;
			byteCount -= readCount;

			mInput.reset();
		}

		if (byteCount > 0 && mOutput.size() > 0) {
			ByteArrayInputStream tmp = new ByteArrayInputStream(mOutput.toByteArray());
			readCount += tmp.read(bytes, offset, byteCount);
		}

		return readCount;
	}

	public long skip(long byteCount) {
		long skipCount = 0;

		if (mInput.available() > 0) {
			skipCount = mInput.skip(byteCount);
			byteCount -= skipCount;

			mInput.mark(0);
		}

		if (byteCount > 0 && mOutput.size() > 0) {
			mInput = new ByteArrayInputStream(mOutput.toByteArray());
			skipCount += mInput.skip(byteCount);

			mOutput.reset();
			mInput.mark(0);
		}

		return skipCount;
	}

	public int available() {
		return mInput.available() + mOutput.size();
	}

}
