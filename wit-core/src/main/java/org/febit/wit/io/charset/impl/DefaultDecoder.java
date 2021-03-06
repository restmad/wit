// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.io.charset.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import org.febit.wit.io.Buffers;
import org.febit.wit.io.charset.Decoder;

/**
 *
 * @author zqq90
 */
public class DefaultDecoder implements Decoder {

    private final CharsetDecoder charsetDecoder;
    private final double expansionFactor;
    private final Buffers buffers;

    public DefaultDecoder(String encoding, Buffers buffers) {
        this.expansionFactor = (double) (this.charsetDecoder = newDecoder(encoding)).maxCharsPerByte();
        this.buffers = buffers;
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length, final Writer writer) throws IOException {
        if (bytes != null && length != 0) {
            final char[] chars;
            final CharsetDecoder decoder;
            final CharBuffer cb;
            (decoder = this.charsetDecoder).reset().decode(
                    ByteBuffer.wrap(bytes, offset, length),
                    cb = CharBuffer.wrap(chars = this.buffers.getChars((int) (length * this.expansionFactor))),
                    true);
            decoder.flush(cb);
            writer.write(chars, 0, cb.position());
        }
    }

    private static CharsetDecoder newDecoder(String csn) throws UnsupportedCharsetException {
        return Charset.forName(csn).newDecoder();
    }
}
