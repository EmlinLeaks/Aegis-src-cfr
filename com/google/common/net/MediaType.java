/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.nio.charset.Charset;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Beta
@GwtCompatible
@Immutable
public final class MediaType {
    private static final String CHARSET_ATTRIBUTE = "charset";
    private static final ImmutableListMultimap<String, String> UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of("charset", Ascii.toLowerCase((String)Charsets.UTF_8.name()));
    private static final CharMatcher TOKEN_MATCHER = CharMatcher.ascii().and((CharMatcher)CharMatcher.javaIsoControl().negate()).and((CharMatcher)CharMatcher.isNot((char)' ')).and((CharMatcher)CharMatcher.noneOf((CharSequence)"()<>@,;:\\\"/[]?="));
    private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ascii().and((CharMatcher)CharMatcher.noneOf((CharSequence)"\"\\\r"));
    private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf((CharSequence)" \t\r\n");
    private static final String APPLICATION_TYPE = "application";
    private static final String AUDIO_TYPE = "audio";
    private static final String IMAGE_TYPE = "image";
    private static final String TEXT_TYPE = "text";
    private static final String VIDEO_TYPE = "video";
    private static final String WILDCARD = "*";
    private static final Map<MediaType, MediaType> KNOWN_TYPES = Maps.newHashMap();
    public static final MediaType ANY_TYPE = MediaType.createConstant((String)"*", (String)"*");
    public static final MediaType ANY_TEXT_TYPE = MediaType.createConstant((String)"text", (String)"*");
    public static final MediaType ANY_IMAGE_TYPE = MediaType.createConstant((String)"image", (String)"*");
    public static final MediaType ANY_AUDIO_TYPE = MediaType.createConstant((String)"audio", (String)"*");
    public static final MediaType ANY_VIDEO_TYPE = MediaType.createConstant((String)"video", (String)"*");
    public static final MediaType ANY_APPLICATION_TYPE = MediaType.createConstant((String)"application", (String)"*");
    public static final MediaType CACHE_MANIFEST_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"cache-manifest");
    public static final MediaType CSS_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"css");
    public static final MediaType CSV_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"csv");
    public static final MediaType HTML_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"html");
    public static final MediaType I_CALENDAR_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"calendar");
    public static final MediaType PLAIN_TEXT_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"plain");
    public static final MediaType TEXT_JAVASCRIPT_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"javascript");
    public static final MediaType TSV_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"tab-separated-values");
    public static final MediaType VCARD_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"vcard");
    public static final MediaType WML_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"vnd.wap.wml");
    public static final MediaType XML_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"xml");
    public static final MediaType VTT_UTF_8 = MediaType.createConstantUtf8((String)"text", (String)"vtt");
    public static final MediaType BMP = MediaType.createConstant((String)"image", (String)"bmp");
    public static final MediaType CRW = MediaType.createConstant((String)"image", (String)"x-canon-crw");
    public static final MediaType GIF = MediaType.createConstant((String)"image", (String)"gif");
    public static final MediaType ICO = MediaType.createConstant((String)"image", (String)"vnd.microsoft.icon");
    public static final MediaType JPEG = MediaType.createConstant((String)"image", (String)"jpeg");
    public static final MediaType PNG = MediaType.createConstant((String)"image", (String)"png");
    public static final MediaType PSD = MediaType.createConstant((String)"image", (String)"vnd.adobe.photoshop");
    public static final MediaType SVG_UTF_8 = MediaType.createConstantUtf8((String)"image", (String)"svg+xml");
    public static final MediaType TIFF = MediaType.createConstant((String)"image", (String)"tiff");
    public static final MediaType WEBP = MediaType.createConstant((String)"image", (String)"webp");
    public static final MediaType MP4_AUDIO = MediaType.createConstant((String)"audio", (String)"mp4");
    public static final MediaType MPEG_AUDIO = MediaType.createConstant((String)"audio", (String)"mpeg");
    public static final MediaType OGG_AUDIO = MediaType.createConstant((String)"audio", (String)"ogg");
    public static final MediaType WEBM_AUDIO = MediaType.createConstant((String)"audio", (String)"webm");
    public static final MediaType L24_AUDIO = MediaType.createConstant((String)"audio", (String)"l24");
    public static final MediaType BASIC_AUDIO = MediaType.createConstant((String)"audio", (String)"basic");
    public static final MediaType AAC_AUDIO = MediaType.createConstant((String)"audio", (String)"aac");
    public static final MediaType VORBIS_AUDIO = MediaType.createConstant((String)"audio", (String)"vorbis");
    public static final MediaType WMA_AUDIO = MediaType.createConstant((String)"audio", (String)"x-ms-wma");
    public static final MediaType WAX_AUDIO = MediaType.createConstant((String)"audio", (String)"x-ms-wax");
    public static final MediaType VND_REAL_AUDIO = MediaType.createConstant((String)"audio", (String)"vnd.rn-realaudio");
    public static final MediaType VND_WAVE_AUDIO = MediaType.createConstant((String)"audio", (String)"vnd.wave");
    public static final MediaType MP4_VIDEO = MediaType.createConstant((String)"video", (String)"mp4");
    public static final MediaType MPEG_VIDEO = MediaType.createConstant((String)"video", (String)"mpeg");
    public static final MediaType OGG_VIDEO = MediaType.createConstant((String)"video", (String)"ogg");
    public static final MediaType QUICKTIME = MediaType.createConstant((String)"video", (String)"quicktime");
    public static final MediaType WEBM_VIDEO = MediaType.createConstant((String)"video", (String)"webm");
    public static final MediaType WMV = MediaType.createConstant((String)"video", (String)"x-ms-wmv");
    public static final MediaType FLV_VIDEO = MediaType.createConstant((String)"video", (String)"x-flv");
    public static final MediaType THREE_GPP_VIDEO = MediaType.createConstant((String)"video", (String)"3gpp");
    public static final MediaType THREE_GPP2_VIDEO = MediaType.createConstant((String)"video", (String)"3gpp2");
    public static final MediaType APPLICATION_XML_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"xml");
    public static final MediaType ATOM_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"atom+xml");
    public static final MediaType BZIP2 = MediaType.createConstant((String)"application", (String)"x-bzip2");
    public static final MediaType DART_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"dart");
    public static final MediaType APPLE_PASSBOOK = MediaType.createConstant((String)"application", (String)"vnd.apple.pkpass");
    public static final MediaType EOT = MediaType.createConstant((String)"application", (String)"vnd.ms-fontobject");
    public static final MediaType EPUB = MediaType.createConstant((String)"application", (String)"epub+zip");
    public static final MediaType FORM_DATA = MediaType.createConstant((String)"application", (String)"x-www-form-urlencoded");
    public static final MediaType KEY_ARCHIVE = MediaType.createConstant((String)"application", (String)"pkcs12");
    public static final MediaType APPLICATION_BINARY = MediaType.createConstant((String)"application", (String)"binary");
    public static final MediaType GZIP = MediaType.createConstant((String)"application", (String)"x-gzip");
    public static final MediaType JAVASCRIPT_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"javascript");
    public static final MediaType JSON_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"json");
    public static final MediaType MANIFEST_JSON_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"manifest+json");
    public static final MediaType KML = MediaType.createConstant((String)"application", (String)"vnd.google-earth.kml+xml");
    public static final MediaType KMZ = MediaType.createConstant((String)"application", (String)"vnd.google-earth.kmz");
    public static final MediaType MBOX = MediaType.createConstant((String)"application", (String)"mbox");
    public static final MediaType APPLE_MOBILE_CONFIG = MediaType.createConstant((String)"application", (String)"x-apple-aspen-config");
    public static final MediaType MICROSOFT_EXCEL = MediaType.createConstant((String)"application", (String)"vnd.ms-excel");
    public static final MediaType MICROSOFT_POWERPOINT = MediaType.createConstant((String)"application", (String)"vnd.ms-powerpoint");
    public static final MediaType MICROSOFT_WORD = MediaType.createConstant((String)"application", (String)"msword");
    public static final MediaType NACL_APPLICATION = MediaType.createConstant((String)"application", (String)"x-nacl");
    public static final MediaType NACL_PORTABLE_APPLICATION = MediaType.createConstant((String)"application", (String)"x-pnacl");
    public static final MediaType OCTET_STREAM = MediaType.createConstant((String)"application", (String)"octet-stream");
    public static final MediaType OGG_CONTAINER = MediaType.createConstant((String)"application", (String)"ogg");
    public static final MediaType OOXML_DOCUMENT = MediaType.createConstant((String)"application", (String)"vnd.openxmlformats-officedocument.wordprocessingml.document");
    public static final MediaType OOXML_PRESENTATION = MediaType.createConstant((String)"application", (String)"vnd.openxmlformats-officedocument.presentationml.presentation");
    public static final MediaType OOXML_SHEET = MediaType.createConstant((String)"application", (String)"vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final MediaType OPENDOCUMENT_GRAPHICS = MediaType.createConstant((String)"application", (String)"vnd.oasis.opendocument.graphics");
    public static final MediaType OPENDOCUMENT_PRESENTATION = MediaType.createConstant((String)"application", (String)"vnd.oasis.opendocument.presentation");
    public static final MediaType OPENDOCUMENT_SPREADSHEET = MediaType.createConstant((String)"application", (String)"vnd.oasis.opendocument.spreadsheet");
    public static final MediaType OPENDOCUMENT_TEXT = MediaType.createConstant((String)"application", (String)"vnd.oasis.opendocument.text");
    public static final MediaType PDF = MediaType.createConstant((String)"application", (String)"pdf");
    public static final MediaType POSTSCRIPT = MediaType.createConstant((String)"application", (String)"postscript");
    public static final MediaType PROTOBUF = MediaType.createConstant((String)"application", (String)"protobuf");
    public static final MediaType RDF_XML_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"rdf+xml");
    public static final MediaType RTF_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"rtf");
    public static final MediaType SFNT = MediaType.createConstant((String)"application", (String)"font-sfnt");
    public static final MediaType SHOCKWAVE_FLASH = MediaType.createConstant((String)"application", (String)"x-shockwave-flash");
    public static final MediaType SKETCHUP = MediaType.createConstant((String)"application", (String)"vnd.sketchup.skp");
    public static final MediaType SOAP_XML_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"soap+xml");
    public static final MediaType TAR = MediaType.createConstant((String)"application", (String)"x-tar");
    public static final MediaType WOFF = MediaType.createConstant((String)"application", (String)"font-woff");
    public static final MediaType WOFF2 = MediaType.createConstant((String)"application", (String)"font-woff2");
    public static final MediaType XHTML_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"xhtml+xml");
    public static final MediaType XRD_UTF_8 = MediaType.createConstantUtf8((String)"application", (String)"xrd+xml");
    public static final MediaType ZIP = MediaType.createConstant((String)"application", (String)"zip");
    private final String type;
    private final String subtype;
    private final ImmutableListMultimap<String, String> parameters;
    @LazyInit
    private String toString;
    @LazyInit
    private int hashCode;
    private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on((String)"; ").withKeyValueSeparator((String)"=");

    private static MediaType createConstant(String type, String subtype) {
        return MediaType.addKnownType((MediaType)new MediaType((String)type, (String)subtype, ImmutableListMultimap.<String, String>of()));
    }

    private static MediaType createConstantUtf8(String type, String subtype) {
        return MediaType.addKnownType((MediaType)new MediaType((String)type, (String)subtype, UTF_8_CONSTANT_PARAMETERS));
    }

    private static MediaType addKnownType(MediaType mediaType) {
        KNOWN_TYPES.put((MediaType)mediaType, (MediaType)mediaType);
        return mediaType;
    }

    private MediaType(String type, String subtype, ImmutableListMultimap<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
    }

    public String type() {
        return this.type;
    }

    public String subtype() {
        return this.subtype;
    }

    public ImmutableListMultimap<String, String> parameters() {
        return this.parameters;
    }

    private Map<String, ImmutableMultiset<String>> parametersAsMap() {
        return Maps.transformValues(this.parameters.asMap(), new Function<Collection<String>, ImmutableMultiset<String>>((MediaType)this){
            final /* synthetic */ MediaType this$0;
            {
                this.this$0 = mediaType;
            }

            public ImmutableMultiset<String> apply(Collection<String> input) {
                return ImmutableMultiset.copyOf(input);
            }
        });
    }

    public Optional<Charset> charset() {
        ImmutableSet<E> charsetValues = ImmutableSet.copyOf(this.parameters.get((Object)CHARSET_ATTRIBUTE));
        switch (charsetValues.size()) {
            case 0: {
                return Optional.absent();
            }
            case 1: {
                return Optional.of(Charset.forName((String)((String)Iterables.getOnlyElement(charsetValues))));
            }
        }
        throw new IllegalStateException((String)("Multiple charset values defined: " + charsetValues));
    }

    public MediaType withoutParameters() {
        MediaType mediaType;
        if (this.parameters.isEmpty()) {
            mediaType = this;
            return mediaType;
        }
        mediaType = MediaType.create((String)this.type, (String)this.subtype);
        return mediaType;
    }

    public MediaType withParameters(Multimap<String, String> parameters) {
        return MediaType.create((String)this.type, (String)this.subtype, parameters);
    }

    public MediaType withParameter(String attribute, String value) {
        Preconditions.checkNotNull(attribute);
        Preconditions.checkNotNull(value);
        String normalizedAttribute = MediaType.normalizeToken((String)attribute);
        ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
        Iterator i$ = ((ImmutableCollection)this.parameters.entries()).iterator();
        do {
            if (!i$.hasNext()) {
                builder.put((Object)normalizedAttribute, (Object)MediaType.normalizeParameterValue((String)normalizedAttribute, (String)value));
                MediaType mediaType = new MediaType((String)this.type, (String)this.subtype, (ImmutableListMultimap<String, String>)builder.build());
                return MoreObjects.firstNonNull(KNOWN_TYPES.get((Object)mediaType), mediaType);
            }
            Map.Entry entry = (Map.Entry)i$.next();
            String key = (String)entry.getKey();
            if (normalizedAttribute.equals((Object)key)) continue;
            builder.put((Object)key, entry.getValue());
        } while (true);
    }

    public MediaType withCharset(Charset charset) {
        Preconditions.checkNotNull(charset);
        return this.withParameter((String)CHARSET_ATTRIBUTE, (String)charset.name());
    }

    public boolean hasWildcard() {
        if (WILDCARD.equals((Object)this.type)) return true;
        if (WILDCARD.equals((Object)this.subtype)) return true;
        return false;
    }

    public boolean is(MediaType mediaTypeRange) {
        if (!mediaTypeRange.type.equals((Object)WILDCARD)) {
            if (!mediaTypeRange.type.equals((Object)this.type)) return false;
        }
        if (!mediaTypeRange.subtype.equals((Object)WILDCARD)) {
            if (!mediaTypeRange.subtype.equals((Object)this.subtype)) return false;
        }
        if (!((AbstractCollection)this.parameters.entries()).containsAll(mediaTypeRange.parameters.entries())) return false;
        return true;
    }

    public static MediaType create(String type, String subtype) {
        return MediaType.create((String)type, (String)subtype, ImmutableListMultimap.<String, String>of());
    }

    static MediaType createApplicationType(String subtype) {
        return MediaType.create((String)APPLICATION_TYPE, (String)subtype);
    }

    static MediaType createAudioType(String subtype) {
        return MediaType.create((String)AUDIO_TYPE, (String)subtype);
    }

    static MediaType createImageType(String subtype) {
        return MediaType.create((String)IMAGE_TYPE, (String)subtype);
    }

    static MediaType createTextType(String subtype) {
        return MediaType.create((String)TEXT_TYPE, (String)subtype);
    }

    static MediaType createVideoType(String subtype) {
        return MediaType.create((String)VIDEO_TYPE, (String)subtype);
    }

    private static MediaType create(String type, String subtype, Multimap<String, String> parameters) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(subtype);
        Preconditions.checkNotNull(parameters);
        String normalizedType = MediaType.normalizeToken((String)type);
        String normalizedSubtype = MediaType.normalizeToken((String)subtype);
        Preconditions.checkArgument((boolean)(!WILDCARD.equals((Object)normalizedType) || WILDCARD.equals((Object)normalizedSubtype)), (Object)"A wildcard type cannot be used with a non-wildcard subtype");
        ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();
        Iterator<Map.Entry<String, String>> i$ = parameters.entries().iterator();
        do {
            if (!i$.hasNext()) {
                MediaType mediaType = new MediaType((String)normalizedType, (String)normalizedSubtype, (ImmutableListMultimap<String, String>)builder.build());
                return MoreObjects.firstNonNull(KNOWN_TYPES.get((Object)mediaType), mediaType);
            }
            Map.Entry<String, String> entry = i$.next();
            String attribute = MediaType.normalizeToken((String)entry.getKey());
            builder.put((Object)attribute, (Object)MediaType.normalizeParameterValue((String)attribute, (String)entry.getValue()));
        } while (true);
    }

    private static String normalizeToken(String token) {
        Preconditions.checkArgument((boolean)TOKEN_MATCHER.matchesAllOf((CharSequence)token));
        return Ascii.toLowerCase((String)token);
    }

    private static String normalizeParameterValue(String attribute, String value) {
        String string;
        if (CHARSET_ATTRIBUTE.equals((Object)attribute)) {
            string = Ascii.toLowerCase((String)value);
            return string;
        }
        string = value;
        return string;
    }

    public static MediaType parse(String input) {
        Preconditions.checkNotNull(input);
        Tokenizer tokenizer = new Tokenizer((String)input);
        try {
            String type = tokenizer.consumeToken((CharMatcher)TOKEN_MATCHER);
            tokenizer.consumeCharacter((char)'/');
            String subtype = tokenizer.consumeToken((CharMatcher)TOKEN_MATCHER);
            ImmutableListMultimap.Builder<K, V> parameters = ImmutableListMultimap.builder();
            while (tokenizer.hasMore()) {
                String value;
                tokenizer.consumeTokenIfPresent((CharMatcher)LINEAR_WHITE_SPACE);
                tokenizer.consumeCharacter((char)';');
                tokenizer.consumeTokenIfPresent((CharMatcher)LINEAR_WHITE_SPACE);
                String attribute = tokenizer.consumeToken((CharMatcher)TOKEN_MATCHER);
                tokenizer.consumeCharacter((char)'=');
                if ('\"' != tokenizer.previewChar()) {
                    value = tokenizer.consumeToken((CharMatcher)TOKEN_MATCHER);
                } else {
                    tokenizer.consumeCharacter((char)'\"');
                    StringBuilder valueBuilder = new StringBuilder();
                    while ('\"' != tokenizer.previewChar()) {
                        if ('\\' == tokenizer.previewChar()) {
                            tokenizer.consumeCharacter((char)'\\');
                            valueBuilder.append((char)tokenizer.consumeCharacter((CharMatcher)CharMatcher.ascii()));
                            continue;
                        }
                        valueBuilder.append((String)tokenizer.consumeToken((CharMatcher)QUOTED_TEXT_MATCHER));
                    }
                    value = valueBuilder.toString();
                    tokenizer.consumeCharacter((char)'\"');
                }
                parameters.put((Object)attribute, (Object)value);
            }
            return MediaType.create((String)type, (String)subtype, (Multimap<String, String>)parameters.build());
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException((String)("Could not parse '" + input + "'"), (Throwable)e);
        }
    }

    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MediaType)) return false;
        MediaType that = (MediaType)obj;
        if (!this.type.equals((Object)that.type)) return false;
        if (!this.subtype.equals((Object)that.subtype)) return false;
        if (!this.parametersAsMap().equals(that.parametersAsMap())) return false;
        return true;
    }

    public int hashCode() {
        int h = this.hashCode;
        if (h != 0) return h;
        this.hashCode = h = Objects.hashCode((Object[])new Object[]{this.type, this.subtype, this.parametersAsMap()});
        return h;
    }

    public String toString() {
        String result = this.toString;
        if (result != null) return result;
        this.toString = result = this.computeToString();
        return result;
    }

    private String computeToString() {
        StringBuilder builder = new StringBuilder().append((String)this.type).append((char)'/').append((String)this.subtype);
        if (this.parameters.isEmpty()) return builder.toString();
        builder.append((String)"; ");
        ListMultimap<String, String> quotedParameters = Multimaps.transformValues(this.parameters, new Function<String, String>((MediaType)this){
            final /* synthetic */ MediaType this$0;
            {
                this.this$0 = mediaType;
            }

            public String apply(String value) {
                String string;
                if (MediaType.access$000().matchesAllOf((CharSequence)value)) {
                    string = value;
                    return string;
                }
                string = MediaType.access$100((String)value);
                return string;
            }
        });
        PARAMETER_JOINER.appendTo((StringBuilder)builder, quotedParameters.entries());
        return builder.toString();
    }

    private static String escapeAndQuote(String value) {
        StringBuilder escaped = new StringBuilder((int)(value.length() + 16)).append((char)'\"');
        int i = 0;
        while (i < value.length()) {
            char ch = value.charAt((int)i);
            if (ch == '\r' || ch == '\\' || ch == '\"') {
                escaped.append((char)'\\');
            }
            escaped.append((char)ch);
            ++i;
        }
        return escaped.append((char)'\"').toString();
    }

    static /* synthetic */ CharMatcher access$000() {
        return TOKEN_MATCHER;
    }

    static /* synthetic */ String access$100(String x0) {
        return MediaType.escapeAndQuote((String)x0);
    }
}

