package com.sismics.util;

import com.sismics.docs.core.constant.AclTargetType;
import com.sismics.docs.core.util.SecurityUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import jakarta.json.JsonValue;
import java.util.Arrays;
import java.util.Locale;

/**
 * Additional utility coverage tests.
 */
public class TestUtilityCoverage {

    @Test
    public void localeUtilTest() {
        Assert.assertEquals(Locale.ENGLISH, LocaleUtil.getLocale(null));
        Assert.assertEquals(Locale.ENGLISH, LocaleUtil.getLocale(""));
        Assert.assertEquals(new Locale("fr", "FR"), LocaleUtil.getLocale("fr_FR"));
        Assert.assertEquals(new Locale("zh", "TW", "HK"), LocaleUtil.getLocale("zh_TW_HK"));
    }

    @Test
    public void httpUtilTest() {
        String expiresHeader = HttpUtil.buildExpiresHeader(60_000L);
        Assert.assertNotNull(expiresHeader);
        Assert.assertTrue(expiresHeader.length() > 10);
    }

    @Test
    public void jsonUtilTest() {
        Assert.assertSame(JsonValue.NULL, JsonUtil.nullable((String) null));
        Assert.assertSame(JsonValue.NULL, JsonUtil.nullable((Integer) null));
        Assert.assertSame(JsonValue.NULL, JsonUtil.nullable((Long) null));
    }

    @Test
    public void messageUtilTest() {
        Assert.assertEquals("您的电子邮件客户端不支持HTML格式邮件",
                MessageUtil.getMessage(Locale.CHINA, "email.no_html.error"));
        Assert.assertEquals("**missing.key**", MessageUtil.getMessage(Locale.ENGLISH, "missing.key"));
    }

    @Test
    public void securityUtilTest() {
        Assert.assertTrue(SecurityUtil.skipAclCheck(Arrays.asList("foo", "admin")));
        Assert.assertTrue(SecurityUtil.skipAclCheck(Arrays.asList("bar", "administrators")));
        Assert.assertFalse(SecurityUtil.skipAclCheck(Arrays.asList("foo", "bar")));
    }

    @Test
    public void environmentUtilTest() {
        EnvironmentUtil.setWebappContext(false);
        Assert.assertTrue(EnvironmentUtil.isUnitTest());
        Assert.assertFalse(EnvironmentUtil.isWebappContext());

        EnvironmentUtil.setWebappContext(true);
        Assert.assertFalse(EnvironmentUtil.isUnitTest());
        Assert.assertTrue(EnvironmentUtil.isWebappContext());

        EnvironmentUtil.setWebappContext(false);
    }

    @Test
    public void htmlToPlainTextTest() {
        String html = "<html><body>"
                + "<h1>Heading</h1>"
                + "<p>Paragraph one with a very long sentence that should wrap because it is deliberately longer than the visitor width limit and therefore exercises the wrapping branch.</p>"
                + "<ul><li>First item</li><li>Second item</li></ul>"
                + "<dl><dt>Term</dt><dd>Definition</dd></dl>"
                + "<a href='https://example.com/doc'>Link</a>"
                + "<br/>"
                + "</body></html>";

        Element body = Jsoup.parse(html, "https://example.com/base/").body();
        String plainText = new HtmlToPlainText().getPlainText(body);

        Assert.assertTrue(plainText.contains("Heading"));
        Assert.assertTrue(plainText.contains("First item"));
        Assert.assertTrue(plainText.contains("Term"));
        Assert.assertTrue(plainText.contains("Definition"));
        Assert.assertTrue(plainText.contains("<https://example.com/doc>"));
        Assert.assertTrue(plainText.contains("Paragraph one"));
    }

    @Test
    public void aclTargetTypeReferenceTest() {
        Assert.assertEquals(AclTargetType.USER, AclTargetType.valueOf("USER"));
    }
}