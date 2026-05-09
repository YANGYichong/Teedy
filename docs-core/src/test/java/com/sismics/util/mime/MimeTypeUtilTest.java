package com.sismics.util.mime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link MimeTypeUtil}.
 */
public class MimeTypeUtilTest {

    @Test
    public void testGetFileExtensionForKnownMimeTypes() {
        assertEquals("zip", MimeTypeUtil.getFileExtension(MimeType.APPLICATION_ZIP));
        assertEquals("gif", MimeTypeUtil.getFileExtension(MimeType.IMAGE_GIF));
        assertEquals("jpg", MimeTypeUtil.getFileExtension(MimeType.IMAGE_JPEG));
        assertEquals("png", MimeTypeUtil.getFileExtension(MimeType.IMAGE_PNG));
        assertEquals("pdf", MimeTypeUtil.getFileExtension(MimeType.APPLICATION_PDF));
        assertEquals("odt", MimeTypeUtil.getFileExtension(MimeType.OPEN_DOCUMENT_TEXT));
        assertEquals("docx", MimeTypeUtil.getFileExtension(MimeType.OFFICE_DOCUMENT));
        assertEquals("txt", MimeTypeUtil.getFileExtension(MimeType.TEXT_PLAIN));
        assertEquals("csv", MimeTypeUtil.getFileExtension(MimeType.TEXT_CSV));
        assertEquals("mp4", MimeTypeUtil.getFileExtension(MimeType.VIDEO_MP4));
        assertEquals("webm", MimeTypeUtil.getFileExtension(MimeType.VIDEO_WEBM));
    }

    @Test
    public void testGetFileExtensionForUnknownMimeType() {
        assertEquals("bin", MimeTypeUtil.getFileExtension("application/x-custom"));
    }
}