/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.JDBC4MysqlSQLXML;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public class JDBC4MysqlSQLXML
implements SQLXML {
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;
    private String stringRep;
    private ResultSetInternalMethods owningResultSet;
    private int columnIndexOfXml;
    private boolean fromResultSet;
    private boolean isClosed = false;
    private boolean workingWithResult;
    private DOMResult asDOMResult;
    private SAXResult asSAXResult;
    private SimpleSaxToReader saxToReaderConverter;
    private StringWriter asStringWriter;
    private ByteArrayOutputStream asByteArrayOutputStream;
    private ExceptionInterceptor exceptionInterceptor;

    protected JDBC4MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor) {
        this.owningResultSet = owner;
        this.columnIndexOfXml = index;
        this.fromResultSet = true;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    protected JDBC4MysqlSQLXML(ExceptionInterceptor exceptionInterceptor) {
        this.fromResultSet = false;
        this.exceptionInterceptor = exceptionInterceptor;
    }

    @Override
    public synchronized void free() throws SQLException {
        this.stringRep = null;
        this.asDOMResult = null;
        this.asSAXResult = null;
        this.inputFactory = null;
        this.outputFactory = null;
        this.owningResultSet = null;
        this.workingWithResult = false;
        this.isClosed = true;
    }

    @Override
    public synchronized String getString() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        if (!this.fromResultSet) return this.stringRep;
        return this.owningResultSet.getString((int)this.columnIndexOfXml);
    }

    private synchronized void checkClosed() throws SQLException {
        if (!this.isClosed) return;
        throw SQLError.createSQLException((String)"SQLXMLInstance has been free()d", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    private synchronized void checkWorkingWithResult() throws SQLException {
        if (!this.workingWithResult) return;
        throw SQLError.createSQLException((String)"Can't perform requested operation after getResult() has been called to write XML data", (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
    }

    @Override
    public synchronized void setString(String str) throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        this.stringRep = str;
        this.fromResultSet = false;
    }

    public synchronized boolean isEmpty() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        if (this.fromResultSet) return false;
        if (this.stringRep == null) return true;
        if (this.stringRep.length() == 0) return true;
        return false;
    }

    @Override
    public synchronized InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        return this.owningResultSet.getBinaryStream((int)this.columnIndexOfXml);
    }

    @Override
    public synchronized Reader getCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        return this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml);
    }

    @Override
    public synchronized <T extends Source> T getSource(Class<T> clazz) throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        if (clazz == null || clazz.equals(SAXSource.class)) {
            InputSource inputSource = null;
            if (this.fromResultSet) {
                inputSource = new InputSource((Reader)this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml));
                return (T)((T)new SAXSource((InputSource)inputSource));
            }
            inputSource = new InputSource((Reader)new StringReader((String)this.stringRep));
            return (T)new SAXSource((InputSource)inputSource);
        }
        if (clazz.equals(DOMSource.class)) {
            try {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setNamespaceAware((boolean)true);
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                InputSource inputSource = null;
                if (this.fromResultSet) {
                    inputSource = new InputSource((Reader)this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml));
                    return (T)((T)new DOMSource((Node)builder.parse((InputSource)inputSource)));
                }
                inputSource = new InputSource((Reader)new StringReader((String)this.stringRep));
                return (T)new DOMSource((Node)builder.parse((InputSource)inputSource));
            }
            catch (Throwable t) {
                SQLException sqlEx = SQLError.createSQLException((String)t.getMessage(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
                sqlEx.initCause((Throwable)t);
                throw sqlEx;
            }
        }
        if (clazz.equals(StreamSource.class)) {
            Reader reader = null;
            if (this.fromResultSet) {
                reader = this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml);
                return (T)((T)new StreamSource((Reader)reader));
            }
            reader = new StringReader((String)this.stringRep);
            return (T)new StreamSource((Reader)reader);
        }
        if (!clazz.equals(StAXSource.class)) throw SQLError.createSQLException((String)("XML Source of type \"" + clazz.toString() + "\" Not supported."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        try {
            Reader reader = null;
            if (this.fromResultSet) {
                reader = this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml);
                return (T)((T)new StAXSource((XMLStreamReader)this.inputFactory.createXMLStreamReader((Reader)reader)));
            }
            reader = new StringReader((String)this.stringRep);
            return (T)new StAXSource((XMLStreamReader)this.inputFactory.createXMLStreamReader((Reader)reader));
        }
        catch (XMLStreamException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.getMessage(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    @Override
    public synchronized OutputStream setBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        this.workingWithResult = true;
        return this.setBinaryStreamInternal();
    }

    private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
        this.asByteArrayOutputStream = new ByteArrayOutputStream();
        return this.asByteArrayOutputStream;
    }

    @Override
    public synchronized Writer setCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        this.workingWithResult = true;
        return this.setCharacterStreamInternal();
    }

    private synchronized Writer setCharacterStreamInternal() throws SQLException {
        this.asStringWriter = new StringWriter();
        return this.asStringWriter;
    }

    @Override
    public synchronized <T extends Result> T setResult(Class<T> clazz) throws SQLException {
        this.checkClosed();
        this.checkWorkingWithResult();
        this.workingWithResult = true;
        this.asDOMResult = null;
        this.asSAXResult = null;
        this.saxToReaderConverter = null;
        this.stringRep = null;
        this.asStringWriter = null;
        this.asByteArrayOutputStream = null;
        if (clazz == null || clazz.equals(SAXResult.class)) {
            this.saxToReaderConverter = new SimpleSaxToReader((JDBC4MysqlSQLXML)this);
            this.asSAXResult = new SAXResult((ContentHandler)this.saxToReaderConverter);
            return (T)this.asSAXResult;
        }
        if (clazz.equals(DOMResult.class)) {
            this.asDOMResult = new DOMResult();
            return (T)this.asDOMResult;
        }
        if (clazz.equals(StreamResult.class)) {
            return (T)new StreamResult((Writer)this.setCharacterStreamInternal());
        }
        if (!clazz.equals(StAXResult.class)) throw SQLError.createSQLException((String)("XML Result of type \"" + clazz.toString() + "\" Not supported."), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
        try {
            if (this.outputFactory != null) return (T)new StAXResult((XMLEventWriter)this.outputFactory.createXMLEventWriter((Writer)this.setCharacterStreamInternal()));
            this.outputFactory = XMLOutputFactory.newInstance();
            return (T)new StAXResult((XMLEventWriter)this.outputFactory.createXMLEventWriter((Writer)this.setCharacterStreamInternal()));
        }
        catch (XMLStreamException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.getMessage(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out) {
        try {
            String encoding = "UTF-8";
            try {
                ByteArrayInputStream bIn = new ByteArrayInputStream((byte[])out.toByteArray());
                XMLStreamReader reader = this.inputFactory.createXMLStreamReader((InputStream)bIn);
                int eventType = 0;
                do {
                    if ((eventType = reader.next()) == 8) return new StringReader((String)new String((byte[])out.toByteArray(), (String)encoding));
                } while (eventType != 7);
                String possibleEncoding = reader.getEncoding();
                if (possibleEncoding == null) return new StringReader((String)new String((byte[])out.toByteArray(), (String)encoding));
                encoding = possibleEncoding;
                return new StringReader((String)new String((byte[])out.toByteArray(), (String)encoding));
            }
            catch (Throwable bIn) {
                // empty catch block
            }
            return new StringReader((String)new String((byte[])out.toByteArray(), (String)encoding));
        }
        catch (UnsupportedEncodingException badEnc) {
            throw new RuntimeException((Throwable)badEnc);
        }
    }

    protected String readerToString(Reader reader) throws SQLException {
        StringBuilder buf = new StringBuilder();
        int charsRead = 0;
        char[] charBuf = new char[512];
        try {
            while ((charsRead = reader.read((char[])charBuf)) != -1) {
                buf.append((char[])charBuf, (int)0, (int)charsRead);
            }
            return buf.toString();
        }
        catch (IOException ioEx) {
            SQLException sqlEx = SQLError.createSQLException((String)ioEx.getMessage(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)ioEx);
            throw sqlEx;
        }
    }

    protected synchronized Reader serializeAsCharacterStream() throws SQLException {
        this.checkClosed();
        if (!this.workingWithResult) return this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml);
        if (this.stringRep != null) {
            return new StringReader((String)this.stringRep);
        }
        if (this.asDOMResult != null) {
            return new StringReader((String)this.domSourceToString());
        }
        if (this.asStringWriter != null) {
            return new StringReader((String)this.asStringWriter.toString());
        }
        if (this.asSAXResult != null) {
            return this.saxToReaderConverter.toReader();
        }
        if (this.asByteArrayOutputStream == null) return this.owningResultSet.getCharacterStream((int)this.columnIndexOfXml);
        return this.binaryInputStreamStreamToReader((ByteArrayOutputStream)this.asByteArrayOutputStream);
    }

    protected String domSourceToString() throws SQLException {
        try {
            DOMSource source = new DOMSource((Node)this.asDOMResult.getNode());
            Transformer identity = TransformerFactory.newInstance().newTransformer();
            StringWriter stringOut = new StringWriter();
            StreamResult result = new StreamResult((Writer)stringOut);
            identity.transform((Source)source, (Result)result);
            return stringOut.toString();
        }
        catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException((String)t.getMessage(), (String)"S1009", (ExceptionInterceptor)this.exceptionInterceptor);
            sqlEx.initCause((Throwable)t);
            throw sqlEx;
        }
    }

    protected synchronized String serializeAsString() throws SQLException {
        this.checkClosed();
        if (!this.workingWithResult) return this.owningResultSet.getString((int)this.columnIndexOfXml);
        if (this.stringRep != null) {
            return this.stringRep;
        }
        if (this.asDOMResult != null) {
            return this.domSourceToString();
        }
        if (this.asStringWriter != null) {
            return this.asStringWriter.toString();
        }
        if (this.asSAXResult != null) {
            return this.readerToString((Reader)this.saxToReaderConverter.toReader());
        }
        if (this.asByteArrayOutputStream == null) return this.owningResultSet.getString((int)this.columnIndexOfXml);
        return this.readerToString((Reader)this.binaryInputStreamStreamToReader((ByteArrayOutputStream)this.asByteArrayOutputStream));
    }
}

